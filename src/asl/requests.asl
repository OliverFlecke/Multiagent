
+retrieveRequest([map(Shop,Items)|Shops], Workshop, CNPName) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	getItemsToCarry(Items, Capacity, ItemsToRetrieve, Rest);	
	getVolume(ItemsToRetrieve, Volume);	
	distanceToFacility(Shop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed)*10-Volume; 
	
	lookupArtifact(CNPName, CNPId);
	if ( not (ItemsToRetrieve = []) ) 
	{ bid(Bid)[artifact_id(CNPId)]; }
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		-free;
		
		.send(announcer, achieve, announceRetrieve([map(Shop,Rest)|Shops]));
		
		// Receive items from truck at shop
		!getToFacility(Shop);
		?truckFacility(ShopTruck, Shop);
		!initiateReceiveProtocol(ShopTruck, ItemsToRetrieve);
		
		// Give items to truck at workshop
		!getToFacility(Workshop);
		?truckFacility(WorkshopTruck, Workshop);
		!initiateGiveProtocol(WorkshopTruck, ItemsToRetrieve);
		
		+free;
	}.

+assembleRequest(Items, Workshop, TaskId, DeliveryLocation, CNPName) 
	: free & capacity(Capacity) & speed(Speed) <-
	
	getItemsToCarry(Items, Capacity, ItemsToAssemble, Rest);
	getBaseItems(ItemsToAssemble, ItemsToReceive);
	getVolume(ItemsToReceive, Volume);
	distanceToFacility(Workshop, Distance);	
	
	// Negative volume since lower is better
	Bid = math.ceil(Distance/Speed)*10-Volume; 
	
	lookupArtifact(CNPName, CNPId);
	if ( not (ItemsToRetrieve = []) ) 
	{ bid(Bid)[artifact_id(CNPId)]; }
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		-free;
		
		.send(announcer, achieve, announceAssemble(Rest));
		
		// Receive items from truck at shop
		!getToFacility(Workshop);
//		?truckFacility(WorkshopTruck, Workshop);
		!initiateReceiveProtocol(WorkshopTruck, ItemsToReceive);
		
		// Give items to truck at workshop
		!assembleItems(ItemsToAssemble);
		!deliverItems(TaskId, DeliveryLocation);
		
		+free;
	}.
	
+deliverRequest(Items, Workshop, TaskId, DeliveryLocation, CNPName) 
	: free & capacity(Capacity) <-
	
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
	
+buyRequest(Shop, Items, CNPName) : capacity(Capacity) <-
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
	}.
