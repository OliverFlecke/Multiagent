// Includes
{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
free.

// Initial goals
!register.
!focusArtifacts.

// Percepts
	
//+auction(TaskId, CNPName) : .my_name(car1)
//	<- 
//	.print("Auction!");
//	takeTask(_)[artifact_id(CNPId)];
//	getAuctionBid(TaskId, Bid);
//	!doAction(bid_for_job(TaskId, Bid)).
	
//+free : task(_, _, _, Type, CNPName) & Type = "partial" 	<- !getTask(CNPName).
//+free : task(_, _, _, Type, CNPName) & Type = "mission" 	<- !getTask(CNPName).
//+free : task(_, _, _, Type, CNPName) & Type = "auction"		<- !getTask(CNPName).
//+free : task(_, _, _, Type, CNPName) & Type = "job"	  		<- !getTask(CNPName).
//+free : charge(C) & maxCharge(Max) & C < Max * 0.8 			<- !charge.
	
//+!getTask(CNPName) : task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) & bid(Item, _) <-
//	lookupArtifact(CNPName, CNPId);
//	takeTask(CanTake)[artifact_id(CNPId)];
//	if (CanTake)
//	{
//		clearTask(CNPName);
//		!solveTask(TaskId, DeliveryLocation, [Item|Items]);
//	}.
//-!getTask(_) <- -+free. // The agent could not solve the task given, so try find something else
	
+retrieveRequest([map(Shop,Items)|Shops], Workshop, CNPName) : free & capacity(Capacity) 
	& myRole(Role) & not Role = "truck"
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
		-free;
		
		getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);
		
		.send(announcer, achieve, announceRetrieve([map(Shop,Rest)|Shops]));
		
		// Receive items from truck at shop
		!getToFacility(Shop);
		?truckFacility(ShopTruck, Shop);
		?step(MyStep);
		.send(ShopTruck, tell, give(MyStep, ItemsToRetrieve));
		.wait(readyToGive(Step));
		.print("Waiting for step ", Step, " to retrieve ", ItemsToRetrieve);
		.wait(step(Step));
		!receiveItems(ItemsToRetrieve);
		
		// Give items to truck at workshop
		!getToFacility(Workshop);
		?truckFacility(WorkshopTruck, Workshop);
		.send(WorkshopTruck, tell, receive);
		.wait(readyToReceive);
		.send(WorkshopTruck, achieve, receiveItems(ItemsToRetrieve));
		.wait(step(_));
		?connection(WorkshopTruck, TruckEntity, _);
		!giveItems(TruckEntity, ItemsToRetrieve);
		.send(WorkshopTruck, tell, free);
		
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
			?truckFacility(WorkshopTruck, Workshop);
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
	.print("Received buy request");
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
	
+give[source(Agent)] : not free & .current_intention(I) <- 
	.print(I);
	.suspend(I);
	.send(Agent, tell, readyToGive).
	
+give(Step, ItemsToGive)[source(Agent)] : free & step(MyStep) <-
	.print("give ", Agent);
	-free;
	.max([Step, MyStep], MaxStep);
	.send(Agent, tell, readyToGive(MaxStep+2));
	.print("Waiting for step ", MaxStep+2);
	.wait(step(MaxStep+2));
	?connection(Agent, Entity, _);
	!giveItems(Entity, ItemsToGive);
	+free.
	

+receive[source(Agent)] : not free & .current_intention(I) <-
	.print(I);
	.suspend(I);
	.send(Agent, tell, readyToReceive).
	
+receive[source(Agent)] : free & step(X) <-
	.print("receive ", Agent);
	-free;
	.send(Agent, tell, readyToReceive(X+1)).
	

+!giveItems(_, []).
+!giveItems(Agent, [map(Item, Amount)|Items]) <-
	!doAction(give(Agent, Item, Amount));
	!giveItems(Agent, Items).
	
+!receiveItems([]).
+!receiveItems([_|Items]) <-
	!doAction(receive);
	!receiveItems(Items).

+!buyItems([]).
+!buyItems([map(Item, 	   0)|Items]) <- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) : inShop(Shop) <- 
	getAvailableAmount(Item, Amount, Shop, AmountAvailable);
	if (AmountAvailable > 0) 
	{
		.print("Buying ", AmountAvailable, " of ", Item);
		!doAction(buy(Item, AmountAvailable));
	}
	.concat(Items, [map(Item, Amount - AmountAvailable)], NewItems);
	!buyItems(NewItems).
	
+!doAction(Action) : .my_name(Me) <-
	jia.action(Me, Action);
	.wait(step(_)).

//+step(0) <- +free.
+step(X) : lastAction("give") 		 & lastActionResult("successful") <- .print("Give successful!").
+step(X) : lastAction("receive") 	 & lastActionResult("successful") <- .print("Receive successful!").
+step(X) : lastAction("deliver_job") & lastActionResult("successful") <- .print("Job successful!").
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P);
	if (A = "buy")
	{
		P = [Item, Amount];
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
