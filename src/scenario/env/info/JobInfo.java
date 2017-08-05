package scenario.env.info;

import java.util.HashMap;
import java.util.Map;

import scenario.data.job.*;

public class JobInfo {

	private Map<String, AuctionJob> auctionJobs 	= new HashMap<>();
	private Map<String, SimpleJob> 	simpleJobs 		= new HashMap<>();
	private Map<String, MissionJob> missionJobs 	= new HashMap<>();
	private Map<String, PostedJob> 	postedJobs 		= new HashMap<>();

	public void addJob(Job job) 
	{
		if (job instanceof AuctionJob)
		{
			auctionJobs.put(job.getId(), (AuctionJob) job);
		}
		else if (job instanceof SimpleJob)
		{
			simpleJobs.put(job.getId(), (SimpleJob) job);
		}
		else if (job instanceof MissionJob)
		{
			missionJobs.put(job.getId(), (MissionJob) job);
		}
		else if (job instanceof PostedJob)
		{
			postedJobs.put(job.getId(), (PostedJob) job);
		}
		else throw new UnsupportedOperationException("Unsupported job: " + job.getId());
	}
}
