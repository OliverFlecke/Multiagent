{ include("connections.asl") }
// Initial beliefs
free.

// Rules
getBaseItems(Items, BaseItems) :- BaseItems = [].
inWorkshop 	:- inFacility(F) & .substring("workshop", F).
inStorage 	:- inFacility(F) & .substring("storage",  F).
inShop	    :- inFacility(F) & .substring("shop",     F).
inShop(X)	:- inFacility(F) & .substring("shop",     F) & .substring(X, F).

// Initial goals
!register.
!focusArtifacts.

// Plans 
+!register : connection(C) <- register(C).
-!register <- .wait(100); !register.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("EIArtifact").	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.

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

	!assembleItems(Items);

	!delieverItems(Job, DeliveryLocation);
	.print("Job done!").
	
+!delieverItems(Job, Facility) <- 
	!getToFacility(Facility);
 	action(deliver_job(Job)).
 	
+!assembleItems([]).
+!assembleItems([Item | Items]) : inWorkshop <- 
	!assembleItem(Item); 
	!assembleItems(Items).
+!assembleItems(Items) <- 
	!focusArtifact("FacilityArtifact");
	getClosestFacility("workshop", Workshop); 
	
	!getToFacility(Workshop);
	!assembleItems(Items).
	
+!assembleItem(Item) : inWorkshop 
	<- action(assemble(Item)).

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
	else 					 { !retrieveItems(Items); }
	.
	
+!retrieveTools(Tools).
	
+!buyItem(Item, Amount) : inShop <- 
	.print("Buying ", Amount, " of ", Item); 
	action(buy(Item, Amount)).
-!buyItem(Item, Amount) <- .print("Not in a shop while buying ", Item).
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) <- 
	!focusArtifact("AgentArtifact"); 
	updateFacility; // Update inFacility belief
	if (not inFacility(F)) 	{ action(goto(F)); !getToFacility(F); }.	

// Power related plans
+charge(X) : X < 200 <- !goCharge.
