
+retrieveRequest(AgentStr, [map(Shop,Items)|Shops], Workshop, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);	
	getVolume(ItemsToRetrieve, Volume);	
	distanceToFacility(Shop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid)[artifact_id(CNPId)]; 
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			-free;
			clearRetrieve(CNPId);
			!retrieve(AgentStr, ItemsToRetrieve, Workshop, [map(Shop,Rest)|Shops]);		
			+free;
		}
	}.
	
+assembleRequest(Items, Workshop, TaskId, DeliveryLocation, _, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	getItemsToCarry(Items, Capacity, ItemsToAssemble, AssembleRest);
	getBaseItems(ItemsToAssemble, ItemsToRetrieve);
	getVolume(ItemsToRetrieve, Volume);
	distanceToFacility(Workshop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
//	.print("+request ", Items, Capacity, ItemsToAssemble, AssembleRest, ItemsToRetrieve, Volume);
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{		
			.print(ItemsToAssemble, " - ", AssembleRest);
			-free;
			clearAssemble(CNPId);
			!assemble(ItemsToRetrieve, ItemsToAssemble, AssembleRest, Workshop, TaskId, DeliveryLocation);
			+free;
		}
	}.
	
+!retrieve(_, [], _, _).
+!retrieve(AgentStr, ItemsToRetrieve, Workshop, ToAnnounce) 
	: .my_name(Me) & .term2string(Agent, AgentStr) <-
	
	if (ToAnnounce = [map(Shop,Rest)|Shops] & not (Rest = [] & Shops = []))
	{
		.send(Agent,   tell, assistNeeded);
		.send(Agent, untell, assistNeeded); // Simulates a signal
		.send(announcer, achieve, announceRetrieve(Agent, [map(Shop,Rest)|Shops], Workshop));
	}
	
	!retrieveItems(map(Shop, ItemsToRetrieve));

	!getToFacility(Workshop);
	
	.send(Agent,   tell, assistReady(Me));
	
	.wait(assembleReady(ReadyStep));
	.wait(step(ReadyStep));
	
	!assistAssemble(Agent);
	
	.send(Agent, untell, assistReady(Me)).
	
+!assemble([], _, _).
+!assemble(ItemsToRetrieve, ItemsToAssemble, AssembleRest, Workshop, TaskId, DeliveryLocation) 
	: .my_name(Me) <-

	getShoppingList(ItemsToRetrieve, ShoppingList);
	ShoppingList = [Shop|RetrieveRest];	
	
	+assistants([]);
	
	if (not RetrieveRest = [])
	{
		+assistCount(1);
		.send(announcer, achieve, announceRetrieve(Me, RetrieveRest, Workshop));
	}
	
	if (not AssembleRest = [])
	{
		.send(announcer, achieve, announceAssemble(AssembleRest, Workshop, TaskId, DeliveryLocation, "old"));			
	}
	
	!retrieveItems(Shop);
	!getToFacility(Workshop);
	
	.wait(assistCount(N) & assistants(L) & .length(L, N));
	
	?step(X);
	ReadyStep = X + 2;
	
	for (.member(A, L))
	{
		.send(A,   tell, assembleReady(ReadyStep));
	}
	
	.wait(step(ReadyStep));
	
	!assembleItems(ItemsToAssemble);	
	!!assembleComplete;
	
	!getToFacility(DeliveryLocation);
	!deliverItems(TaskId, DeliveryLocation).

	
+free : retrieveRequest(AgentStr, [map(Shop,Items)|Shops], Workshop, CNPId) 
		& capacity(Capacity) <-
		
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);
	
	if (not ItemsToRetrieve = [])
	{
		takeTask(CanTake)[artifact_id(CNPId)];
		
		if (CanTake)
		{
			-free;
			clearRetrieve(CNPId);			
			!retrieve(AgentStr, ItemsToRetrieve, Workshop, [map(Shop,Rest)|Shops]);			
			+free;
		}
	}.
	
+free : assembleRequest(Items, Workshop, TaskId, DeliveryLocation, "old", CNPId)
		& capacity(Capacity) <-
		
	getItemsToCarry(Items, Capacity, ItemsToAssemble, AssembleRest);
	getBaseItems(ItemsToAssemble, ItemsToRetrieve);
	
//	.print("+free ", Items, Capacity, ItemsToAssemble, AssembleRest, ItemsToRetrieve);
	
	if (not ItemsToRetrieve = [])
	{
		takeTask(CanTake)[artifact_id(CNPId)];
		
		if (CanTake)
		{		
			-free;
			clearAssemble(CNPId);			
			!assemble(ItemsToRetrieve, ItemsToAssemble, AssembleRest, Workshop, TaskId, DeliveryLocation);			
			+free;
		}
	}.
	
@assistCount[atomic]
+assistNeeded : assistCount(N) <- -+assistCount(N + 1).

@assistants[atomic]
+assistReady(A) : assistants(L) <- -+assistants([A|L]).

+!assembleComplete : assistants(L) <-

	for (.member(A, L)) 
	{
		.send(A, tell, assembleComplete);
	}
	
	.wait(step(_));

	for (.member(A, L)) 
	{
		.send(A, untell, assembleComplete);
		.send(A, untell, assembleReady(_));
	}
	
	-assistCount(_);
	-assistants(_).
