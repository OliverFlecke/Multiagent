package mapc2017.env.job;

import mapc2017.data.item.ShoppingList;
import mapc2017.data.job.AuctionJob;
import mapc2017.data.job.Job;
import mapc2017.data.job.MissionJob;

public class JobEvaluation {
	
	private Job 			job;
	private int 			profit, 
							steps, 
							value, 
							reqAgents;
	private String 			workshop;
	private ShoppingList 	shoppingList;
	
	public JobEvaluation(Job job, int profit, int steps, int reqAgents, 
			String workshop, ShoppingList shoppingList) {
		this.job		= job;
		this.profit 	= profit;
		this.steps		= steps;
		this.value 		= profit / steps;
		this.reqAgents 	= reqAgents;
		this.workshop 	= workshop;
		
		if (job instanceof MissionJob) value += 1000;
		if (job instanceof AuctionJob && ((AuctionJob) job).hasWon()) value += 800;
	}
	
	public Job getJob() {
		return job;
	}
	
	public int getProfit() {
		return profit;
	}

	public int getSteps() {
		return steps;
	}

	public int getValue() {
		return value;
	}
	
	public int getReqAgents() {
		return reqAgents;
	}
	
	public String getWorkshop() {
		return workshop;
	}
	
	public ShoppingList getShoppingList() {
		return shoppingList;
	}
	
	@Override
	public String toString() {
		return job.getId();
	}
}