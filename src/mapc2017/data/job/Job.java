package mapc2017.data.job;

import java.util.HashMap;
import java.util.Map;

public class Job {
	
	private String 					id,
									storage;
	private int						reward,
									start, 
									end;
	private Map<String, Integer> 	items;

	public Job(String id, String storage, int reward, 
			int start, int end, Map<String, Integer> items) {
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

	public String getId() {
		return id;
	}

	public String getStorage() {
		return storage;
	}

	public int getReward() {
		return reward;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public Map<String, Integer> getItems() {
		return new HashMap<>(items);
	}
	
	@Override
	public String toString() {
		return id;
	}

}
