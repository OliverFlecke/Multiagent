+assembleRequest(JobId, Items, Storage, CNPId) : free <-
	!!assembleRequest(JobId, Items, Storage, CNPId).
	
+retrieveRequest(Shop, Items, Workshop, Agent, CNPId) : free <-
	!!retrieveRequest(Shop, Items, Workshop, Agent, CNPId). 

+!assembleRequest(JobId, Items, Storage, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	-free;
	getItemsToCarry(Items, Capacity, ItemsToAssemble, AssembleRest);
	getBaseItems(ItemsToAssemble, ItemsToRetrieve);
	getVolume(ItemsToRetrieve, Volume);
	
	// Negative volume since lower is better
	Bid = -Speed - Volume; 
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid, AssembleRest)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{		
			clear("assembleRequest", 4, CNPId);
			!acquireItems(ItemsToAssemble); 
			!deliverJob(JobId, ItemsToAssemble, Storage);
		}
	}
	+free.
	
+!retrieveRequest(Shop, Items, Workshop, Agent, CNPId) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	-free;
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, RetrieveRest);	
	getVolume(ItemsToRetrieve, Volume);	
	distanceToFacility(Shop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid, RetrieveRest)[artifact_id(CNPId)]; 
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			clear("retrieveRequest", 5, CNPId);
			!retrieveItems(Shop, ItemsToRetrieve);
			!getToFacility(F);
			!acceptAssembleProtocol(Agent);
		}
	}
	+free.