package mapc2017.env.info;

import java.util.HashMap;
import java.util.Map;

import cnp.TaskArtifact;
import mapc2017.data.job.*;

public class JobInfo {
	
	private static JobInfo instance;
	
	public JobInfo() {
		instance = this;
	}
	
	public static JobInfo get() {
		return instance;
	}

	private Map<String, AuctionJob> auctionJobs 	= new HashMap<>();
	private Map<String, SimpleJob> 	simpleJobs 		= new HashMap<>();
	private Map<String, MissionJob> missionJobs 	= new HashMap<>();
	private Map<String, PostedJob> 	postedJobs 		= new HashMap<>();

	public void addJob(Job job) 
	{
		if (getJob(job.getId()) == null) announceJob(job);
			
			 if (job instanceof SimpleJob ) simpleJobs .put(job.getId(), (SimpleJob ) job);
		else if (job instanceof MissionJob)	missionJobs.put(job.getId(), (MissionJob) job);
		else if (job instanceof AuctionJob)	auctionJobs.put(job.getId(), (AuctionJob) job);
		else if (job instanceof PostedJob ) postedJobs .put(job.getId(), (PostedJob ) job);
		else throw new UnsupportedOperationException("Unsupported job: " + job.getId());
	}
	
	public Job getJob(String jobId)
	{
			 if (simpleJobs .containsKey(jobId))	return simpleJobs .get(jobId);
		else if (missionJobs.containsKey(jobId))	return missionJobs.get(jobId);
		else if (auctionJobs.containsKey(jobId))	return auctionJobs.get(jobId);
		else										return postedJobs .get(jobId);	
	}
	
	public String getJobType(Job job)
	{
			 if (job instanceof SimpleJob ) return "job";
		else if (job instanceof MissionJob) return "mission";
		else if (job instanceof AuctionJob) return "auction";
		else 								return "posted";
	}
	
	private void announceJob(Job job)
	{
		TaskArtifact.invoke("announce", "task", 
				job.getId(), 
				job.getItems(), 
				job.getStorage(), 
				getJobType(job));
	}
}
