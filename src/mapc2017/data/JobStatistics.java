package mapc2017.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mapc2017.data.job.AuctionJob;
import mapc2017.data.job.Job;
import mapc2017.data.job.MissionJob;

public class JobStatistics 
{
	private static Set<Job> 		jobsTotal 			= new HashSet<>();
	private static Set<Job> 		jobsStarted 		= new HashSet<>();
	private static Set<Job>			jobsCompleted 		= new HashSet<>();
	
	private static Set<AuctionJob>  auctionsTotal 		= new HashSet<>();
	private static Set<AuctionJob>  auctionsStarted 	= new HashSet<>();
	private static Set<AuctionJob> 	auctionsCompleted 	= new HashSet<>();
	private static List<AuctionJob>	auctionsBidOn 		= new LinkedList<>();
	private static Set<AuctionJob> 	auctionsWon			= new HashSet<>();
		
	private static Set<MissionJob> 	missionsTotal		= new HashSet<>();
	private static Set<MissionJob>  missionsStarted 	= new HashSet<>();
	private static Set<MissionJob> 	missionsCompleted	= new HashSet<>();

	/**
	 * Methods to change the state of jobs. 
	 */
	public static void addJob(Job job)
	{
			 if (job instanceof MissionJob) missionsTotal.add((MissionJob) job);
		else if (job instanceof AuctionJob) auctionsTotal.add((AuctionJob) job);
		else 								jobsTotal.add(job);
	}

	public static void startJob(Job job)
	{
			 if (job instanceof MissionJob) missionsStarted.add((MissionJob) job);
		else if (job instanceof AuctionJob) auctionsStarted.add((AuctionJob) job);
		else 								jobsStarted.add(job);
	}

	public static void completeJob(Job job)
	{
			 if (job instanceof MissionJob) missionsCompleted.add((MissionJob) job);
		else if (job instanceof AuctionJob) auctionsCompleted.add((AuctionJob) job);
		else 								jobsCompleted.add(job);
	}
	
	public static void bidOnAuction(AuctionJob auction)
	{
		auctionsBidOn.add(auction);
	}

	public static void auctionWon(AuctionJob auction)
	{
		auctionsWon.add(auction);
	}


	/** 
	 * Getters for the different statistics 
	 */

	public static int getTotalJobs()
	{
		return jobsTotal.size() + auctionsTotal.size() + missionsTotal.size();
	}

	public static int getTotalJobsStarted()
	{
		return jobsStarted.size() + auctionsStarted.size() + missionsStarted.size();
	}

	public static int getTotalJobsCompleted()
	{
		return jobsCompleted.size() + auctionsCompleted.size() + missionsCompleted.size();
	}

	public static int getActiveJobs()
	{
		Set<Job> active = new HashSet<>(jobsStarted);
		active.addAll(auctionsStarted);
		active.addAll(missionsStarted);

		active.removeAll(jobsCompleted);
		active.removeAll(auctionsCompleted);
		active.removeAll(missionsCompleted);

		active.removeIf(j -> j.deadlinePassed());

		return active.size();
	}

	/**
	 * Auction specific statistics
	 */
	public static int getTotalAuctions()
	{
		return auctionsTotal.size();
	}

	public static int getAuctionsBidOn()
	{
		return auctionsBidOn.size();
	}

	public static int getAuctionsWon()
	{
		return auctionsWon.size();
	}

	public static int getAuctionsStarted()
	{
		return auctionsStarted.size();
	}

	public static int getAuctionsCompleted()
	{
		return auctionsCompleted.size();
	}

	public static int getAuctionsFailed()
	{
		Set<AuctionJob> auctionsFailed = new HashSet<>(auctionsWon);
		auctionsFailed.removeAll(auctionsCompleted);
		auctionsFailed.removeIf(a -> !a.deadlinePassed());
		return auctionsFailed.size();
	}

	/**
	 * Mission specific statistics
	 */

	public static int getTotalMissions()
	{
		return missionsTotal.size();
	}

	public static int getMissionsStarted()
	{
		return missionsStarted.size();
	}
	
	public static int getMissionsCompleted()
	{
		return missionsCompleted.size();
	}

	public static int getMissionsFailed()
	{
		Set<MissionJob> missionsFailed = new HashSet<>(missionsTotal);
		missionsFailed.removeAll(missionsCompleted);
		missionsFailed.removeIf(m -> !m.deadlinePassed());
		return missionsFailed.size();
	}

}
