package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cnp.TaskArtifact;
import data.CUtil;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import massim.protocol.scenario.city.data.ItemAmountData;
import massim.scenario.city.data.AuctionJob;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Job;
import massim.scenario.city.data.Mission;
import massim.scenario.city.data.facilities.Storage;

public class JobArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(JobArtifact.class.getName());

	private static final String AUCTION = "auction";
	private static final String JOB 	= "job";
	private static final String MISSION = "mission";
	private static final String POSTED 	= "posted";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(AUCTION, JOB, MISSION, POSTED)));

	private static Map<String, AuctionJob> 	auctions 	= new HashMap<>();
	private static Map<String, Job> 		jobs 		= new HashMap<>();
	private static Map<String, Mission> 	missions 	= new HashMap<>();
	private static Map<String, Job> 		postedJobs 	= new HashMap<>();
	
	@OPERATION
	void getJob(String jobId, OpFeedbackParam<String> storage, 
			OpFeedbackParam<Object> items)
	{
		Job job = jobs.get(jobId);
		
		storage.set(job.getStorage().getName());

		items.set(CUtil.extractItems(job));
	}
	
	/**
	 * Computes the price to by all the necessary items needed to complete this job
	 * @param job
	 * @return The price to buy all the items needed for the job
	 */
	public static int priceForItems(Job job)
	{
		int price = 0;
		for (ItemAmountData itemData : job.getRequiredItems().toItemAmountData())
		{
			int currentPrice = 0;
			
			for (Entry<Item, Integer> entry : ItemArtifact.getBaseItems(itemData.getName()).entrySet())
			{				
				currentPrice += ItemArtifact.itemPrice(entry.getKey()) * entry.getValue();
			}
			
			price += currentPrice * itemData.getAmount();
		}
		return price;
	}
	
	/**
	 * @param job
	 * @return The possible amount of money one can earn from this job
	 */
	public static int possibleEarning(Job job)
	{
		int price = priceForItems(job);
		
		// TODO: Add some estimate for the charge cost
		
		return job.getReward() - price;
	}
	
	public static void perceiveUpdate(Collection<Percept> percepts)
	{
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case AUCTION: perceiveAuction	(percept); break;
			case JOB:     perceiveJob		(percept); break;
			case MISSION: perceiveMission	(percept); break;
			case POSTED:  perceivePosted	(percept); break;
			}
		}

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info("Perceived jobs");
			logJobs("Auctions perceived:"	, auctions	.values());
			logJobs("Jobs perceived:"		, jobs		.values());
			logJobs("Missions perceived:"	, missions	.values());
			logJobs("Posted jobs perceived:", postedJobs.values());
		}		
	}
	
	private static void logJobs(String msg, Collection<? extends Job> jobs)
	{
		if (jobs.size() != 0)
			logger.info(msg);
			for (Job job : jobs)
				logger.info(job.toString());
	}
	
	private static void perceiveAuction(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		String 	id 			= (String) args[0];
		String 	storageId	= (String) args[1];
		int 	reward 		= (int)    args[2];
		int 	start 		= (int)    args[3];
		int 	end 		= (int)    args[4];
		int 	fine		= (int)    args[5];
		int 	bid 		= (int)    args[6];
		int 	time		= (int)    args[7];
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		AuctionJob auction = new AuctionJob(reward, storage, start, end, time, fine);
		
		auction.bid(null, bid);

		for (Object part : (Object[]) args[8])
		{
			Object[] partArgs = (Object[]) part;
			
			String 	itemId   = (String) partArgs[0];
			int    	quantity = (int)    partArgs[1];
			
			auction.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}

		if (!auctions.containsKey(id))
		{
			auctions.put(id, auction);
			TaskArtifact.announce(id, "auction");			
		}
		else auctions.put(id, auction);
	}
	
	private static void perceiveJob(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		String 	id 			= (String) args[0];
		String 	storageId	= (String) args[1];
		int 	reward 		= (int)    args[2];
		int 	start 		= (int)    args[3];
		int 	end 		= (int)    args[4];
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Job job = new Job(reward, storage, start, end, "");

		for (Object part : (Object[]) args[5])
		{
			Object[] partArgs = (Object[]) part;
			
			String 	itemId   = (String) partArgs[0];
			int    	quantity = (int)    partArgs[1];
			
			job.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		
		if (!jobs.containsKey(id))
		{
			jobs.put(id, job); 			
			TaskArtifact.announce(id, "job");
		}
		else jobs.put(id, job); 		
	}
	
	private static void perceiveMission(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		String 	id 			= (String) args[0];
		String 	storageId	= (String) args[1];
		int 	reward 		= (int)    args[2];
		int 	start 		= (int)    args[3];
		int 	end 		= (int)    args[4];
		int 	fine		= (int)    args[5];
		int 	bid 		= (int)    args[6];
//		int 	time		= (int)    args[7];
		String	mId			= (String) args[8];
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Mission mission = new Mission(reward, storage, start, end, fine, null, mId);
		
		mission.bid(null, bid);

		for (Object part : (Object[]) args[9])
		{
			Object[] partArgs = (Object[]) part;
			
			String itemId   = (String) partArgs[0];
			int    quantity = (int)    partArgs[1];
			
			mission.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		if (!missions.containsKey(id))
		{			
			missions.put(id, mission);
			TaskArtifact.announce(id, "mission");
		}
		else missions.put(id, mission);
		
	}
	
	private static void perceivePosted(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		String 	id 			= (String) args[0];
		String 	storageId	= (String) args[1];
		int 	reward 		= (int)    args[2];
		int 	start 		= (int)    args[3];
		int 	end 		= (int)    args[4];
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Job job = new Job(reward, storage, start, end, "");

		for (Object part : (Object[]) args[5])
		{
			Object[] partArgs = (Object[]) part;
			
			String 	itemId   = (String) partArgs[0];
			int    	quantity = (int)    partArgs[1];
			
			job.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}

		if (!postedJobs.containsKey(id))
		{
			postedJobs.put(id, job);
			TaskArtifact.announce(id, "postedJob");
		}
		else postedJobs.put(id, job);
	}
	
	public static Job getJob(String taskId)
	{
		if (jobs.containsKey(taskId))
		{
			return jobs.get(taskId);
		}
		else if (missions.containsKey(taskId))
		{
			return missions.get(taskId);
		}
		else if (auctions.containsKey(taskId))
		{
			return auctions.get(taskId);
		}
		else
		{
			return postedJobs.get(taskId);			
		}
		
	}

	/**
	 * @return All the current jobs
	 */
	public static Collection<Job> getJobs() 
	{
		return jobs.values();
	}
}
