+!deliverJob(_, [], _).
+!deliverJob(Id, Items, F) : hasItems(Items) & inFacility(F) <- !doAction(deliver_job(Id)).
+!deliverJob(Id, Items, F) : hasItems(Items) 				 <- !getToFacility(F); !deliverJob(Id, Items, F).
+!deliverJob(Id, Items, F)									 <- !delegateJob(Id, Items, F).

+!delegateJob(_, [], _).
+!delegateJob(Id, Items, F) : freeAgents(N) & N > 0 <- .print("Help").
+!delegateJob(Id, Items, F) : capacity(C) 			<- 
	getItemsToCarry(Items, C, ItemsToCarry, Rest);
	!acquireItems(ItemsToCarry); 
	!deliverJob(Id, ItemsToCarry, F);
	!delegateJob(Id, Rest, F).

+!acquireItems([]).
+!acquireItems(Items) : hasItems(Items).
+!acquireItems(Items) : hasBaseItems(Items) <- 
	getClosestFacility("workshop", F);
	!getToFacility(F);
	!assembleItems(Items).
+!acquireItems(Items)						<- 
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!delegateItems(ShoppingList);
	!acquireItems(Items).

+!delegateItems([]).
+!delegateItems([map(_,[])|ShopList]) 								<- !delegateItems(ShopList).
+!delegateItems([map(Shop,Items)|ShopList]) : freeAgents(N) & N > 0 <- .print("Help").
+!delegateItems([map(Shop,Items)|ShopList]) : capacity(C) 			<-
	getItemsToCarry(Items, C, ItemsToCarry, Rest);
	!retrieveItems(Shop, ItemsToCarry);
	!delegateItems([map(Shop,Rest)|ShopList]).
	
+!retrieveItems(_, []).
+!retrieveItems(_, Items) : hasItems(Items).
+!retrieveItems(Shop, Items) : inShop(Shop) <- !buyItems(Items).
+!retrieveItems(Shop, Items) 				<- !getToFacility(Shop); !retrieveItems(Shop, Items).

+!assembleItems([]).
+!assembleItems([map(_, 0)|Items]) 		 	<- !assembleItems(Items).
+!assembleItems([map(Item, Amount)|Items]) 	<- 
	getRequiredItems(Item, ReqItems);
	!assembleItems(ReqItems);
	!doAction(assemble(Item)); 
	!assembleItems([map(Item, Amount - 1) | Items]).
		
+!buyItems([]).
+!buyItems([map(Item, 0)|Items]) 		<- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) 	<- 
	getAvailableAmount(Item, Amount, Shop, AmountAvailable);
	!doAction(buy(Item, AmountAvailable));
	!buyItems(Items);
	!buyItems([map(Item, Amount - AmountAvailable)]).

//+!assistAssemble(Agent) : getInventory([]) | assembleComplete.
//+!assistAssemble(Agent) : connection(Agent, Entity, _) <-
//	!doAction(assist_assemble(Entity));
//	!assistAssemble(Agent).

+!getToFacility(F) : inFacility(F).
+!getToFacility(F) : not canMove			<- !doAction(recharge); !getToFacility(F).
+!getToFacility(F) : isChargingStation(F) 	<- !doAction(goto(F)); 	!getToFacility(F).
+!getToFacility(F) : not enoughCharge 		<- !charge; 			!getToFacility(F).
+!getToFacility(F) 							<- !doAction(goto(F)); 	!getToFacility(F).

+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation 			<- !doAction(charge); !charge.
+!charge 								<-
	getClosestFacility("chargingStation", F);
	!getToFacility(F); 
	!charge.
	
