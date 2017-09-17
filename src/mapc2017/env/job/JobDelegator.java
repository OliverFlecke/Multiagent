package mapc2017.env.job;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import cartago.AgentId;
import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import mapc2017.data.JobStatistics;
import mapc2017.data.facility.Facility;
import mapc2017.data.facility.Shop;
import mapc2017.data.item.ItemList;
import mapc2017.data.item.ShoppingList;
import mapc2017.data.job.AuctionJob;
import mapc2017.data.job.Job;
import mapc2017.data.job.MissionJob;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.DynamicInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;

public class JobDelegator extends Artifact {
	
	private static JobDelegator instance;	
	public  static JobDelegator get() { return instance; }
	
	private Map<AgentInfo, AgentId> agentIds 	= new HashMap<>();
	private LinkedList<AgentInfo> 	freeAgents 	= new LinkedList<>();
	
	private Map<String, Set<AgentInfo>>	taskToAgents	= new HashMap<>();
	private Map<AgentInfo, String> 		agentToTask 	= new HashMap<>();

	private DynamicInfo 	dInfo;
	private FacilityInfo 	fInfo;
	private ItemInfo		iInfo;
	private StaticInfo 		sInfo;
	
	void init() 
	{
		instance = this;
		
		dInfo = DynamicInfo	.get();
		fInfo = FacilityInfo.get();
		iInfo = ItemInfo	.get();
		sInfo = StaticInfo	.get();
	}
	
	public void select(PriorityQueue<JobEvaluation> evals)
	{
		if (evals.isEmpty() || freeAgents.isEmpty()) return;
		
		synchronized (freeAgents) 
		{
			// Removes duplicates
			Set<AgentInfo> 	distinctAgents 	= new HashSet<>(freeAgents);			
							freeAgents 		= new LinkedList<>(distinctAgents);
			// Sort agents by capacity
			Collections.sort(freeAgents, Comparator.comparingInt(AgentInfo::getCapacity));	
			System.out.println("[JobDelegator] Free: " + freeAgents);		
		}
		
		int maxSteps 	= sInfo.getSteps();
		int currentStep = dInfo.getStep();
		
		Iterator<JobEvaluation> it = evals.iterator();
		
		while (it.hasNext())
		{
			JobEvaluation 	eval = it.next();
			Job				job	 = eval.getJob();
			
			int stepComplete = eval.getSteps() + currentStep;

			if (stepComplete < maxSteps && stepComplete < job.getEnd())
			{
				if (eval.getReqAgents() > freeAgents.size()) continue;
				
				 	 if (job instanceof MissionJob) { if (!delegate(eval)) continue; }
				else if (job instanceof AuctionJob) 
				{
					AuctionJob auction = (AuctionJob) job;
					
					if (auction.hasWon())
					{
						JobStatistics.auctionWon(auction);
						if (!delegate(eval)) return;
					}
					else if (!auction.isHighestBidder() && eval.getReqAgents() <= freeAgents.size())
					{
						execInternalOp("bidForAuction", auction);
						continue;
					}
					else continue;
				}
				else if (!delegate(eval)) continue;
			}
			JobStatistics.addJobEvaluation(eval);
			it.remove();				
		}
	}
	
	private boolean delegate(JobEvaluation eval) 
	{		
		Job job = eval.getJob();
		
		// Make a copy of freeAgents to prevent removing agents before assigning them
		LinkedList<AgentInfo> 			agents 		= new LinkedList<>();
		// To prevent ArrayOutOfBounds addAll
		synchronized (freeAgents) {	agents.addAll(freeAgents); }
		// Maps agents to their workload
		Map<AgentInfo, ItemList> 		assemblers 	= new HashMap<>();
		Map<AgentInfo, ShoppingList> 	retrievers 	= new HashMap<>();
		// Maps retrievers to the name of the assembler
		Map<AgentInfo, String>			assistants 	= new HashMap<>();		
		
		ItemList itemsToAssemble = job.getItems();		
		
		while (!itemsToAssemble.isEmpty())
		{
			if (agents.isEmpty()) return false;
			
			// Find best suited agent to assemble items based on their volume
			AgentInfo assembler = getAssembler(agents, iInfo.getBaseVolume(itemsToAssemble));
			
			agents.remove(assembler);
			
			// Find actual items to deliver
			ItemList toAssemble = assembler.getItemsToCarry(itemsToAssemble);
			
			if (toAssemble.isEmpty()) return false;
			
			itemsToAssemble.subtract(toAssemble);
			
			assemblers.put(assembler, toAssemble);
			
			// Create shopping list for the given items
			ShoppingList 	shoppingList  = ShoppingList.getShoppingList(toAssemble);
			String 			assemblerShop = getShop(assembler, shoppingList);
			
			ItemList assemblerRetrieve = assembler.getItemsToCarry(shoppingList.get(assemblerShop));
			
			shoppingList.get(assemblerShop).subtract(assemblerRetrieve);
			
			retrievers.put(assembler, new ShoppingList(assemblerShop, assemblerRetrieve));

			for (String shop : shoppingList.keySet())
			{
				ItemList itemsToRetrieve = shoppingList.get(shop);
				
				while (!itemsToRetrieve.isEmpty())
				{
					if (agents.isEmpty()) return false;

					// Find best suited agent to retrieve items
					AgentInfo retriever = getRetriever(agents, shop, itemsToRetrieve);
					
					agents.remove(retriever);
					
					ItemList toRetrieve = retriever.getItemsToCarry(itemsToRetrieve);
					
					if (toRetrieve.isEmpty()) return false;
					
					itemsToRetrieve.subtract(toRetrieve);
					
					retrievers.put(retriever, new ShoppingList(shop, toRetrieve));
					
					assistants.put(retriever, assembler.getName());
				}
			}
		}
		
		retrievers.keySet().stream().forEach(freeAgents::remove);
		retrievers.keySet().stream().forEach(agent -> agentToTask.put(agent, job.getId()));
		retrievers.values().stream().forEach(shoppingList -> shoppingList.entrySet().forEach(entry -> {
			Shop shop = (Shop) fInfo.getFacility(entry.getKey());			
			for (Entry<String, Integer> item : entry.getValue().entrySet())
				shop.addReserved(item.getKey(), item.getValue());
		}));
		
		taskToAgents.put(job.getId(), retrievers.keySet());
		
		execInternalOp("assign", assemblers, retrievers, assistants, job, eval);
		
		JobStatistics.startJob(job, dInfo.getStep());
		
		return true;
	}
	
	private AgentInfo getAssembler(LinkedList<AgentInfo> agents, int volume) {
		for (AgentInfo agent : agents)
			if (volume < agent.getCapacity())
				return agent;
		return agents.getLast();
	}
	
	private AgentInfo getRetriever(LinkedList<AgentInfo> agents, String shop, Map<String, Integer> items) {
		Facility facility = fInfo.getFacility(shop);
		return agents.stream().min(Comparator.comparingInt(agent -> {
			int steps = sInfo.getRouteDuration(agent, facility.getLocation());
			int volume = agent.getVolumeToCarry(items);
			return steps - volume;
		})).get();
	}
	
	private String getShop(AgentInfo agent, ShoppingList shoppingList) {
		return shoppingList.entrySet().stream().min(Comparator.comparingInt(e -> {
			int steps = sInfo.getRouteDuration(agent, fInfo.getFacility(e.getKey()).getLocation());
			int volume = agent.getVolumeToCarry(e.getValue());
			return steps - volume;
		})).get().getKey();
	}
	
	@OPERATION
	void free()
	{
		AgentInfo agent = AgentInfo.get(getOpUserName());
		
		freeAgents.add(agent);
		
		agentToTask.put(agent, "");
		
		agentIds.put(agent, getOpUserId());
	}
	
	@INTERNAL_OPERATION
	void assign(Map<AgentInfo, ItemList> assemblers, Map<AgentInfo, ShoppingList> retrievers, Map<AgentInfo, String> assistants, Job job, JobEvaluation eval)
	{
		for (AgentInfo agent : assemblers.keySet())
			signal(agentIds.get(agent), "task", job.getId(), assemblers.get(agent), job.getStorage(), retrievers.get(agent), eval.getWorkshop());

		for (AgentInfo agent : assistants.keySet())
			signal(agentIds.get(agent), "task", assistants.get(agent), retrievers.get(agent), eval.getWorkshop());
	}
	
	@INTERNAL_OPERATION
	private void bidForAuction(AuctionJob auction) 
	{
		int bid = auction.getReward() - 1;
		
		AgentInfo agent = freeAgents.removeFirst();
		
		agentToTask.put(agent, "bid");
		
		signal(agentIds.get(agent), "task", auction.getId(), bid);
		
		JobStatistics.bidOnAuction(auction);
	}
	
	@INTERNAL_OPERATION
	void release(String job)
	{
		if (taskToAgents.get(job) == null) return;
		
		for (AgentInfo agent : taskToAgents.get(job))
		{
			if (agentToTask.get(agent).equals(job)) 
			{
				signal(agentIds.get(agent), "task", "release");
			}
		}
	}
	
	public void releaseAgents(Job job) {
		execInternalOp("release", job.getId());
	}
}
