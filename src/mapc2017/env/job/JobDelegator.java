package mapc2017.env.job;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

import cartago.AgentId;
import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import mapc2017.data.ItemList;
import mapc2017.data.ShoppingList;
import mapc2017.data.job.Job;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.DynamicInfo;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;

public class JobDelegator extends Artifact {
	
	private static JobDelegator instance;	
	public  static JobDelegator get() { return instance; }
	
	private Map<AgentInfo, AgentId> agentIds 	= new HashMap<>();
	private LinkedList<AgentInfo> 	freeAgents 	= new LinkedList<>();
	
	private DynamicInfo dInfo;
	private ItemInfo	iInfo;
	private StaticInfo 	sInfo;
	
	void init() 
	{
		instance = this;
		
		dInfo = DynamicInfo	.get();
		iInfo = ItemInfo	.get();
		sInfo = StaticInfo	.get();
	}
	
	public void select(PriorityQueue<JobEvaluation> evals)
	{
		if (evals.isEmpty()) return;
		
		Collections.sort(freeAgents, Comparator.comparingInt(AgentInfo::getCapacity));

		int maxSteps 	= sInfo.getSteps();
		int currentStep = dInfo.getStep();
		
		Iterator<JobEvaluation> it = evals.iterator();
		
		while (it.hasNext())
		{
			JobEvaluation eval = it.next();	
			
			int stepComplete = eval.getSteps() + currentStep;

			if (stepComplete > maxSteps ||
				stepComplete > eval.getJob().getEnd())
			{
				it.remove();
			}
			else if (eval.getReqAgents() < agentIds.size())
			{
				if (delegate(eval)) it.remove();
				else				eval.incReqAgents();
			}
		}
	}
	
	private boolean delegate(JobEvaluation eval) 
	{
		Job job = eval.getJob();
		
		Map<AgentInfo, ItemList> 		assemblers = new HashMap<>();
		Map<AgentInfo, ShoppingList> 	retrievers = new HashMap<>();
		Map<AgentInfo, String>			assistants = new HashMap<>();
		
		Map<String, Integer> itemsToAssemble = job.getItems();		
		
		while (!itemsToAssemble.isEmpty())
		{
			AgentInfo assembler = getAgent(iInfo.getBaseVolume(itemsToAssemble));
			
			if (assembler == null)
			{
				return false;
			}
			else
			{
				freeAgents.remove(assembler);
			}
			
			assemblers.put(assembler, ItemList.getItemsToCarry(itemsToAssemble, assembler.getCapacity()));
			
			ShoppingList 	shoppingList = ShoppingList.getShoppingList(assemblers.get(assembler));			
			String 			maxShop 	 = shoppingList.getMaxVolumeShop(iInfo);
			
			retrievers.put(assembler, new ShoppingList(maxShop, shoppingList.remove(maxShop)));

			for (String shop : shoppingList.keySet())
			{
				Map<String, Integer> itemsToRetrieve = shoppingList.get(shop);
				
				while (!itemsToRetrieve.isEmpty())
				{
					AgentInfo retriever = getAgent(iInfo.getVolume(itemsToRetrieve));
					
					if (retriever == null)
					{
						return false;
					}
					else
					{
						freeAgents.remove(retriever);
					}
					
					retrievers.put(retriever, new ShoppingList(shop, 
							ItemList.getItemsToCarry(itemsToRetrieve, retriever.getCapacity())));
					
					assistants.put(retriever, assembler.getName());
				}
			}
		}
		execInternalOp("update", assemblers, retrievers, assistants, job, eval);
		
		return true;
	}
	
	private AgentInfo getAgent(int volume) {
		for (AgentInfo agent : freeAgents)
			if (volume < agent.getCapacity())
				return agent;
		return freeAgents.isEmpty() ? null : freeAgents.getLast();
	}
	
	@OPERATION
	void free()
	{
		AgentInfo agent = AgentInfo.get(getOpUserName());
		
		freeAgents.add(agent);
		
		agentIds.put(agent, getOpUserId());
	}
	
	@INTERNAL_OPERATION
	void assign(AgentInfo agent, Object... args)
	{
		signal(agentIds.get(agent), "job", args);
	}
	
	@INTERNAL_OPERATION
	void update(Map<AgentInfo, ItemList> assemblers, Map<AgentInfo, ShoppingList> retrievers, Map<AgentInfo, String> assistants, Job job, JobEvaluation eval)
	{
		for (AgentInfo agent : assemblers.keySet())
		{
			assign(agent, job.getId(), assemblers.get(agent), job.getStorage(), retrievers.get(agent), eval.getWorkshop());
		}

		for (AgentInfo agent : assistants.keySet())
		{
			assign(agent, assistants.get(agent), retrievers.get(agent), eval.getWorkshop());
		}
	}
}
