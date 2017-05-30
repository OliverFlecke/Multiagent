
+!delieverItems(Job, Facility) <- 
	!getToFacility(Facility);
 	!doAction(deliver_job(Job)).
 	
+!assembleItems([]).
+!assembleItems([map(	_, 		0) | Items]) <- !assembleItems(Items).
+!assembleItems([map(Item, Amount) | Items]) : inWorkshop <- 
	getRequiredItems(Item, ReqItems);
	!assembleItem(Item, ReqItems); 
	!assembleItems([map(Item, Amount - 1) | Items]).
// Get to workshop
+!assembleItems(Items) <-
	getClosestFacility("workshop", Workshop);	
	!getToFacility(Workshop);
	!assembleItems(Items).
	
// Recursively assemble required items
+!assembleItem(	  _, 	   []).
+!assembleItem(Item, ReqItems) : inWorkshop <- 
//	.print("Assembling item: ", Item);
	!assembleItems(ReqItems);
	!doAction(assemble(Item)).
-!assembleItem(Item) <- .print("Could not assemble item").

+!retrieveItems([]).
+!retrieveItems([map(	_, 		0) | Items]) <- !retrieveItems(Items).
+!retrieveItems([map(Item, Amount) | Items]) <- 
	getShopSelling(Item, Amount, Shop, AmountAvailable);
	!getToFacility(Shop);
	!doAction(buy(Item, AmountAvailable));
  	!retrieveItems([map(Item, Amount - AmountAvailable) | Items]).
	
+!retrieveTools([]).
+!retrieveTools([Tool | Tools]) : have(Tool) 	<- !retrieveTools(Tools).
+!retrieveTools([Tool | Tools]) 				<- !retrieveTool(Tool);	!retrieveTools(Tools).
+!retrieveTool(Tool) <-
	canUseTool(Tool, CanUse);
	if (CanUse)
	{
		!retrieveItems([map(Tool, 1)]);
	}
	else { .print("Can't use the tool"); } // Need help from someone that can use this tool
	.
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) : not enoughCharge & not isChargingStation(F) 	<- .print("Need to charge"); !charge; !getToFacility(F).
+!getToFacility(F) 													<- !doAction(goto(F)); !getToFacility(F).

+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation <- !doAction(charge); !charge.
+!charge <- 
	getClosestFacility("chargingStation", ChargingStation);
	!getToFacility(ChargingStation);
	!charge.
	 
+!doAction(Action) : step(X) <-
//	.wait(step(X));
//	.print(X);	
	action(Action).