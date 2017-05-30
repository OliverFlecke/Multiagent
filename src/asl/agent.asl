// Includes
{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
free.

// Initial goals
!register.
!focusArtifacts.

// Percepts
+task(TaskId, DeliveryLocation, [Item|Items], _, CNPName) : free & bid(Item, Bid) <-
	lookupArtifact(CNPName, CNPId);
	bid(Bid)[artifact_id(CNPId)];
	winner(Name)[artifact_id(CNPId)];
	if ( myName(Name) )
	{
		!solveTask(TaskId, DeliveryLocation, [Item|Items])
	}.
	
+free : task(TaskId, DeliveryLocation, [Item|Items], "partial", CNPName) & bid(Item, _) <-
	.print("Partial");
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveTask(TaskId, DeliveryLocation, [Item|Items]).
	
//+free : task(TaskId, DeliveryLocation, [Item|Items], _, CNPName) & bid(Item, _) <-
//	.print("Non-Partial");
//	lookupArtifact(CNPName, CNPId);
//	takeTask[artifact_id(CNPId)];
//	!solveTask(TaskId, DeliveryLocation, [Item|Items]).
	
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
	!retrieveItems(BaseItems);
	!assembleItems([Item]);
	!delieverItems(TaskId, DeliveryLocation).
