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

+task(Id, ArtifactName) : .my_name(agentA1) & free <- 
	-free;
	lookupArtifact("JobArtifact", JobArtifactId);
	getJob(Id, Storage, Items)[artifact_id(JobArtifactId)];
	!solveJob(Storage, Items);
	+free.
	
+!solveJob(Storage, Items) <- 
	.print(Storage);
	.print(Items);
	lookupArtifact("ItemArtifact", ItemArtifactId);
	// Find which items needed
	getBaseItems(Items, BaseItems);
	.print(BaseItems);
	// Find with tools needed
	// Collect items (and tools if needed)
	!retriveItems(BaseItems);
	// Assemble items 
	
	// Deliver items 
	!getToFacility(Storage);
	.

+!retriveItems([]). 
+!retriveItems([Item | Items]) : true <- !retriveItem(Item); !retriveItems(Items).

+!retriveItem(map(Item, Amount)) <- .print("Retriving ", Item);
	getClosestFacilitySelling(Item, Shop);
	.print("Closet shop is ", Shop);
	!getToFacility(Shop);
//	waitUntilInFacility(Shop);
  	!buyItem(Item, Amount);
	.
+!buyItem(Item, Amount) <- .print("Bying ", Amount, " of ", Item); action(buy(Item, Amount)).
	
+!getToFacility(Facility) <- action(goto(Facility)); await(inFacility(Facility)).

+step(X) <- -step(X).



// Power related plans
+charge(X) : X < 200 <- !goCharge.

// Test plans
+inFacility(X) <- .print("I am at: ", X).
+inFacility : true <- .print("Hello world").

