// Includes
{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
itemsToRetrieve([]).

// Initial goals
!register.
!focusArtifacts.

// Percepts
+task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) 
	: free & bid(Item, Bid) & myRole("car")
//	& (Type == "job" | Type == "partial" | Type == "mission")
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
	
+!getTask(CNPName) : task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) & bid(Item, _) <-
	lookupArtifact(CNPName, CNPId);
	takeTask(CanTake)[artifact_id(CNPId)];
	if (CanTake)
	{
		clearTask(CNPName);
		!solveTask(TaskId, DeliveryLocation, [Item|Items]);
	}.
-!getTask(_) <- -+free. // The agent could not solve the task given, so try find something else
	
// Plans	
//+!solveTask(TaskId, DeliveryLocation, [Item|Items]) : free <-
//	-free;
//	!delegateTask(TaskId, DeliveryLocation, Items);
//	!solvePartialTask(TaskId, DeliveryLocation, Item);
//	+free.
	
+!solveTask(TaskId, DeliveryLocation, Items) 
	<-
	getBaseItems(Items, BaseItems);
	!buyItems(BaseItems);
	getClosestFacility("workshop", Workshop);
	announceRetrieval(BaseItems, Workshop);
	!assembleItems(Items);
	announceDelivery(Items, Workshop, TaskId, DeliveryLocation);
	.
	
+retrievalRequest(Items, DeliveryLocation, CNPName) : capacity(C) <-
	// Reqursively announce retrieval until all items have been retrieved.
	.print("Not implemented");
	.
	
+deliveryRequest(Items, Workshop, TaskId, DeliveryLocation, CNPName) : capacity(Capacity) 
	<-
	getVolume(Items, Volume);
	.max([Volume, Capacity], Max); 
	
	distanceToFacility(Workshop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	// TODO: Add weighting to distance or capacity
	bid(Distance-Max)[artifact_id(CNPId)]; // Negative capacity since lower is better
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		if (Capacity < Volume)
		{
			// Figure out which items can be carried and receive them
			announceDelivery(Rest);
		}
		else
		{
			// Receive items
			!getToFacility(DeliveryLocation);
			!doAction(deliver_job(TaskId));
		}
	}
	.
	
	
	
+!buyItems([]).
+!buyItems([map(   _,      0) | Items]) <- !buyItems(Items).
+!buyItems([map(Item, Amount) | Items]) <- 
	getShopSelling(Item, Amount, Shop, AmountAvailable);
	!buyItem(Item, AmountAvailable, Shop);
	!buyItems([map(Item, Amount - AmountAvailable) | Items]).
  	
+!buyItem(Item, Amount, Shop) : shop(Shop, Agent) <-
	.send(Agent, achieve, buy(Item, Amount)).
+!buyItem(Item, Amount, Shop) <-
	announceBuy(Item, Amount, Shop).
	
+buyRequest(Item, Amount, Shop, CNPName) : capacity(C) 
	<-
	getVolume([map(Item, Amount)], V);
	
	if (V < C)
	{
		distanceToFacility(Shop, Distance);
	
		lookupArtifact(CNPName, CNPId);
		bid(Bid)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			!getToFacility(Shop);
			!doAction(buy(Item, Amount));
		}
	}
	.
	
	
//+!delegateTask(_, _, []).
//+!delegateTask(TaskId, DeliveryLocation, Items) : not free <-
//	announceJob(TaskId, DeliveryLocation, Items, "partial").
//	
//+!solvePartialTask(TaskId, DeliveryLocation, Item) <- 
//	.print(TaskId, ": Delivering ", Item, " to ", DeliveryLocation);
//	getBaseItems([Item], BaseItems);
//	!addItemsToRetrieve(BaseItems);
//	!retrieveItems;
//	!assembleItems([Item]);
//	!delieverItems(TaskId, DeliveryLocation).
	
+!doAction(Action) : .my_name(Me) <-
	jia.action(Me, Action);
	.wait(step(_)).

+step(0) <- +free.
+step(X) : lastAction("deliver_job") & lastActionResult("successful") <- .print("Job successful!").
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P);
	if (A = "buy")
	{
		P = [Item, Amount];
		!addItemsToRetrieve([map(Item, Amount)]);
	}
	if (A = "deliver_job")
	{
		P = [TaskId];
		
	}
	if (A = "assemble")
	{
		P = [Item];	
	}
	.
+step(X) : charge(C) & speed(S) & not charging <- 
	distanceToClosestFacility("chargingStation", Dist);
	if (not enoughCharge(Dist) & not .desire(charge))
	{
		.suspend(getToFacility(_));
		!charge;
		.resume(getToFacility(_));
	}.
