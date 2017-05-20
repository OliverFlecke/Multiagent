{ include("connections.asl") }
// Initial beliefs
free.

// Rules
getBaseItems(Items, BaseItems) :- BaseItems = [].
//inWorkshop :- inFacility(F).

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
	
+!solveJob(Job, Storage, Items) <- 
	.print(Job, " to ", Storage, " with: ", Items);
	// Find which items needed
	getBaseItems(Items, BaseItems);
	.print("Base items needed: ", BaseItems);
	// Find with tools needed
	// Collect items (and tools if needed)
	!retriveItems(BaseItems);

	// Assemble items 
	lookupArtifact("FacilityArtifact", FAID); focus(FAID);
	getClosestFacility("workshop", Workshop); 
	!getToFacility(Workshop);
	!assembleItems(Items);

	// Deliver items 
	!getToFacility(Storage);
	action(deliver_job(Job));
	.print("Job done!").

+!assembleItems([]).
+!assembleItems([Item | Items]) <- 
	!assembleItem(Item); 
	!assembleItems(Items).
+!assembleItem(Item) : true // Should check that agent is in a workshop
	<- action(assemble(Item)).

+!retriveItems([]). 
+!retriveItems([Item | Items]) : true <- !retriveItem(Item); !retriveItems(Items).

+!retriveItem(map(Item, Amount)) <- 
	.print("Retriving ", Item);
	getShopSelling(Item, Amount, Shop, AmountAvailable);
	.print("Closet shop is ", Shop);
	!getToFacility(Shop);
  	!buyItem(Item, AmountAvailable);
	.
+!buyItem(Item, Amount) <- 
	.print("Bying ", Amount, " of ", Item); 
	action(buy(Item, Amount)).
	
+!getToFacility(F) : inFacility(F). 
+!getToFacility(F) <- 
	action(goto(F)); 
	lookupArtifact("AgentArtifact", AAID); focus(AAID);
	inFacility(_); // Update inFacility belief
	!getToFacility(F).

+step(X) <- -step(X).
+inFacility(X) <- +inFacility(X). // Needed to add the belief 



// Power related plans
+charge(X) : X < 200 <- !goCharge.

// Test plans

