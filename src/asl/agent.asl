{ include("connections.asl") }
// Initial beliefs

// Rules
getBaseItems(Items, BaseItems) :- BaseItems = [].


// Initial goals
!register.


// Plans 
+!register : connection(C) <- register(C);
	lookupArtifact("TaskArtifact", Id); focus(Id).
-!register <- .wait(100); !register.
	
+task(Id, ArtifactName) <- 
	lookupArtifact("JobArtifact", JobArtifactId);
	getJob(Id, Storage, Items)[artifact_id(JobArtifactId)];
	.print(Storage);
	.print(Items).

// This is called in every step. Should therefore always make sure an action is returned
+step(X) <-
	.print("This is step: ", X);
	action(goto(storage4));
	-step(X).
	

// Power related plans
+charge(X) : X < 200 <- !goCharge.



// Test plans
+inFacility(X) <- .print("I am at: ", X).
+inFacility : true <- .print("Hello world").

