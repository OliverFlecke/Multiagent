package mapc2017.env.job;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import mapc2017.data.Role;
import mapc2017.data.facility.Facility;
import mapc2017.data.item.Item;
import mapc2017.data.item.ShoppingList;
import mapc2017.data.job.Job;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;
import massim.scenario.city.data.Location;

public class JobEvaluator implements Comparator<JobEvaluation> {
	
	private static JobEvaluator instance;	
	public  static JobEvaluator get() { return instance; }
	
	private PriorityQueue<JobEvaluation> 	evals 	= new PriorityQueue<>(this);
	private LinkedList<Role> 				roles;
	
	private Collection<AgentInfo> 	aInfos;
	private FacilityInfo			fInfo;
	private ItemInfo				iInfo;
	private StaticInfo 				sInfo;
	
	private Location 	center;
	private int			avgSpeed;
	
	public JobEvaluator() 
	{
		instance = this;
		
		aInfos 	= AgentInfo		.get();
		fInfo 	= FacilityInfo	.get();
		iInfo	= ItemInfo		.get();
		sInfo 	= StaticInfo	.get();
	}
	
	public void init()
	{		
		roles = sInfo.getRoles().stream()
				.sorted(Comparator.comparingInt(Role::getLoad))
				.collect(Collectors.toCollection(LinkedList::new));
		
		center = sInfo.getCenter();
		
		avgSpeed 	= (int) aInfos.stream()
							.map(AgentInfo::getRole)
							.mapToInt(Role::getSpeed)
							.average().getAsDouble();
	}
	
	public void evaluate(Job job)
	{
		int price = job.getItems().entrySet().stream()
						.mapToInt(Item::getAvgPrice).sum();
		
		int profit = job.getReward() - price;
				
		int 			baseVolume 		= iInfo.getBaseVolume(job.getItems());		
		ShoppingList 	shoppingList 	= ShoppingList.getShoppingList(job.getItems());
		
		int reqAssemblers	= getReqAgents(baseVolume);
		int reqAgents		= shoppingList.values().stream()
								.map(iInfo::getVolume)
								.mapToInt(this::getReqAgents)
								.sum();
		
		Facility workshop 	= fInfo.getFacilities(FacilityInfo.WORKSHOP).stream()
								.min(Comparator.comparingInt(f -> sInfo.getRouteLength(
									fInfo.getFacility(job.getStorage()).getLocation(), 
									f.getLocation()))).get();
		
		int maxDistance 	= shoppingList.keySet().stream().map(fInfo::getFacility)
								.mapToInt(shop -> sInfo.getRouteLength(center, shop.getLocation())
												+ sInfo.getRouteLength(shop.getLocation(), workshop.getLocation()))
								.max().getAsInt();
		
		int maxPurchases 	= shoppingList.values().stream().mapToInt(Map::size).max().getAsInt();
		
		int reqAssemblies 	= job.getItems().entrySet().stream()
								.mapToInt(e -> iInfo.getItem(e.getKey()).reqAssembly() ? e.getValue() : 0)
								.sum();
		
		int stepEstimate 	= maxDistance / avgSpeed + maxPurchases + reqAssemblies / reqAssemblers;
		
		evals.add(new JobEvaluation(job, profit, stepEstimate, reqAgents, workshop.getName(), shoppingList));
	}
	
	private int getReqAgents(int volume) {
		int agents = 0;		
		while (volume > 0) {
			Role role = getReqRole(volume);
			agents++;
			volume -= role.getLoad();
		}		
		return agents;
	}
	
	private Role getReqRole(int volume) {
		for (Role role : roles)
			if (volume < role.getLoad())
				return role;		
		return roles.getLast();
	}
	
	public PriorityQueue<JobEvaluation> getEvaluations() {
		return evals;
	}
	
	public void removeEvaluation(Job job) 
	{
		Iterator<JobEvaluation> it = evals.iterator();
		
		while (it.hasNext())
		{
			JobEvaluation eval = it.next();
			
			if (eval.getJob().getId().equals(job.getId()))
			{
				it.remove();
				return;
			}
		}
	}

	@Override
	public int compare(JobEvaluation o1, JobEvaluation o2) {
		return o1.getValue() - o2.getValue();
	}
}
