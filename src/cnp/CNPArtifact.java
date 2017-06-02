package cnp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import cartago.Artifact;
import cartago.GUARD;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class CNPArtifact extends Artifact {
	
	private List<Bid> 		bids;	
	private Optional<Bid> 	bestBid;
	private boolean 		isOpen;
	
	/**
	 * 
	 * @param duration
	 */
	void init()
	{		
		this.bids	= new ArrayList<>();
		this.isOpen = true;
		
		execInternalOp("awaitBids", 250);
	}
	
	/**
	 * Awaits bids, closing the bidding after a given duration.
	 * @param duration - Duration to wait.
	 */
	@INTERNAL_OPERATION
	void awaitBids(long duration)
	{		
		await_time(duration);
		
		this.isOpen = false;		
	}
	
	/**
	 * Accepts a bid if the bidding is open, setting the bid's ID
	 * as a feedback parameter. If the bid is better than previous
	 * bids, the bestBid is updated.
	 * @param bid - An agent's bid.
	 * @param id - ID of the bid.
	 */
	@OPERATION
	void bid(int bid)
	{		
		if (isOpen)
		{
			bids.add(new Bid(getOpUserName(), bid));
		}
	}
	
	/**
	 * Guard to signal when bidding is closed.
	 * @return True if bidding is closed, false otherwise.
	 */
	@GUARD
	boolean biddingClosed()
	{
		return !isOpen;
	}
	
	/**
	 * Sets the ID of the best bid as a feedback parameter when the
	 * bidding is closed.
	 * @param id - ID of the best bid.
	 */
	@OPERATION
	void winner(OpFeedbackParam<Boolean> won)
	{		
		await("biddingClosed");
		
		String winner = getWinner();
		
		if (winner != null && winner.equals(getOpUserName()))
		{
			won.set(true);
		}
		else
		{
			won.set(false);
		}
	}
	
	/**
	 * Any agent is allowed to immediately take a task which has been 
	 * bid for, but not received any bids.
	 */
	@OPERATION
	void takeTask(OpFeedbackParam<Boolean> canTake)
	{
		await("biddingClosed");
		
		if (bids.isEmpty())
		{
			canTake.set(true);
		}
		else
		{
			canTake.set(false);
		}
	}
	
	private String getWinner()
	{
		if (bestBid == null)
		{
			bestBid = bids.stream().min(Comparator.comparingInt(Bid::getBid));
		}
		if (bestBid.isPresent())
		{
			return bestBid.get().getAgent();
		}
		return null;
		
	}
	
	static class Bid {
		
		private String 	agent;
		private int 	bid;
		
		public Bid(String agent, int bid) {
			this.agent 	= agent;
			this.bid	= bid;
		}
		
		public String 	getAgent() 	{ return this.agent; }
		public int 		getBid()	{ return this.bid; }
	}
}
