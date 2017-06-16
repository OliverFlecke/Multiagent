
+!giveItems(_, []).
+!giveItems(Agent, [map(Item, Amount)|Items]) : connection(Agent, Entity, _) <-
	!doAction(give(Entity, Item, Amount));
	!giveItems(Agent, Items).
	
+!receiveItems([]).
+!receiveItems([_|Items]) <-
	!doAction(receive);
	!receiveItems(Items).
	
+!retrieveItems(map(Shop, Items)) <-
	!getToFacility(Shop);
	!buyItems(Items).
	
+!buyItems([]).
+!buyItems([map(Item, 	   0)|Items]) <- !buyItems(Items).
+!buyItems([map(Item, Amount)|Items]) : inShop(Shop) <- 
	getAvailableAmount(Item, Amount, Shop, AmountAvailable);
	if (AmountAvailable > 0) { !doAction(buy(Item, AmountAvailable)); }
	.concat(Items, [map(Item, Amount - AmountAvailable)], NewItems);
	!buyItems(NewItems).
	
+!deliverItems(TaskId, Facility) <- 
	!getToFacility(Facility);
 	!doAction(deliver_job(TaskId)).
 	
+!assembleItems([]).
+!assembleItems([map(	_, 		0) | Items]) <- !assembleItems(Items).
+!assembleItems([map(Item, Amount) | Items]) <- 
	getRequiredItems(Item, ReqItems);
	!assembleItem(Item, ReqItems); 
	!assembleItems([map(Item, Amount - 1) | Items]).
	
// Recursively assemble required items
+!assembleItem(	  _, 	   []).
+!assembleItem(Item, ReqItems) <-
	!assembleItems(ReqItems);
	!doAction(assemble(Item)).
	
+!assistAssemble(Agent) : assembleComplete[source(S)] <- -assembleComplete[source(S)].
+!assistAssemble(Agent) : connection(Agent, Entity, _) <-
	!doAction(assist_assemble(Entity));
	!assistAssemble(Agent).

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
