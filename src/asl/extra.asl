
+task(JobId, Items, Storage, Type, CNPId) : 
	free & canSolve(Items) & Type \== "auction" <-
	!doIntention(newTask(JobId, Items, Storage, Type, CNPId)).
	
+!newTask(JobId, Items, Storage, Type, CNPId) : getBid(Items, Bid) <-
	bid(Bid)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	if (Won) {
		clear("task", 5, CNPId);
		!deliverJob(JobId, Items, Storage);
	}.
	
+!deliverJob(Id, Storage) : facility(Storage) <- 						  !doAction(deliver_job(Id)).
+!deliverJob(Id, Storage)  			 		  <- !getToFacility(Storage); !doAction(deliver_job(Id)).

+!delegateJob( _,    [], _).
+!delegateJob(Id, Items, F) : canCarry(Items) 					  	  <- !solveJob(Id, Items, F).
+!delegateJob(Id, Items, F) : jia.req.delegateJob(Id, Items, F, Rest) <- !delegateJob(Id, Rest, F).
+!delegateJob(Id, Items, F) : capacity(C) 						  	  <- 
	getItemsToCarry(Items, C, ItemsToCarry, Rest);
	!solveJob(Id, ItemsToCarry, F);
	!delegateJob(Id, Rest, F).
	
+!delegateItems([                         ], _).
+!delegateItems([map(   _,    [])|ShopList], F) 				<- !delegateItems(ShopList, F).
+!delegateItems([map(Shop, Items)|      []], F) 				<- !retrieveItems(Shop, Items).
+!delegateItems([map(Shop, Items)|ShopList], F) : .my_name(Me)
	& jia.req.delegateItems(Shop, Items, F, Me, Agent, Carry, Rest) <-
	+assistant(Agent, Shop, Carry); 
	!delegateItems([map(Shop, Rest)|ShopList], F).
+!delegateItems([map(Shop, Items)|ShopList], F)					<-
	!retrieveItems(Shop, Items);
	!delegateItems(ShopList, F).
	
+!delegateTools([], _).
+!delegateTools(Tools, F) : .my_name(Me) & .print("delegateTools: ", Tools)
	& jia.req.delegateTools(Tools, F, Me, Agent, Carry, Rest) <-
	+assistant(Agent, "tool", Carry); 
	!delegateTools(Rest, F).
+!delegateTools(Tools, F) <-
	.wait({+step(_)});
	!delegateTools(Tools, F).

+!coordinateAssemble(Items, Tools, _) : not assistant(_, _, _) & hasTools(Tools).
+!coordinateAssemble(Items, Tools, F) : not assistant(_, _, _) & getInventory(Inv) <-
	getMissingTools(Tools, Inv, MissingTools);
	!!getToFacility(F);
	!delegateTools(MissingTools, F);
	.wait(inFacility(F));
	!initiateAssembleProtocol(Items).
+!coordinateAssemble(Items, Tools, F) : getInventory(Inv) <-
	.findall(I, assistant(X, _, _) & getInventory(X, I), AllInv);
	collectInventories([Inv|AllInv], Inventory);
	getMissingTools(Tools, Inventory, MissingTools);
	!!getToFacility(F);
	!delegateTools(MissingTools, F);
	.wait(inFacility(F));
	!initiateAssembleProtocol(Items).	
	
+!acquireTools : tools(Tools) <-
	sortByPermissionCount(Tools, SortedTools);
	for (.member(T, SortedTools)) {
		jia.items.getVolume(T, V); ?capacity(C);
		if (C >= V) { !retrieveTool(T);	}
	}.
-!acquireTools <- .wait(100); !acquireTools.

+!retrieveTools([]).
+!retrieveTools([Tool|Tools]) <-
	!retrieveTool(Tool);
	!retrieveTools(Tools).

+!retrieveTool(Tool) : hasTools([Tool]).
+!retrieveTool(Tool) : getClosestShopSelling(Tool, Shop) <-
	!retrieveItems(Shop, [map(Tool, 1)]).

+!coordinateAssemble(Items, [], F) <-
	!getToFacility(F);
	!initiateAssembleProtocol(Items).

+!coordinateAssist(Workshop, Agent) <-
	!getToFacility(Workshop);
	!acceptAssembleProtocol(Agent).
	