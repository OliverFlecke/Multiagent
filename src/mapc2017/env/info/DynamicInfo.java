package mapc2017.env.info;

public class DynamicInfo {
	
	private static DynamicInfo instance;	
	public  static DynamicInfo get() { return instance; }
	
	private long	money,
					timestamp,
					deadline;
	private int		step,  
					jobsCompleted;
	
	public DynamicInfo() {
		instance = this;
	}
	
	/////////////
	// GETTERS //
	/////////////

	public synchronized long getTimestamp() {
		return timestamp;
	}

	public synchronized long getDeadline() {
		return deadline;
	}

	public synchronized int getStep() {
		return step;
	}

	public synchronized long getMoney() {
		return money;
	}

	public synchronized int getJobsCompleted() {
		return jobsCompleted;
	}
	
	/////////////
	// SETTERS //
	/////////////

	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public synchronized void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public synchronized void setStep(int step) {
		this.step = step;
	}

	public synchronized void setMoney(long money) {
		this.money = money;
	}

	public synchronized void incJobsCompleted() {
		this.jobsCompleted++;
	}
	
	/////////////
	// METHODS //
	/////////////
	
	public static boolean isDeadlinePassed() {
		return System.currentTimeMillis() > instance.getDeadline();
	}

}
