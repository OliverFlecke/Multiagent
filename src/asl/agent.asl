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
	
	
+retrievalRequest([map(Shop,Items)|Shops], Workshop, CNPName) : free & capacity(Capacity) 
	<-
	getVolume(Items, Volume);
	.min([Volume, Capacity], Min); 
	
	distanceToFacility(Shop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	// TODO: Add weighting to distance or capacity
	bid(Distance-Min)[artifact_id(CNPId)]; // Negative capacity since lower is better
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		getItemsToCarry(Items, Capacity, Retrieve, Rest);
		
		-free;
		.send(announcer, achieve, announceRetrieve([map(Shop,Rest)|Shops]));
		
		// Receive items from truck at shop
		!getToFacility(Shop);
		.my_name(Me);
		?truckFacility(ShopTruck, Shop);
		.send(ShopTruck, achieve, giveItems(Me, Retrieve));
		!receiveItems(Retrieve);
		
		// Give items to truck at workshop
		!getToFacility(Workshop);
		?truckFacility(WorkshopTruck, Workshop);
		.send(WorkshopTruck, achieve, receiveItems(Retrieve));
		!giveItems(WorkshopTruck, Retrieve);
		
		+free;
	}
	.
	
+assemblyRequest([Item|Items], Workshop, CNPName) : capacity(Capacity)
	<-
	getVolume([Item], Volume);
	
	if (Volume <= Capacity)
	{	
		distanceToFacility(Workshop, Distance);
		
		lookupArtifact(CNPName, CNPId);
		bid(Distance)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			!getToFacility(Workshop);
			?truckFacility(WorkshopTruck, Workshop)
			.send(WorkshopTruck, achieve, giveItems(Me, []));
			!receiveItems(Items);			
		}
	}
	
	.
	
+deliveryRequest(Items, Workshop, TaskId, DeliveryLocation, CNPName) : free & capacity(Capacity) 
	<-
	getVolume(Items, Volume);
	.min([Volume, Capacity], Min); 
	
	distanceToFacility(Workshop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	// TODO: Add weighting to distance or capacity
	bid(Distance-Min)[artifact_id(CNPId)]; // Negative capacity since lower is better
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		-free;
		if (Volume <= Capacity)
		{
			// Receive items
			!getToFacility(DeliveryLocation);
			!doAction(deliver_job(TaskId));
			
		}
		else
		{
			// Figure out which items can be carried and receive them
			announceDelivery(Rest);
		}
		+free;
	}
	.
	
+buyRequest(Shop, Items, CNPName) : capacity(Capacity) 
	<-
	getVolume(Items, Volume);
	
	if (Volume <= Capacity)
	{
		distanceToFacility(Shop, Distance);
	
		lookupArtifact(CNPName, CNPId);
		bid(Bid)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			!getToFacility(Shop);
			!buyItems(Items);
		}
	}
	.
	
	

+!giveItems(_, []).
+!giveItems(Agent, [map(Item, Amount)|Items]) <-
	!doAction(give(Agent, Item, Amount));
	!giveItems(Items).
	
+!receiveItems([]).
+!receiveItems([_|Items]) <-
	!doAction(receive);
	!receiveItems(Items).

+!buyItems([]).
+!buyItems([map(Item, Amount)|Items]) <- 
	!doAction(buy(Item, Amount));
	!buyItems(Items).
	
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
