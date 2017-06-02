
+!delieverItems(TaskId, Facility) <- 
	!getToFacility(Facility);
 	!doAction(deliver_job(TaskId)).
 	
+!assembleItems([]).
//+!assembleItems(_) : itemsToRetrieve(Items) & not Items = [] <- !retrieveItems; !assembleItems.
+!assembleItems(Items) : not itemsToRetrieve([]) <- !retrieveItems; !assembleItems(Items).
+!assembleItems([map(	_, 		0) | Items]) <- !assembleItems(Items).
+!assembleItems([map(Item, Amount) | Items]) : inWorkshop <- 
	getRequiredItems(Item, ReqItems);
	!assembleItem(Item, ReqItems); 
	!assembleItems([map(Item, Amount - 1) | Items]).
// Get to workshop
+!assembleItems(Items) <-	
	getClosestFacility("workshop", F);
	!getToFacility(F);
	!assembleItems(Items).
	
// Recursively assemble required items
+!assembleItem(	  _, 	   []).
+!assembleItem(Item, ReqItems) : inWorkshop <- 
//	.print("Assembling item: ", Item);
	!assembleItems(ReqItems);
	!doAction(assemble(Item)).
-!assembleItem(Item) <- .print("Could not assemble item").
  	
+!retrieveItems : itemsToRetrieve([]).
+!retrieveItems : itemsToRetrieve([map(   _,      0) | Items]) <- 
	-+itemsToRetrieve(Items); 
	!retrieveItems.
+!retrieveItems : itemsToRetrieve([map(Item, Amount) | Items]) <- 
	getShopSelling(Item, Amount, Shop, AmountAvailable);
	!getToFacility(Shop);
	!doAction(buy(Item, AmountAvailable));
	-+itemsToRetrieve([map(Item, Amount - AmountAvailable) | Items]);
  	!retrieveItems.

+!addItemsToRetrieve(ItemsToAdd) : itemsToRetrieve(Items) <- .concat(Items, ItemsToAdd, NewItems); -+itemsToRetrieve(NewItems).
	
+!retrieveTools([]).
+!retrieveTools([Tool | Tools]) : have(Tool) 	<- !retrieveTools(Tools).
+!retrieveTools([Tool | Tools]) 				<- !retrieveTool(Tool);	!retrieveTools(Tools).
+!retrieveTool(Tool) : canUseTool(Tool) 		<- !retrieveItems([map(Tool, 1)]).
+!retrieveTool(Tool) 							<- .print("Can not use ", Tool). // Need help from someone that can use this tool
	
+!getToFacility(F) : inFacility(F).
//+!getToFacility(F) : routeDuration(1).
+!getToFacility(F) : not enoughCharge & not isChargingStation(F) 	<- !charge; !getToFacility(F).
+!getToFacility(F) 													<- !doAction(goto(F)); !getToFacility(F).

+!charge : charge(X) & maxCharge(X).
+!charge : charge(X) & X < 10 & not inChargingStation 	<- !doAction(recharge); !charge.
+!charge : inChargingStation 							<- !doAction(charge); !charge.
+!charge <-
	getClosestFacility("chargingStation", F);
	!getToFacility(F); 
	!charge.
