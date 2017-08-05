package scenario.env.info;

public class DynamicInfo {
	
	private static DynamicInfo instance;
	
	public DynamicInfo() {
		instance = this;
	}
	
	private long	timestamp,
					deadline;
	private int		step, 
					money, 
					jobsCompleted;

	public long getTimestamp() {
		return timestamp;
	}

	public long getDeadline() {
		return deadline;
	}

	public int getStep() {
		return step;
	}

	public int getMoney() {
		return money;
	}

	public int getJobsCompleted() {
		return jobsCompleted;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void incJobsCompleted() {
		this.jobsCompleted++;
	}
	
	public static boolean isDeadlinePassed() {
		return System.currentTimeMillis() > instance.getDeadline();
	}

}
