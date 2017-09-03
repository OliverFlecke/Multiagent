// Solve job
+!doTask(Id, Items, Storage, ShoppingList, Workshop) <-
	!acquireItems(ShoppingList);
	!getToFacility(Workshop);
	!initiateAssembleProtocol(Items);
	!getToFacility(Storage); 
	!doAction(deliver_job(Id)).
	
// Assist job
+!doTask(Agent, ShoppingList, Workshop) <-
	.send(Agent, tell, assistant);
	!acquireItems(ShoppingList);
	!getToFacility(Workshop);
	!acceptAssembleProtocol(Agent);
	.send(Agent, untell, assistant).
	
// Bid job
+!doTask(Id, Bid) <- !doAction(bid_for_job(Id, Bid)).

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
+!buyItems([Item|Items]) : hasItems([Item]) <- 
	!buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) : buyAmount(Item, Amount, BuyAmount) <- 
	!doAction(buy(Item, BuyAmount));
	!buyItems(Items);
	!buyItems([map(Item, Amount)]).

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
+!assembleItem(map(Name, Amount), ReqItems) : hasItems(ReqItems) <- 
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).
+!assembleItem(map(Name, Amount), ReqItems) <- 
	!assembleItems(ReqItems);
	!doAction(assemble(Name));
	!assembleItem(map(Name, Amount), ReqItems).

// Post-condition: Empty inventory or -assemble.
+!assistAssemble(Agent) :         not assemble[source(Agent)].
//+!assistAssemble(Agent) : load(0) <- -assemble[source(Agent)].
+!assistAssemble(Agent) <-
	!doAction(assist_assemble(Agent));
	.wait(1000); // Allow assembler to remove assemble in time.
	!assistAssemble(Agent).

// Post-condition: In facility F.
+!getToFacility(F) : facility(F).
+!getToFacility(F) : isChargingStation(F) <- 		  !goToFacility(F).
+!getToFacility(F) : not enoughCharge(F)  <- !charge; !goToFacility(F).
+!getToFacility(F) 						  <- 		  !goToFacility(F).

// Prevents checking enoughCharge multiple times.
+!goToFacility(F) : facility(F).
//+!goToFacility(F) : lastActionResult("failed_no_route") <- 
+!goToFacility(F) : not canMove	<- !doAction(recharge); !goToFacility(F).
+!goToFacility(F) 				<- !doAction(goto(F)); 	!goToFacility(F).

// Does not check charge, use with care.
+!goToLocation(F) : getFacilityLocation(F, Lat, Lon) <- !goToLocation(Lat, Lon).
+!goToLocation(Lat, Lon) : atLocation(Lat, Lon).
+!goToLocation(Lat, Lon)							 <- !doAction(goto(Lat, Lon)); !goToLocation(Lat, Lon).

// Post-condition: At random location.
+!random : getRandomLocation(Lat, Lon) <- !goToLocation(Lat, Lon).

// Post-condition: Full charge.
+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation 						<- !doAction(charge); !charge.
+!charge : getClosestFacility("chargingStation", F) <- !goToFacility(F);  !charge.

+!gather : capacity(C) & load(L) & (C <= 150 | C <= 0.5 * L).
+!gather : inResourceNode 						 <- !doAction(gather); !gather.
+!gather : getClosestFacility("resourceNode", F) <- !goToLocation(F);  !gather.
+!gather  			 							 <- !random; !charge;  !gather.

+!skip <- !doAction(skip); !skip.