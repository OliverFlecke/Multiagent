+assembleRequest(JobId, Items, Storage, CNPId) : free <-
	!!doIntention(assembleRequest(JobId, Items, Storage, CNPId)).
	
+retrieveRequest(Shop, Items, Workshop, Agent, CNPId) : free & not myRole("drone") <-
	!!doIntention(retrieveRequest(Shop, Items, Workshop, Agent, CNPId)). 
	
+toolRequest(Tools, Workshop, Agent, CNPId) : free <-
	!!doIntention(toolRequest(Tools, Workshop, Agent, CNPId)).
	
+toolRequest(Tools, Workshop, Agent, CNPId) : assisting(Agent) & idle <-
	!toolRequestAssist(Tools, Workshop, Agent, CNPId).

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
//			clear("assembleRequest", 4, CNPId);
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
			.print("Helping ", Agent);
			+assisting(Agent);
//			clear("retrieveRequest", 5, CNPId);
			!retrieveItems(Shop, ItemsToRetrieve);
			!coordinateAssist(Workshop, Agent);
		}
	}.

+!toolRequestAssist(Tools, Workshop, Agent, CNPId) 
	: capacity(Capacity) & speed(Speed) <-
	?getUsableTools(Tools, Usable, NotUsable);
	?getInvTools(Usable, InInv, NotInInv);
	
	getToolsToCarry(NotInInv, Capacity, ToolsToRetrieve, RetrieveRest);
	.concat(InInv, ToolsToRetrieve, AllTools);
	.concat(NotUsable, RetrieveRest, RestTools);
	
	if (not AllTools = [])
	{
		bid(-Speed - 1000, AllTools, RestTools)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			.send(Agent, untell, assistantReady(Me));
			!reset;
			-idle;
			.print("Helping ", Agent, " with ", AllTools);
			!retrieveTools(ToolsToRetrieve);
			!coordinateAssist(Workshop, Agent);
		}		
	}.
	
+!toolRequest(Tools, Workshop, Agent, CNPId) 
	: capacity(Capacity) & speed(Speed) <-
	?getUsableTools(Tools, Usable, NotUsable);
	?getInvTools(Usable, InInv, NotInInv);
	
	getToolsToCarry(NotInInv, Capacity, ToolsToRetrieve, RetrieveRest);
	.concat(InInv, ToolsToRetrieve, AllTools);
	.concat(NotUsable, RetrieveRest, RestTools);
	
	if (not AllTools = [])
	{
		getToolsVolume(AllTools, Volume);	
		Bid = -Speed - Volume; 
		
		bid(Bid, AllTools, RestTools)[artifact_id(CNPId)];
		winner(Won)[artifact_id(CNPId)];
		
		if (Won)
		{
			.print("Helping ", Agent, " with ", AllTools);
			+assisting(Agent);
			!retrieveTools(ToolsToRetrieve);
			!coordinateAssist(Workshop, Agent);
		}		
	}.
