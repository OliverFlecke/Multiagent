// Includes
{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
free.

// Initial goals
!register.
!focusArtifacts.

// Strategy

+task(TaskId, DeliveryLocation, [Item|Items], _, CNPName) : free & bid(Item, Bid) <-
	lookupArtifact(CNPName, CNPId);
	bid(Bid)[artifact_id(CNPId)];
	winner(Name)[artifact_id(CNPId)];
	if ( myName(Name) )
	{
		!solveJob(TaskId, DeliveryLocation, [Item|Items])
	}.
	
+!solveJob(TaskId, DeliveryLocation, [Item|Items]) <-
	-free;
	if ( not Items = [] )
	{
		announce(TaskId, DeliveryLocation, Items, "partial");
	}
	!solvePartialJob(TaskId, DeliveryLocation, Item);
	+free.
	
+free : task(TaskId, DeliveryLocation, [Item|Items], "partial", CNPName) & bid(Item, _) <-
	.print("Partial");
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveJob(TaskId, DeliveryLocation, [Item|Items]).
	
+free : task(TaskId, DeliveryLocation, [Item|Items], _, CNPName) & bid(Item, _) <-
	.print("Non-Partial");
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveJob(TaskId, DeliveryLocation, [Item|Items]).
	
+!solvePartialJob(Job, DeliveryLocation, Item) : .my_name(Me) <- 
	.print(Me, " doing ", Job, " to ", DeliveryLocation, " with: ", Item);
	
	getBaseItems([Item], BaseItems);
	.print("Base items needed: ", BaseItems);
	
	!retrieveItems(BaseItems);

	!assembleItems([Item]);

	!delieverItems(Job, DeliveryLocation);
	.print("Job done!").
