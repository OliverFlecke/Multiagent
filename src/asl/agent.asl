{ include("connections.asl") }
// Initial beliefs
free.

// Rules
getBaseItems(Items, BaseItems) :- BaseItems = [].

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
	!solveJob(Storage, Items);
	+free.
	
+!solveJob(Storage, Items) <- 
	.print(Storage); .print(Items);
	// Find which items needed
	getBaseItems(Items, BaseItems);
	.print("Base items needed: ", BaseItems);
	// Find with tools needed
	// Collect items (and tools if needed)
	!retriveItems(BaseItems);
	// Assemble items 

	// Deliver items 
//	!getToFacility(Storage);
	.

+!retriveItems([]). 
+!retriveItems([Item | Items]) : true <- !retriveItem(Item); !retriveItems(Items).

+!retriveItem(map(Item, Amount)) <- .print("Retriving ", Item);
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

