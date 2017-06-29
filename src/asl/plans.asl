+!deliverJob(Id, Items, F) : hasItems(Items) & inFacility(F) <- !doAction(deliver_job(Id)).
+!deliverJob(Id, Items, F) : hasItems(Items) 				 <- !getToFacility(F); !deliverJob(Id, Items, F).
+!deliverJob(Id, Items, F)									 <- !delegateJob(Id, Items, F).

+!delegateJob( _,    [], _).
+!delegateJob(Id, Items, F) : canCarry(Items) 					  <- !solveJob(Id, Items, F).
+!delegateJob(Id, Items, F) : jia.delegateJob(Id, Items, F, Rest) <- !delegateJob(Id, Rest, F).
+!delegateJob(Id, Items, F) : capacity(C) 						  <- 
	getItemsToCarry(Items, C, ItemsToCarry, Rest);
	!solveJob(Id, ItemsToCarry, F);
	!delegateJob(Id, Rest, F).

// Pre-condition: Items can be carried by agent
+!solveJob(Id, Items, Storage) : .print("Solving ", Id, " ", Items) & false.
+!solveJob(Id, Items, Storage) <- 
	getClosestWorkshopToStorage(Storage, Workshop);
	getRequiredTools(Items, Tools);
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!delegateItems(ShoppingList, Workshop);
	!coordinateAssemble(Items, Tools, Workshop);
	!deliverJob(Id, Items, Storage).

+!delegateItems([                         ], _).
+!delegateItems([map(   _,    [])|ShopList], F) 				<- !delegateItems(ShopList, F).
+!delegateItems([map(Shop, Items)|      []], F) 				<- !retrieveItems(Shop, Items).
+!delegateItems([map(Shop, Items)|ShopList], F) : .my_name(Me)
	& jia.delegateItems(Shop, Items, F, Me, Agent, Carry, Rest) <-
	+assistant(Agent, Shop, Carry); 
	!delegateItems([map(Shop, Rest)|ShopList], F).
+!delegateItems([map(Shop, Items)|ShopList], F)					<-
	!retrieveItems(Shop, Items);
	!delegateItems(ShopList, F).

+!retrieveItems(   _, Items) : hasItems(Items).
+!retrieveItems(Shop, Items) : inShop(Shop) 	<- !buyItems(Items).
+!retrieveItems(Shop, Items) : .print("Retrieving: ", Shop, " ", Items) & false.
+!retrieveItems(Shop, Items) <- !getToFacility(Shop); !retrieveItems(Shop, Items).

// Pre-condition: In workshop and all base items available.
// Post-condition: Items in inventory.
+!assembleItems(Items) : hasItems(Items).
+!assembleItems([map(Name,      _)|Items]) : jia.getReqItems(Name, []) 		 <- 
	!assembleItems(Items).
+!assembleItems([map(Name, Amount)|Items]) : jia.getReqItems(Name, ReqItems) <-
	!assembleItem(map(Name, Amount), ReqItems); 
	!assembleItems(Items).

// Pre-condition: In workshop and base items available.
// Post-condition: Amount of Item with Name in inventory.
+!assembleItem(Item, _) : hasItems([Item]).
+!assembleItem(map(Name, Amount), ReqItems) : hasItems(ReqItems) <- 
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).
+!assembleItem(map(Name, Amount), ReqItems) <- 
	!assembleItems(ReqItems);
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).

+!coordinateAssemble(Items, [], F) <-
	!getToFacility(F);
	!initiateAssembleProtocol(Items).
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
	
+!delegateTools([], _).
+!delegateTools(Tools, F) : .my_name(Me) & .print("delegateTools: ", Tools)
	& jia.delegateTools(Tools, F, Me, Agent, Carry, Rest) <-
	+assistant(Agent, "tool", Carry); 
	!delegateTools(Rest, F).
+!delegateTools(Tools, F) <-
	.wait({+step(_)});
	!delegateTools(Tools, F).	

+!coordinateAssist(Workshop, Agent) <-
	!getToFacility(Workshop);
	!acceptAssembleProtocol(Agent).
	
+!acquireTools : tools(Tools) <-
	sortByPermissionCount(Tools, SortedTools);
	for (.member(T, SortedTools)) {
		getToolVolume(T, V); ?capacity(C);
		if (C >= V) { !retrieveTool(T);	}
	}.
-!acquireTools <- .wait(100); !acquireTools.

+!retrieveTools([]).
+!retrieveTools([Tool|Tools]) <-
	!retrieveTool(Tool);
	!retrieveTools(Tools).

+!retrieveTool(Tool) : hasTools([Tool]).
+!retrieveTool(Tool) <- 
	getClosestShopSelling(Tool, Shop);
	!retrieveItems(Shop, [map(Tool, 1)]).

// Post-condition: Empty inventory or -assemble.
+!assistAssemble(    _) : load(0) | not assemble.
+!assistAssemble(Agent) <-
	!doAction(assist_assemble(Agent));
	.wait(1000); // To allow assembler to remove assemble
	!assistAssemble(Agent).
	
// Pre-condition: In shop and shop selling the items.
// Post-condition: Items in inventory.
//+!buyItems([]) <- !doAction(skip). // To prevent duplicate purchases.
+!buyItems(Items) 		 : hasItems(Items).
+!buyItems([Item|Items]) : hasItems([Item]) <- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items])		<- 
	?hasAmount(Item, HasAmount); ?inShop(Shop);
	getAvailableAmount(Item, Amount - HasAmount, Shop, AmountAvailable);
	!doAction(buy(Item, AmountAvailable));
	!buyItems(Items);
	!buyItems([map(Item, Amount)]).

// Post-condition: In facility F.
+!getToFacility(F) : inFacility(F).
+!getToFacility(F) : not canMove									<- !doAction(recharge); !getToFacility(F).
+!getToFacility(F) : not enoughCharge & not isChargingStation(F)	<- !charge; 			!getToFacility(F).
+!getToFacility(F) 													<- !doAction(goto(F)); 	!getToFacility(F).

// Post-condition: Full charge.
+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation <- !doAction(charge); !charge.
+!charge <-	getClosestFacility("chargingStation", F); !getToFacility(F); !charge.