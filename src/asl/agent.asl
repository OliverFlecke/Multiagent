// Includes
{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }

// Initial beliefs
free.
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
	winner(Name)[artifact_id(CNPId)];
	if ( myName(Name) )
	{
		!solveTask(TaskId, DeliveryLocation, [Item|Items])
	}.
	
+free : task(_, _, _, "partial", _) <- !getTask("partial").
+free : task(_, _, _, "mission", _) <- .print("Doing a mission!"); !getTask("mission").
+free : task(_, _, _, "job", _)		<- !getTask("job").
	
+!getTask(Type) : task(TaskId, DeliveryLocation, [Item|Items], Type, CNPName) & bid(Item, _) <-
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveTask(TaskId, DeliveryLocation, [Item|Items]).
	
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
	.wait({+step(_)}).

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
	.