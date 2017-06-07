
+!giveItems(_, []).
+!giveItems(Agent, [map(Item, Amount)|Items]) : connection(Agent, Entity, _) <-
	!doAction(give(Entity, Item, Amount));
	!giveItems(Agent, Items).
	
+!receiveItems([]).
+!receiveItems([_|Items]) <-
	!doAction(receive);
	!receiveItems(Items).
	
+!buyItems([]).
+!buyItems([map(Item, 	   0)|Items]) <- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) : inShop(Shop) <- 
	getAvailableAmount(Item, Amount, Shop, AmountAvailable);
	if (AmountAvailable > 0) { !doAction(buy(Item, AmountAvailable)); }
	.concat(Items, [map(Item, Amount - AmountAvailable)], NewItems);
	!buyItems(NewItems).
	
+!delieverItems(TaskId, Facility) <- 
	!getToFacility(Facility);
 	!doAction(deliver_job(TaskId)).
 	
+!assembleItems([]).
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

+!retrieveTools([]).
+!retrieveTools([Tool | Tools]) : have(Tool) 	<- !retrieveTools(Tools).
+!retrieveTools([Tool | Tools]) 				<- !retrieveTool(Tool);	!retrieveTools(Tools).
+!retrieveTool(Tool) : canUseTool(Tool) 		<- !retrieveItems([map(Tool, 1)]).
+!retrieveTool(Tool) 							<- .print("Can not use ", Tool). // Need help from someone that can use this tool
	
+!getToFacility(F) : inFacility(F).
+!getToFacility(F) : charge(X) & X < 20 & not inChargingStation 	<- !charge; !getToFacility(F).
+!getToFacility(F) 													<- !doAction(goto(F)); !getToFacility(F).

+!charge : charge(X) & maxCharge(X)		<- -charging.
+!charge : inChargingStation 			<- !doAction(charge); !charge.
+!charge : charge(X) & X < 10			<- !doAction(recharge); !charge.
+!charge <-
	+charging;
	getClosestFacility("chargingStation", F);
	!getToFacility(F); 
	!charge.
	
+!gather : inResourceNode	<- !doAction(gather); !gather.
+!gather 					<- 
	getClosestFacility("resourceNode", F);
	if (not (F == "none"))
	{
		!getToFacility(F);
		!gather;
	}
	else { .print("Can not find any resource nodes"); }.
