package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cartago.OperationException;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import jason.asSyntax.Term;
import massim.scenario.city.data.AuctionJob;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Job;
import massim.scenario.city.data.Mission;
import massim.scenario.city.data.facilities.Storage;
import util.ArtifactUtil;

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
//		items.set(job.getRequiredItems().getStoredTypes().stream()
//				.map(x -> x.getName()).collect(Collectors.toList()));
		String[] itemNames = new String[job.getRequiredItems().getStoredTypes().size()];
		int i = 0;
		for (Item item : job.getRequiredItems().getStoredTypes())
		{
			itemNames[i++] = item.getName();
		}
		items.set(itemNames);
	}
	
	@LINK
	public void perceiveUpdate(Collection<Percept> allPercepts)
	{
		Collection<Percept> percepts = allPercepts.stream()
				.filter(percept -> PERCEPTS.contains(percept.getName()))
				.collect(Collectors.toList());
		
		percepts.forEach(percept -> execInternalOp(ArtifactUtil.perceive(percept), percept));

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info("Perceived jobs");
			logJobs("Auctions perceived:"	, auctions	.values());
			logJobs("Jobs perceived:"		, jobs		.values());
			logJobs("Missions perceived:"	, missions	.values());
			logJobs("Posted jobs perceived:", postedJobs.values());
		}		
	}
	
	@INTERNAL_OPERATION
	private void perceiveAuction(Percept percept)
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

	@INTERNAL_OPERATION
	private void perceiveJob(Percept percept) throws OperationException
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
			execLinkedOp(lookupArtifact("TaskArtifact"), "announce", id);
		jobs.put(id, job); 
	}

	@INTERNAL_OPERATION
	private void perceiveMission(Percept percept)
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

	@INTERNAL_OPERATION
	private void perceivePosted(Percept percept)
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
	
	private static void logJobs(String msg, Collection<? extends Job> jobs)
	{
		if (jobs.size() != 0)
			logger.info(msg);
		for (Job job : jobs)
			logger.info(job.toString());
	}
}
