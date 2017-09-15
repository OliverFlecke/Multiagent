package mapc2017.data.job;

import java.util.Map;

import mapc2017.data.item.ItemList;
import mapc2017.env.info.DynamicInfo;

public class Job {
	
	private String 		id,
						storage;
	private int			reward,
						start, 
						end;
	private ItemList	items;

	public Job(String id, String storage, int reward, 
			int start, int end, Map<String, Integer> items) {
		this.id			= id;
		this.storage	= storage;
		this.reward		= reward;
		this.start		= start;
		this.end		= end;
		this.items		= new ItemList(items);
	}
	
	public Job(Job job) {
		this(job.id, job.storage, job.reward, job.start, job.end, job.items);
	}

	public String getId() {
		return id;
	}
	
	public int getNumber() {
		return Integer.parseInt(id.replaceAll("job", ""));
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

	public boolean isDeadlinePassed()
	{
		return DynamicInfo.get().getStep() > this.getEnd();
	}

	public ItemList getItems() {
		return new ItemList(items);
	}
	
	@Override
	public String toString() {
		return id;
	}

}
