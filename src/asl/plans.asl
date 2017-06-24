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

// Agent should be able to carry all items at this point
+!solveJob(Id, Items, F) : .print("Solving ", Id, " ", Items) & false.
+!solveJob(Id, Items, F) <- !acquireItems(Items); !deliverJob(Id, Items, F).

+!acquireItems(Items) : hasItems(Items).
+!acquireItems(Items) : hasBaseItems(Items) <- 
	getClosestFacility("workshop", F);
	!getToFacility(F);
	!assembleItems(Items).
+!acquireItems(Items) : itemRetriever(_) & getInventory(Inventory) <-
	.findall(Inv, itemRetriever(A) & getInventory(A, Inv), AllInv);
	collectInventories([Inventory|AllInv], InvItems);
	getMissingItems(Items, InvItems, Missing);
	!acquireItems(Missing).
+!acquireItems(Items) 						<- 
	getClosestFacility("workshop", F);
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!delegateItems(ShoppingList, F);
	!coordinateAssemble(Items, F);
	!acquireItems(Items).

+!delegateItems([                         ], _).
+!delegateItems([map(   _,    [])|ShopList], F) 				<- !delegateItems(ShopList, F).
+!delegateItems([map(Shop, Items)|      []], F) 				<- .print("Retrieving: ", Shop, " ", Items); !retrieveItems(Shop, Items).
+!delegateItems([map(Shop, Items)|ShopList], F) : .my_name(Me)
	& jia.delegateItems(Shop, Items, F, Me, Agent, Rest) 		<-
	+itemRetriever(Agent); 
	!delegateItems([map(Shop, Rest)|ShopList], F).
+!delegateItems([map(Shop, Items)|ShopList], F) : not canCarry(Items) <- .print("delegateItems - ERROR").
+!delegateItems([map(Shop, Items)|ShopList], F)					<-  .print("Retrieving: ", Shop, " ", Items);
	!retrieveItems(Shop, Items);
	!delegateItems(ShopList, F).
	
+!retrieveItems(   _, Items) : hasItems(Items).
+!retrieveItems(Shop, Items) : inShop(Shop) 	<- !buyItems(Items); 	 !retrieveItems(Shop, Items).
+!retrieveItems(Shop, Items) 					<- !getToFacility(Shop); !retrieveItems(Shop, Items).

+!assembleItems([                       ]).
+!assembleItems([map(	_, 		0)|Items]) <- !assembleItems(Items).
+!assembleItems([map(Item, Amount)|Items]) <- 
	getRequiredItems(Item, ReqItems);
	!assembleItem(Item, ReqItems); 
	!assembleItems([map(Item, Amount - 1)|Items]).

+!assembleItem(	  _, 	   []).
+!assembleItem(Item, ReqItems) <-
	!assembleItems(ReqItems);
	!doAction(assemble(Item)).

+!coordinateAssemble(    _, _) : not itemRetriever(_).
+!coordinateAssemble(Items, F) : not inFacility(F) <- !getToFacility(F); !coordinateAssemble(Items, F).
+!coordinateAssemble(Items, _) 					   <- !initiateAssembleProtocol(Items).

+!assistRetrieve(Shop, Items, _, Agent) : .print("Assisting ", Agent, " ", Shop, " ", Items) & false.
+!assistRetrieve(Shop, Items, F, Agent) <- 
	!retrieveItems(Shop, Items);
	!getToFacility(F);
	!acceptAssembleProtocol(Agent).

+!assistAssemble(    _) : getInventory([]) | assembleComplete.
+!assistAssemble(Agent) <-
	!doAction(assist_assemble(Agent));
	!assistAssemble(Agent).

+!buyItems([                       ]).
+!buyItems([map(Item,      0)|Items]) 				 <- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) : inShop(Shop) <- 
	getAvailableAmount(Item, Amount, Shop, AmountAvailable);
	!doAction(buy(Item, AmountAvailable));
	!buyItems(Items);
	!buyItems([map(Item, Amount - AmountAvailable)]).

+!getToFacility(F) : inFacility(F).
+!getToFacility(F) : not canMove			<- !doAction(recharge); !getToFacility(F).
+!getToFacility(F) : isChargingStation(F) 	<- !doAction(goto(F)); 	!getToFacility(F).
+!getToFacility(F) : not enoughCharge 		<- !charge; 			!getToFacility(F).
+!getToFacility(F) 							<- !doAction(goto(F)); 	!getToFacility(F).

+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation <- !doAction(charge); !charge.
+!charge 					 <-
	getClosestFacility("chargingStation", F);
	!getToFacility(F); !charge.
