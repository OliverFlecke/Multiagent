+assembleRequest(JobId, Items, Storage, CNPId) : free <-
	!!doIntention(assembleRequest(JobId, Items, Storage, CNPId)).
	
+retrieveRequest(Shop, Items, Workshop, Agent, CNPId) : free <-
	!!doIntention(retrieveRequest(Shop, Items, Workshop, Agent, CNPId)). 
	
+toolRequest(Tool, Agent, CNPId) : free & canCarryAndUseTool(Tool) <-
	!!doIntention(toolRequest(Tool, Agent, CNPId)).

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
			.print("Helping ", Agent);
			clear("retrieveRequest", 5, CNPId);
			!retrieveItems(Shop, ItemsToRetrieve);
			!coordinateAssist(Workshop, Agent);
		}
	}.

+!toolRequest(Tools, Workshop, Agent, CNPId) 
	: capacity(Capacity) & speed(Speed) <-
	?getUsableTools(Tools, Usable, NotUsable);
	?getInvTools(Usable, InInv, NotInInv);
	
	getItemsToCarry(NotInInv, Capacity, ToolsToRetrieve, RetrieveRest);
	.concat(InInv, ToolsToRetrieve, AllTools);
	.concat(NotUsable, RetrieveRest, RestTools);
	
	getVolume(AllTools, Volume);	
	Bid = -Speed - Volume; 
	
	bid(Bid, AllTools, RestTools)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		.print("Helping ", Agent, " with ", AllTools);
		.my_name(Me);
		.send(Agent, tell, assistant(Me));
		clear("toolRequest", 3, CNPId);
		!retrieveTools(ToolsToRetrieve);
		!coordinateAssist(Workshop, Agent);
	}.
