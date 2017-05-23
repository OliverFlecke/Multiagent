{ include("connections.asl") }
// Initial beliefs
free.

// Rules
isMyName(Name) :- .my_name(Me) & .term2string(Me, String) & String == Name.

//inFacility(F) :- .my_name(Me) & .term2string(Me, Name) & inFacility(Name, F).

getBaseItems(Items, BaseItems) :- BaseItems = [].
inWorkshop 	:- inFacility(F) & .substring("workshop", F).
inStorage 	:- inFacility(F) & .substring("storage",  F).
inShop	    :- inFacility(F) & .substring("shop",     F).
inShop(X)	:- inFacility(F) & .substring("shop",     F) & .substring(X, F).

contains(map(Item, X), [map(Item, Y) | _]) 	:- X <= Y. 		// There is a .member function, but we need to unwrap the objects
contains(Item, [_ | Inventory]) 			:- contains(Item, Inventory). 

// Initial goals
!register.
!focusArtifacts.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("AgentArtifact");
	!focusArtifact("EIArtifact").	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.
// Plans 
+inFacility(Name, X) : isMyName(Name) <- +inFacility(X).

+task(Id) : .my_name(agentA1) & free <- 
	-free;
	getJob(Id, Storage, Items);
	!solveJob(Id, Storage, Items);
	+free.
	
+!solveJob(Job, DeliveryLocation, Items) <- 
	.print("Doing ", Job, " to ", DeliveryLocation, " with: ", Items);
	
	getBaseItems(Items, BaseItems);
	.print("Base items needed: ", BaseItems);
	
	!retrieveItems(BaseItems);
	!retrieveTools(Tools);

	!focusArtifact("ItemArtifact");
	!assembleItems(Items);

	!delieverItems(Job, DeliveryLocation);
	.print("Job done!").
	
+!delieverItems(Job, Facility) <- 
	!getToFacility(Facility);
 	action(deliver_job(Job)).
 	
+!assembleItems([]).
+!assembleItems([map(Item, Amount) | Items]) : inWorkshop 
	<- 
	!assembleItem(Item); 
	if (Amount > 1) { !assembleItems([map(Item, Amount - 1) | Items]); }
	else 			{ !assembleItems(Items); }.
+!assembleItems(Items) <- 
	!focusArtifact("FacilityArtifact");
	getClosestFacility("workshop", Workshop); 
	
	!getToFacility(Workshop);
	!assembleItems(Items).
	
+!assembleItem(Item) : inWorkshop & .my_name(Agent) 
	<- 
	.print("Assembling item: ", Item);
	getRequiredItems(Item, ReqItems);
	getAgentInventory(Agent, Inv);
	?contains(ReqItems, Inv, Missing);
	if (Missing = []) { action(assemble(Item)); }
	else {
		!assembleItems(Missing);
		!assembleItem(Item);	
	}.
+!assembleItem(Item) <- .print("Could not assemble item").

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
	
+!retrieveTools(Tools).
	
+!buyItem(Item, Amount) : inShop <- action(buy(Item, Amount)).
-!buyItem(Item, Amount) <- .print("Not in a shop while buying ", Item).
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) <- action(goto(F)); !getToFacility(F).	

// Power related plans
+charge(X) : X < 200 <- !goCharge.

