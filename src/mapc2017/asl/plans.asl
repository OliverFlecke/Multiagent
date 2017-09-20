// Solve job
+!doTask(Id, Items, Storage, ShoppingList, Workshop) <-
	!acquireItems(ShoppingList);
	!getToFacility(Workshop);
	!initiateAssembleProtocol(Items);
	!getToFacility(Storage); 
	!deliverJob(Id).
	
// Assist job
+!doTask(Agent, ShoppingList, Workshop) <-
	.send(Agent, tell, assistant);
	!acquireItems(ShoppingList);
	!getToFacility(Workshop);
	!acceptAssembleProtocol(Agent);
	.send(Agent, untell, assistant).
	
// Bid job
+!doTask(Id, Bid) <- !bidForJob(Id, Bid).

+!deliverJob( _) : lastAction("deliver_job").
+!deliverJob(Id) <- !doAction(deliver_job(Id)); !deliverJob(Id).

+!acquireItems([]).
+!acquireItems([map(Shop, Items)|ShoppingList]) <- 
	!retrieveItems(Shop, Items);
	!acquireItems(ShoppingList).

+!retrieveItems(   _, Items) : hasItems(Items).
+!retrieveItems(Shop, Items) : facility(Shop) <- 
	!buyItems(Items).
+!retrieveItems(Shop, Items) <- 
	!getToFacility(Shop); 
	!buyItems(Items).
	
// Pre-condition: In shop and shop selling the items.
// Post-condition: Items in inventory.
+!buyItems(Items) 		 : hasItems(Items).
+!buyItems([Item|Items]) : hasItems([Item]) <- !buyItems(Items).
//+!buyItems([map(Item, Amount)|Items]) : buyAmount(Item, Amount, 0) 
//	& getAlternativeShop(Item, Amount, Shop) <- 
//	!retrieveItems(Shop, [map(Item, Amount)|Items]).
+!buyItems([map(Item, Amount)|Items]) : buyAmount(Item, Amount, 0) <- 
	!doAction(recharge); 
	!buyItems([map(Item, Amount)|Items]).
+!buyItems([map(Item, Amount)|Items]) : buyAmount(Item, Amount, BuyAmount) <- 
	!doAction(buy(Item, BuyAmount));
	!buyItems(Items);
	!buyItems([map(Item, Amount)]).
	
+!buyLeastAvailableItems : getClosestFacility("shop", Shop)
						 & getLeastAvailableItems(Shop, Items) <-
	!getToFacility(Shop);
	!buyItems(Items).
-!buyLeastAvailableItems.


// Pre-condition: In workshop and all base items available.
// Post-condition: Items in inventory.
+!assembleItems(Items) : hasItems(Items).
+!assembleItems([map(Name, _)|Items]) : getReqItems(Name, []) <- 
	!assembleItems(Items).
+!assembleItems([map(Name, Amount)|Items]) : getReqItems(Name, ReqItems) <-
	!assembleItem(map(Name, Amount), ReqItems); 
	!assembleItems(Items).

// Pre-condition: In workshop and base items available.
// Post-condition: Amount of Item with Name in inventory.
+!assembleItem(Item, _) : hasItems([Item]).
+!assembleItem(   _, _) : lastActionResult("failed_location") <- .fail.
+!assembleItem(map(Name, Amount), ReqItems) : hasItems(ReqItems) <- 
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).
+!assembleItem(map(Name, Amount), ReqItems) <- 
	!assembleItems(ReqItems);
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).

// Post-condition: Empty inventory or -assemble.
+!assistAssemble(Agent) :         not assemble[source(Agent)].
+!assistAssemble(Agent) : load(0) <- -assemble[source(Agent)].
+!assistAssemble(    _) : lastActionResult("failed_location") <- .fail.
+!assistAssemble(Agent) <-
	!doAction(assist_assemble(Agent));
	.wait(200); // Allow assembler to remove assemble in time.
	!assistAssemble(Agent).

// Post-condition: In facility F.
+!getToFacility(F) : facility(F).
+!getToFacility(F) : isChargingStation(F) <- 		  !goToFacility(F).
+!getToFacility(F) : not enoughCharge(F)  <- !charge; !goToFacility(F).
+!getToFacility(F) 						  <- 		  !goToFacility(F).

// Prevents checking enoughCharge multiple times.
+!goToFacility(F) : facility(F).
+!goToFacility(F) : lastActionResult("failed_no_route") <- 
	?getRandomLocation(Lat, Lon); 
	!doAction(goto(Lat, Lon)); 
	!goToFacility(F).
+!goToFacility(F) : not canMove	<- !doAction(recharge); !goToFacility(F).
+!goToFacility(F) 				<- !doAction(goto(F)); 	!goToFacility(F).

// Does not check charge, use with care.
+!goToLocation(F) 		 : getFacilityLocation(F, Lat, Lon) <- !goToLocation(Lat, Lon).
+!goToLocation(Lat, Lon) : atLocation(Lat, Lon).
+!goToLocation(Lat, Lon) : lastActionResult("failed_no_route")  <- 
	?getRandomLocation(Lat2, Lon2); 
	!doAction(goto(Lat2, Lon2)); 
	!goToLocation(Lat, Lon).
+!goToLocation(Lat, Lon)							 			<- !doAction(goto(Lat, Lon)); !goToLocation(Lat, Lon).

// Post-condition: At random location.
+!goToRandom : getRandomLocation		(Lat, Lon) <- !goToLocation(Lat, Lon).
+!goToCenter : getRandomCenterLocation	(Lat, Lon) <- !goToLocation(Lat, Lon).

// Post-condition: Full charge.
+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation & not lastActionResult("failed_facility_state") <- !doAction(charge); !charge.
+!charge : getClosestFacility("chargingStation", F) 						 <- !goToFacility(F);  !charge.

+!gather : capacity(C) & maxLoad(L) & C <= 0.8 * L.
+!gather : inResourceNode 						 <- !doAction(gather);    !gather.
//+!gather : getClosestFacility("resourceNode", F) <- !goToLocation(F);  	  !gather.
+!gather : getResourceNode(F) 					 <- !goToLocation(F);  	  !gather.
+!gather  			 							 <- !goToRandom; !charge; !gather.

+!bidForJob( _,   _) : lastAction("bid_for_job").
+!bidForJob(Id, Bid) <- 
	!doAction(bid_for_job(Id, Bid)); 
	!bidForJob(Id, Bid).

+!skip 					<- while (true)		   { !doAction(recharge) }.
+!skip(Literal) 		<- while (not Literal) { !doAction(recharge) }.
+!skip(Literal, Time)  	<- while (not Literal) { !doAction(recharge); .wait(Time) }.