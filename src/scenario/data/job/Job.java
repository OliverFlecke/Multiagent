package scenario.data.job;

import java.util.Map;

public class Job {
	
	String 					id, storage;
	int						reward;
	long					start, end;
	Map<String, Integer> 	items;

	public Job(String id, String storage, int reward, 
			long start, long end, Map<String, Integer> items) {
		this.id			= id;
		this.storage	= storage;
		this.reward		= reward;
		this.start		= start;
		this.end		= end;
		this.items		= items;
	}
	
	public Job(Job job) {
		this(job.id, job.storage, job.reward, job.start, job.end, job.items);
	}

}
