+assembleRequest(JobId, Items, Storage, CNPId) : free <-
	!!doIntention(assembleRequest(JobId, Items, Storage, CNPId)).
	
+retrieveRequest(Shop, Items, Workshop, Agent, CNPId) : free <-
	!!doIntention(retrieveRequest(Shop, Items, Workshop, Agent, CNPId)). 

+!assembleRequest(JobId, Items, Storage, CNPId) 
	: capacity(Capacity) & speed(Speed) <-
	getItemsToCarry(Items, Capacity, ItemsToAssemble, AssembleRest);
	getBaseVolume(ItemsToAssemble, Volume);
	
	// Negative volume since lower is better
	Bid = -Speed - Volume; 
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid, AssembleRest)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			.print("Job: ", JobId, "Assembling: ", ItemsToAssemble);
			clear("assembleRequest", 4, CNPId);
			!solveJob(JobId, ItemsToAssemble, Storage);
		}
	}.
	
+!retrieveRequest(Shop, Items, Workshop, Agent, CNPId) 
	: capacity(Capacity) & speed(Speed) <-
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, RetrieveRest);	
	getVolume(ItemsToRetrieve, Volume);	
	distanceToFacility(Shop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed) * 10 - Volume; 
	
	if (not ItemsToRetrieve = []) 
	{ 
		bid(Bid, ItemsToRetrieve, RetrieveRest)[artifact_id(CNPId)]; 
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			clear("retrieveRequest", 5, CNPId);
			!assistRetrieve(Shop, ItemsToRetrieve, Workshop, Agent);
		}
	}.
