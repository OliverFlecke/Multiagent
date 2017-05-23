package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cnp.TaskArtifact;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import jason.asSyntax.Term;
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
		items.set(job.getRequiredItems().toItemAmountData().stream()
				.collect(Collectors.toMap(x -> x.getName(), x -> x.getAmount())));
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
			
			for (Entry<String, Integer> entry : ItemArtifact.getBaseItem(itemData.getName()).entrySet())
			{
				Item item = ItemArtifact.getItem(entry.getKey());
				currentPrice += item.getValue() * entry.getValue();
			}
			
			price += currentPrice * itemData.getAmount();
		}
		return price;
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
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String id 			= Translator.termToString(args[0]);
		String storageId	= Translator.termToString(args[1]);
		int reward 			= Translator.termToInteger(args[2]);
		int start 			= Translator.termToInteger(args[3]);
		int end 			= Translator.termToInteger(args[4]);
		int fine			= Translator.termToInteger(args[5]);
		int bid 			= Translator.termToInteger(args[6]);
		int time			= Translator.termToInteger(args[7]);
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		AuctionJob auction = new AuctionJob(reward, storage, start, end, time, fine);
		
		auction.bid(null, bid);

		for (Term partArg : Translator.termToTermList(args[8]))
		{
			List<Term> partTuple = Translator.literalToTermList(partArg);
			
			String itemId   = Translator.termToString(partTuple.get(0));
			int    quantity = Translator.termToInteger(partTuple.get(1));
			
			auction.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		
		auctions.put(id, auction);
	}
	
	private static void perceiveJob(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String id 			= Translator.termToString(args[0]);
		String storageId	= Translator.termToString(args[1]);
		int reward 			= Translator.termToInteger(args[2]);
		int start 			= Translator.termToInteger(args[3]);
		int end 			= Translator.termToInteger(args[4]);
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Job job = new Job(reward, storage, start, end, "");

		for (Term partArg : Translator.termToTermList(args[5]))
		{
			List<Term> partTuple = Translator.literalToTermList(partArg);
			
			String itemId   = Translator.termToString(partTuple.get(0));
			int    quantity = Translator.termToInteger(partTuple.get(1));
			
			job.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		
		if (!jobs.containsKey(id))
			TaskArtifact.announce(id);
		jobs.put(id, job); 
	}
	
	private static void perceiveMission(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String id 			= Translator.termToString(args[0]);
		String storageId	= Translator.termToString(args[1]);
		int reward 			= Translator.termToInteger(args[2]);
		int start 			= Translator.termToInteger(args[3]);
		int end 			= Translator.termToInteger(args[4]);
		int fine			= Translator.termToInteger(args[5]);
		int bid 			= Translator.termToInteger(args[6]);
//		int time			= Translator.termToInteger(args[7]);
		String mId			= Translator.termToString(args[8]);
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Mission mission = new Mission(reward, storage, start, end, fine, null, mId);
		
		mission.bid(null, bid);

		for (Term partArg : Translator.termToTermList(args[9]))
		{
			List<Term> partTuple = Translator.literalToTermList(partArg);
			
			String itemId   = Translator.termToString(partTuple.get(0));
			int    quantity = Translator.termToInteger(partTuple.get(1));
			
			mission.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		
		missions.put(id, mission);
	}
	
	private static void perceivePosted(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String id 			= Translator.termToString(args[0]);
		String storageId	= Translator.termToString(args[1]);
		int reward 			= Translator.termToInteger(args[2]);
		int start 			= Translator.termToInteger(args[3]);
		int end 			= Translator.termToInteger(args[4]);
		
		Storage storage = (Storage) FacilityArtifact.getFacility(FacilityArtifact.STORAGE, storageId);
		
		Job job = new Job(reward, storage, start, end, "");

		for (Term partArg : Translator.termToTermList(args[5]))
		{
			List<Term> partTuple = Translator.literalToTermList(partArg);
			
			String itemId   = Translator.termToString(partTuple.get(0));
			int    quantity = Translator.termToInteger(partTuple.get(1));
			
			job.addRequiredItem(ItemArtifact.getItem(itemId), quantity);
		}
		
		postedJobs.put(id, job);
	}
}
