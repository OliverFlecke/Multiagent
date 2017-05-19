{ include("connections.asl") }
// Initial beliefs

// Rules

// Initial goals
!register.
!focusArtifacts.

// Plans 
+!register : connection(C) <- register(C).
	
-!register <- .wait(100); !register.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).

+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("EIArtifact");
	.print("Success").
	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.
	
+task(Id, ArtifactName) : .my_name(agentA1) <- .print("Got task: ", Id, " and artifact ", ArtifactName).

+step(X) <- -step(X).
	

// Power related plans
+charge(X) : X < 200 <- !goCharge.

// Test plans
+inFacility(X) <- .print("I am at: ", X).
+inFacility : true <- .print("Hello world").

