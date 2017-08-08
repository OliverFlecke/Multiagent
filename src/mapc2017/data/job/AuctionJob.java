package mapc2017.data.job;

public class AuctionJob extends Job {
	
	private int fine, 
				bid, 
				steps;

	public AuctionJob(Job job, int fine, int bid, int steps) {
		super(job);
		this.fine	= fine;
		this.bid	= bid;
		this.steps	= steps;
	}
	
	public AuctionJob(AuctionJob job) {
		this(job, job.fine, job.bid, job.steps);
	}

	public int getFine() {
		return fine;
	}

	public int getBid() {
		return bid;
	}

	public int getSteps() {
		return steps;
	}

}
