package mapc2017.env.info;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mapc2017.data.job.AuctionJob;
import mapc2017.data.job.Job;
import mapc2017.data.job.MissionJob;
import mapc2017.data.job.PostedJob;
import mapc2017.data.job.SimpleJob;

public class JobInfo {
	
	private static JobInfo instance;
	public  static JobInfo get() { return instance; }

	private Map<String, AuctionJob> auctionJobs 	= new HashMap<>();
	private Map<String, SimpleJob> 	simpleJobs 		= new HashMap<>();
	private Map<String, MissionJob> missionJobs 	= new HashMap<>();
	private Map<String, PostedJob> 	postedJobs 		= new HashMap<>();	
	private Set<Job> 				newJobs 		= new HashSet<>();
	
	public JobInfo() {
		instance = this;
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public synchronized Job getJob(String jobId) {
			 if (simpleJobs .containsKey(jobId))	return simpleJobs .get(jobId);
		else if (missionJobs.containsKey(jobId))	return missionJobs.get(jobId);
		else if (auctionJobs.containsKey(jobId))	return auctionJobs.get(jobId);
		else										return postedJobs .get(jobId);	
	}
	
	public synchronized Set<Job> getNewJobs() {
		Set<Job> jobs = new HashSet<>(newJobs);		
		newJobs.clear();		
		return jobs;
	}
	
	/////////////
	// SETTERS //
	/////////////

	public synchronized void addJob(Job job) {
		if (job.getItems().isEmpty()) return;
		if (getJob(job.getId()) == null) newJobs.add(job);
			
			 if (job instanceof SimpleJob ) simpleJobs .put(job.getId(), (SimpleJob ) job);
		else if (job instanceof MissionJob)	missionJobs.put(job.getId(), (MissionJob) job);
		else if (job instanceof AuctionJob)	auctionJobs.put(job.getId(), (AuctionJob) job);
		else if (job instanceof PostedJob ) postedJobs .put(job.getId(), (PostedJob ) job);
		else throw new UnsupportedOperationException("Unsupported job: " + job.getId());
	}
	
	public synchronized void clearJobs() {
		auctionJobs	.clear();
		simpleJobs	.clear();
		missionJobs	.clear();
		postedJobs	.clear();
	}
}
