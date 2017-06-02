// Includes
{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
itemsToRetrieve([]).

// Initial goals
!register.
!focusArtifacts.

// Percepts
+task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) 
	: free & bid(Item, Bid) & (Type == "job" | Type == "partial" | Type == "mission")
	<-
	lookupArtifact(CNPName, CNPId);
	bid(Bid)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	if (Won)
	{
		.drop_desire(charge); .drop_desire(gather);
		!solveTask(TaskId, DeliveryLocation, [Item|Items]);
	}.
	
+free : task(_, _, _, Type, _) & Type = "partial" 	<- !getTask(Type).
+free : task(_, _, _, Type, _) & Type = "mission" 	<- .print("Doing a mission!"); !getTask(Type).
+free : task(_, _, _, Type, _) & Type = "job"	  	<- !getTask(Type).
+free : charge(C) & maxCharge(Max) & C < Max * 0.8 	<- !charge.
+free <- 
	getClosestFacility("resourceNode", F);
	if (not (F == "none"))
	{
		!getToFacility(F);
		!gather;
	}
	else {
		getClosestFacility("shop", S);
		!getToFacility(S);
	}.
	
+!getTask(Type) : task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) & bid(Item, _) <-
	lookupArtifact(CNPName, CNPId);
	takeTask(CanTake)[artifact_id(CNPId)];
	if (CanTake)
	{
		!solveTask(TaskId, DeliveryLocation, [Item|Items]);
	}.
	
// Plans	
+!solveTask(TaskId, DeliveryLocation, [Item|Items]) : free <-
	-free;
	!delegateTask(TaskId, DeliveryLocation, Items);
	!solvePartialTask(TaskId, DeliveryLocation, Item);
	+free.
	
+!delegateTask(_, _, []).
+!delegateTask(TaskId, DeliveryLocation, Items) : not free <-
	announce(TaskId, DeliveryLocation, Items, "partial").
	
+!solvePartialTask(TaskId, DeliveryLocation, Item) <- 
	.print(TaskId, ": Delivering ", Item, " to ", DeliveryLocation);
	getBaseItems([Item], BaseItems);
	!addItemsToRetrieve(BaseItems);
	!retrieveItems;
	!assembleItems([Item]);
	!delieverItems(TaskId, DeliveryLocation).
	
+!doAction(Action) : .my_name(Me) <-
	jia.action(Me, Action);
	.wait(step(_)).

+step(X) : readyToStart <- +free.
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
	if (not enoughCharge(Dist))
	{
		.suspend(getToFacility(_));
		!charge;
		.resume(getToFacility(_));
	}.
