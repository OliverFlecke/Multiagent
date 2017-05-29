{ include("connections.asl") }
// Initial beliefs
free.

// Rules
myName(Name)	:- .my_name(Me) & .term2string(Me, Name).
myRole(Role) 	:- myName(Name) & myRole(Name, Role).

// Personal percepts
inFacility(F) 		:- myName(Name) & inFacility(Name, F).
charge(C) 			:- myName(Name) & charge(Name, C).
load(L)				:- myName(Name) & load(Name, L).
routeLength(L)		:- myName(Name) & routeLength(Name, L).
lastAction(A)		:- myName(Name) & lastAction(Name, A).
lastActionResult(R) :- myName(Name) & lastActionResult(Name, R).

speed(S)		:- myRole(Role) & role(Role, S, _, _, _).
maxLoad(L)		:- myRole(Role) & role(Role, _, L, _, _).
maxCharge(C)	:- myRole(Role) & role(Role, _, _, C, _).
chargeThreshold(100). // Should the threshold be dependent on the type of vehicle (properly) 
// Don't know if we will need to know if they travel by road or air

// Check facility type
isChargingStation(F)	:- .substring("chargingStation", F).
isWorkshop(F)			:- .substring("workshop", F).
isStorage(F)			:- .substring("storage",  F).
isShop(F)				:- .substring("shop",     F).

// Check if agent is in this type of facility
inChargingStation 	:- inFacility(F) & isChargingStation(F).
inWorkshop 			:- inFacility(F) & isWorkshop(F).
inStorage 			:- inFacility(F) & isStorage(F).
inShop	    		:- inFacility(F) & isShop(F).
inShop(X)			:- inFacility(F) & inShop & .substring(X, F).

contains(map(Item, X), [map(Item, Y) | _]) 	:- X <= Y. 		// There is a .member function, but we need to unwrap the objects
contains(Item, [_ | Inventory]) 			:- contains(Item, Inventory). 

have(I) :- .my_name(Me) & getAgentInventory(Agent, Inv) & .member(I, Inv).

enoughCharge :- routeLength(L) & speed(S) & charge(C) & chargeThreshold(Threshold) & 
				Steps = math.ceil(L / S) & Steps <= (C - Threshold) / 10.
				
// Initial goals
!register.
!focusArtifacts.

// Plans 
//+step(X) <- +newStep.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
//	!focusArtifact("AgentArtifact");
//	!focusArtifact("ItemArtifact");
//	!focusArtifact("FacilityArtifact");
	!focusArtifact("TaskArtifact");
<<<<<<< HEAD
=======
	!focusArtifact("AgentArtifact");
	!focusArtifact("FacilityArtifact");
>>>>>>> 94a2a30c35dc77c2fc555aca20c2997ab1ba34ea
	!focusArtifact("EIArtifact").	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.

+task(TaskId, DeliveryLocation, [Item|Items], _, CNPName) : free & .my_name(agentA1) <-
	lookupArtifact(CNPName, CNPId);
	bid(20)[artifact_id(CNPId)];
	winner(Name)[artifact_id(CNPId)];
	if ( myName(Name) )
	{
		!solveJob(TaskId, DeliveryLocation, [Item|Items])
	}.
	
<<<<<<< HEAD
+!solveJob(TaskId, DeliveryLocation, [Item|Items]) <-
=======
+winner(Name, TaskId) : myName(Name) <-
>>>>>>> 94a2a30c35dc77c2fc555aca20c2997ab1ba34ea
	-free;
	if ( not Items = [] )
	{
		announce(TaskId, DeliveryLocation, Items, "partial");
	}
	!solvePartialJob(TaskId, DeliveryLocation, Item);
	+free.
	
<<<<<<< HEAD
+free : task(TaskId, DeliveryLocation, Items, "partial", CNPName) & .my_name(agentA1) <-
	.print("Partial");
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveJob(TaskId, DeliveryLocation, Items).
	
+free : task(TaskId, DeliveryLocation, Items, _, CNPName) & .my_name(agentA1) <-
	.print("Non-Partial");
	lookupArtifact(CNPName, CNPId);
	takeTask[artifact_id(CNPId)];
	!solveJob(TaskId, DeliveryLocation, Items).
=======
+!solveJob(Job) : .my_name(Me) <- 
	getJob(Job, DeliveryLocation, Items);
	
	.print(Me, " doing ", Job, " to ", DeliveryLocation, " with: ", Items);
>>>>>>> 94a2a30c35dc77c2fc555aca20c2997ab1ba34ea
	
+!solvePartialJob(Job, DeliveryLocation, Item) : .my_name(Me) <- 
	.print(Me, " doing ", Job, " to ", DeliveryLocation, " with: ", Item);
	
	getBaseItems([Item], BaseItems);
	.print("Base items needed: ", BaseItems);
	
	!retrieveItems(BaseItems);

	!assembleItems([Item]);

	!delieverItems(Job, DeliveryLocation);
	.print("Job done!").
	
	
+!delieverItems(Job, Facility) <- 
	!getToFacility(Facility);
 	!doAction(deliver_job(Job)).
 	
+!assembleItems([]).
+!assembleItems([map(Item, Amount) | Items]) : inWorkshop 
	<- !assembleItem(Item); 
	if (Amount > 1) { !assembleItems([map(Item, Amount - 1) | Items]); }
	else 			{ !assembleItems(Items); }.
+!assembleItems(Items) <- 
	getClosestFacility("workshop", Workshop); 
	
	!getToFacility(Workshop);
	!assembleItems(Items).
	
+!assembleItem(Item) : inWorkshop & .my_name(Agent) 
	<- .print("Assembling item: ", Item);
	getRequiredItems(Item, ReqItems);
	getAgentInventory(Agent, Inv);
	?contains(ReqItems, Inv, Missing);
	if (Missing = []) { !doAction(assemble(Item)); }
	else {
		!assembleItems(Missing);
		!assembleItem(Item);	
	}.
-!assembleItem(Item) <- .print("Could not assemble item").

+?contains([], _, _).
+?contains([Item | Rest], Inventory, Missing) : contains(Item, Inventory)
	<- ?contains(Rest, Inventory, Missing). 
+?contains([Item | Rest], Inventory, [Item | Missing]) 	
 	<- ?contains(Rest, Inventory, Missing).


+!retrieveItems([]).
+!retrieveItems([map(Item, Amount) | Items]) <- 
	getShopSelling(Item, Amount, Shop, AmountAvailable);
	.print("Retriving ", Amount, " of ", Item, " in ", Shop);
	
	!getToFacility(Shop);
  	!buyItem(Item, AmountAvailable);
  	
  	AmountRemaining = Amount - AmountAvailable;
  	
	if (AmountRemaining > 0) { 
		.concat(Items, [map(Item, AmountRemaining)], NewItems);
		!retrieveItems(NewItems);
	}
	else { !retrieveItems(Items); }.
	
+!retrieveTools([]).
+!retrieveTools([Tool | Tools]) : have(Tool) 	<- !retrieveTools(Tools).
+!retrieveTools([Tool | Tools]) 				<- !retrieveTool(Tool);	!retrieveTools(Tools).
+!retrieveTool(Tool) <-
	.my_name(Me); canUseTool(Tool, Me, CanUse);
	if (CanUse)
	{
		getShopSelling(Tool, Shop);
		.print(Tool, " can be bougth in ", Shop);
		
		!getToFacility(Shop);
		!buyItem(Tool, 1);
	}
	else { .print("Can't use the tool"); } // Need help from someone that can use this tool
	.
	
<<<<<<< HEAD
+!buyItem(Item, Amount) : inShop <- !doAction(buy(Item, Amount)).
-!buyItem(Item, Amount) <- .print("Not in a shop while buying ", Item).
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) : not enoughCharge & not isChargingStation(F) 	<- .print("Need to charge"); !charge; !getToFacility(F).
+!getToFacility(F) 													<- !doAction(goto(F)); !getToFacility(F).
=======
+!buyItem(Item, Amount) : inShop & newStep 	<- -newStep; action(buy(Item, Amount)). // Should maybe check if the shop sells the item
+!buyItem(Item, Amount) : inShop 			<- !buyItem(Item, Amount).
-!buyItem(Item, Amount) : newStep			<- .print("Not in a shop while buying ", Item).

	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) : newStep & enoughCharge		<- -newStep; action(goto(F)); !getToFacility(F).
+!getToFacility(F) : not enoughCharge 			<- .print("Need to charge"); !charge; !getToFacility(F).
+!getToFacility(F) 								<- !getToFacility(F).
>>>>>>> 94a2a30c35dc77c2fc555aca20c2997ab1ba34ea

+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation <- !doAction(charge); !charge.
+!charge <- 
	getClosestFacility("chargingStation", ChargingStation);
	!getToFacility(ChargingStation);
	!charge.
	 
+!doAction(Action) : step(X) <-
//	.wait(step(X));
	.print(X);	
	action(Action).
