{ include("agent.asl") }

+task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) 
	: free & bid(Item, Bid)
	<-
	lookupArtifact(CNPName, CNPId);
	bid(Bid)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	if (Won)
	{
		.drop_desire(charge); .drop_desire(gather);
		clearTask(CNPName);
		!solveTask(TaskId, DeliveryLocation, [Item|Items]);
	}.
	
+auction(TaskId, CNPName) : .my_name(car1)
	<- 
	.print("Auction!");
	takeTask(_)[artifact_id(CNPId)];
	getAuctionBid(TaskId, Bid);
	!doAction(bid_for_job(TaskId, Bid)).
	
+free : task(_, _, _, Type, CNPName) & Type = "partial" 	<- !getTask(CNPName).
+free : task(_, _, _, Type, CNPName) & Type = "mission" 	<- !getTask(CNPName).
+free : task(_, _, _, Type, CNPName) & Type = "auction"		<- !getTask(CNPName).
+free : task(_, _, _, Type, CNPName) & Type = "job"	  		<- !getTask(CNPName).
+free : charge(C) & maxCharge(Max) & C < Max * 0.8 			<- !charge.