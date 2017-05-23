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
	
	private List<Bid> 	bids;	
	private boolean 	isOpen;
	
	/**
	 * 
	 * @param duration
	 */
	void init()
	{		
		this.bids	= new ArrayList<>();
		this.isOpen = true;
		
		execInternalOp("awaitBids", 1000);
	}
	
	/**
	 * Awaits bids, closing the bidding after a given duration.
	 * @param duration - Duration to wait.
	 */
	@INTERNAL_OPERATION
	void awaitBids(long duration)
	{
		log("Awaiting bids");
		
		await_time(duration);
		
		this.isOpen = false;
		
		log("Bidding closed");
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
	void winner(OpFeedbackParam<String> agent)
	{		
		await("biddingClosed");
		
		Optional<Bid> bestBid = bids.stream().min(Comparator.comparingInt(Bid::getBid));
		
		if (bestBid.isPresent())
		{
			agent.set(bestBid.get().getAgent());
		}
	}
	
	static class Bid {
		
		String 	agent;
		int 	bid;
		
		public Bid(String agent, int bid) {
			this.agent 	= agent;
			this.bid	= bid;
		}
		
		public String 	getAgent() 	{ return this.agent; }
		public int 		getBid()	{ return this.bid; }
	}
}
