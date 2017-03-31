{ include("connections.asl") }
// Initial beliefs

// Rules


// Initial goals
!register.


// Plans 
+!register : connection(C) <-
	makeArtifact("a", "env.EIArtifact", [], Id);
	register(C);
	focus(Id);
	+updatePercepts.
	
+updatePercepts : connection(C) <- 
	getPercepts(C);
	.wait(1000);
	-+updatePercepts.
	
// Power related plans
+charge(X) : X < 200 <- !goCharge.



// Test plans
+inFacility(X) <- .print("I am at: ", X).
+inFacility : true <- .print("Hello world").
	