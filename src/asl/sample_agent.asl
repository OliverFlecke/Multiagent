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
//	getPercepts(C);
	-+updatePercepts.
	
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
	
+shop(ShopId, Lat, Lng, Items) <- .print("Shop", ShopId).

