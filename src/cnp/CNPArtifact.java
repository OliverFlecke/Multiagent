package cnp;

import cartago.Artifact;
import cartago.GUARD;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class CNPArtifact extends Artifact {
	
	private int bidId;
	
	private int bestBid;
	private int bestBidId;
	
	private boolean isOpen;
	
	/**
	 * 
	 * @param duration
	 */
	void init(long duration)
	{
		log("init");
		
		bidId = 0;
		
		// Assuming lower is better
		bestBid   = Integer.MAX_VALUE;
		bestBidId = -1;
		
		isOpen = true;
		
		execInternalOp("awaitBids", duration);
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
		
		isOpen = false;
		
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
	void bid(int bid, OpFeedbackParam<Integer> id)
	{
		log("New bid of " + bid + " from agent " + getOpUserName());
		
		if (isOpen)
		{
			id.set(++bidId);
			
			// Assuming lower is better
			if (bid < bestBid)
			{
				bestBid   = bid;
				bestBidId = bidId;				
			}
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
	void winner(OpFeedbackParam<Integer> id)
	{		
		await("biddingClosed");
		
		id.set(bestBidId);
	}
}
