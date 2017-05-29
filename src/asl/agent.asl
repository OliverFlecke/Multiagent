{ include("connections.asl") }
// Initial beliefs
free.

// Rules
myName(Name)	:- .my_name(Me) & .term2string(Me, Name).
myRole(Role) 	:- myName(Name) & myRole(Name, Role).

// Personal percepts
inFacility(F) 	:- myName(Name) & inFacility(Name, F).
charge(C) 		:- myName(Name) & charge(Name, C).
load(L)			:- myName(Name) & load(Name, L).
routeLength(L)	:- myName(Name) & routeLength(Name, L).

speed(S)		:- myRole(Role) & role(Role, S, _, _, _).
maxLoad(L)		:- myRole(Role) & role(Role, _, L, _, _).
maxCharge(C)	:- myRole(Role) & role(Role, _, _, C, _).
chargeThreshold(100). // Should the threshold be dependent on the type of vehicle (properly) 
// Don't know if we will need to know if they travel by road or air

// Check if agent is in this type of facility
inChargingStation 	:- inFacility(F) & .substring("chargingStation", F).
inWorkshop 			:- inFacility(F) & .substring("workshop", F).
inStorage 			:- inFacility(F) & .substring("storage",  F).
inShop	    		:- inFacility(F) & .substring("shop",     F).
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
+step(X) <- +newStep.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("AgentArtifact");
	!focusArtifact("FacilityArtifact");
	!focusArtifact("EIArtifact").	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.

+task(TaskId, CNPName) : free & .my_name(agentA1) <- 
	lookupArtifact(CNPName, CNPId);
	bid(20)[artifact_id(CNPId)];
	winner(Name)[artifact_id(CNPId)];
	+winner(Name, TaskId).
	
+task(Items, DeliveryLocation, CNPName) : free <- 
	lookupArtifact(CNPName, CNPId);
	bid(20)[artifact_id(CNPId)];
	winner(Name)[artifact_id(CNPId)];
	+winner(Name, TaskId).
	
+winner(Name, TaskId) : myName(Name) <-
	-free;
	!solveJob(TaskId);
	+free.
	
+!solveJob(Job) : .my_name(Me) <- 
	getJob(Job, DeliveryLocation, Items);
	.print(Me, " doing ", Job, " to ", DeliveryLocation, " with: ", Items);
	
	getBaseItems(Items, BaseItems);
	.print("Base items needed: ", BaseItems);
	
	!retrieveTools(Tools);
	!retrieveItems(BaseItems);

	!focusArtifact("ItemArtifact");
	!assembleItems(Items);

	!delieverItems(Job, DeliveryLocation);
	.print("Job done!").
	
+!delieverItems(Job, Facility) <- 
	!getToFacility(Facility);
 	action(deliver_job(Job)).
 	
+!assembleItems([]).
+!assembleItems([map(Item, Amount) | Items]) : inWorkshop 
	<- !assembleItem(Item); 
	if (Amount > 1) { !assembleItems([map(Item, Amount - 1) | Items]); }
	else 			{ !assembleItems(Items); }.
+!assembleItems(Items) <- 
	!focusArtifact("FacilityArtifact");
	getClosestFacility("workshop", Workshop); 
	
	!getToFacility(Workshop);
	!assembleItems(Items).
	
+!assembleItem(Item) : inWorkshop & .my_name(Agent) 
	<- .print("Assembling item: ", Item);
	getRequiredItems(Item, ReqItems);
	getAgentInventory(Agent, Inv);
	?contains(ReqItems, Inv, Missing);
	if (Missing = []) { action(assemble(Item)); }
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
	
+!buyItem(Item, Amount) : inShop 	<- action(buy(Item, Amount)). // Should maybe check if the shop sells the item
-!buyItem(Item, Amount) 			<- .print("Not in a shop while buying ", Item).
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) : newStep & enoughCharge		<- -newStep; action(goto(F)); !getToFacility(F).
+!getToFacility(F) : not enoughCharge 			<- .print("Need to charge"); !charge; !getToFacility(F).
+!getToFacility(F) 								<- !getToFacility(F).

+!charge : charge(X) & maxCharge(X).
+!charge : inChargingStation <- action(charge); !charge.
+!charge <- 
	getClosestFacility("chargingStation", ChargingStation);
	+enoughCharge;
	!getToFacility(ChargingStation);
	-enoughCharge;
	!charge.
	 

