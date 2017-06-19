
+retrieveRequest(AgentStr, [map(Shop,Items)|Shops], Workshop, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	.term2string(Agent, AgentStr);
	
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);	
	getVolume(ItemsToRetrieve, Volume);	
	distanceToFacility(Shop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
	if ( not ItemsToRetrieve = [] ) 
	{ bid(Bid)[artifact_id(CNPId)]; }
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		-free;
		clearRetrieve(CNPId);
		
		.my_name(Me);
		
		if (not Rest = [] & not Shops = [])
		{			
			.send(Agent, tell, assistant);
			.send(announcer, achieve, announceRetrieve(Agent, [map(Shop,Rest)|Shops], Workshop));
		}
		
		// Receive items from truck at shop
		!retrieveItems(map(Shop, ItemsToRetrieve));
		// Give items to truck at workshop
		!getToFacility(Workshop);
		
		if (Me \== Agent)
		{
			.send(Agent, tell, assistReady(Me));
			.wait(assembleReady(ReadyStep));
			.wait(step(ReadyStep));
			
			!assistAssemble(Agent); // Waits for assembleComplete
			
			.send(Agent, untell, assistant(Me));
			.send(Agent, untell, assistReady(Me));
		}
		
		+free;
	}.
	
+free : retrieveRequest(AgentStr, [map(Shop,Items)|Shops], Workshop, CNPId) <-
	canTake(CanTake)[artifact_id(CNPId)];
	if (CanTake)
	{
		-free;
		clearRetrieve(CNPId);
	
		getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);	
		
		.my_name(Me);
		.send(Agent, tell, assistant);
		.send(announcer, achieve, announceRetrieve(Agent, [map(Shop,Rest)|Shops], Workshop));
		
		// Receive items from truck at shop
		!retrieveItems(map(Shop, ItemsToRetrieve));
		// Give items to truck at workshop
		!getToFacility(Workshop);
		
		if (Me \== Agent)
		{
			.send(Agent, tell, assistReady(Me));
			.wait(assembleReady(ReadyStep));
			.wait(step(ReadyStep));
			
			!assistAssemble(Agent); // Waits for assembleComplete
			
			.send(Agent, untell, assistant(Me));
			.send(Agent, untell, assistReady(Me));
		}
		
		+free;
	}
	
+assembleRequest([], _, _, _).
+assembleRequest(Items, Workshop, TaskId, DeliveryLocation, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	getItemsToCarry(Items, Capacity, ItemsToAssemble, AssembleRest);
	getBaseItems(ItemsToAssemble, ItemsToRetrieve);
	getVolume(ItemsToRetrieve, Volume);
	distanceToFacility(Workshop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
	.print(Bid, Items, Capacity, ItemsToAssemble, ItemsToRetrieve, Volume, AssembleRest);
	
	if ( not (ItemsToRetrieve = []) ) 
	{ bid(Bid)[artifact_id(CNPId)]; }
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{		
		-free;
		clearAssemble(CNPId);
		
		getShoppingList(ItemsToRetrieve, ShoppingList);
		ShoppingList = [Shop|RetrieveRest];
		
		.print("ItemsToRetrieve: ", ItemsToRetrieve, " - ", Volume, " - ", Capacity);
		.print("To Retrieve: ", Shop, " - ", RetrieveRest);
		.print("To Assemble: ", ItemsToAssemble, " - ", AssembleRest);
		
		.my_name(Me);
		if (not RetrieveRest = [])
		{
			-+assistants(1);
			.send(announcer, achieve, announceRetrieve(Me, RetrieveRest, Workshop));
		}
		.send(announcer, achieve, announceAssemble(AssembleRest, Workshop, TaskId, DeliveryLocation));
		
		!retrieveItems(Shop);
		!getToFacility(Workshop);
		
		.wait(assistants(N) & .count(assistReady(_), N));
		
		?step(X);
		ReadyStep = X + 2;
		
		for ( assistReady(A) )
		{
			.send(A, tell, assembleReady(ReadyStep));
		}
		
		.wait(step(ReadyStep));
		
		!assembleItems(ItemsToAssemble);
		
		for ( assistReady(A) ) 
		{
			.send(A, tell, assembleComplete);
			.send(A, untell, assembleReady(ReadyStep));
		}
		
		!getToFacility(DeliveryLocation);
		!deliverItems(TaskId, DeliveryLocation);
		
		+free;
	}.
	
+assistant : assistants(N) <- -+assistants(N + 1); -assistant.
