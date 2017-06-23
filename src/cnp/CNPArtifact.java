package cnp;

import cartago.Artifact;
import cartago.GUARD;
import cartago.INTERNAL_OPERATION;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class CNPArtifact extends Artifact {
	
	private Bid 		bestBid;
	private boolean 	isOpen;
	
	void init()
	{		
		execInternalOp("awaitBids", 500);
	}
	
	/**
	 * Awaits bids, closing the bidding after a given duration.
	 * @param duration - Duration to wait.
	 */
	@INTERNAL_OPERATION
	void awaitBids(long duration)
	{		
		this.isOpen = true;
		
		await_time(duration);
		
		this.isOpen = false;		
	}
	
	@OPERATION
	void bid(int bid)
	{
		if (bidIsBetter(bid))
		{
			bestBid = new Bid(getOpUserName(), bid);			
		}
	}
	
	@OPERATION
	void bid(int bid, Object data)
	{
		if (bidIsBetter(bid))
		{
			bestBid = new Bid(getOpUserName(), bid, data);			
		}
	}
	
	/**
	 * Lower bid is better
	 * @param bid
	 * @return
	 */
	boolean bidIsBetter(int bid)
	{
		return isOpen && (bestBid == null || bid < bestBid.getBid());
	}
	
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
		
		if (bestBid != null && bestBid.getAgent().equals(getOpUserName()))
		{
			won.set(true);
		}
		else
		{
			won.set(false);
		}
	}
	
	@OPERATION @LINK
	void takeTask(OpFeedbackParam<Boolean> canTake)
	{
		await("biddingClosed");
		
		canTake.set(bestBid == null);
		
		if (canTake.get())
		{			
			bestBid = new Bid(null, 0);
		}
	}
	
	@LINK
	void getBid(OpFeedbackParam<Bid> ret)
	{
		await("biddingClosed");
		
		ret.set(bestBid);
	}
	
	@LINK
	void disposeArtifact()
	{
		this.dispose();
	}
}
