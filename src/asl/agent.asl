// Includes
{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }
{ include("requests.asl") }

// Initial beliefs
free.

// Initial goals
!register.
!focusArtifacts.
	
// Percepts
	
+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait({+step(_)}).
//+!doAction(Action) : .my_name(Me) & step(X) <- jia.action(Me, Action); .wait(step(Y) & X < Y).

+step(999) : .my_name(car1) <- getJobCompletedCount(X); .print("Jobs completed: ", X).
+step(X) : lastAction("assist_assemble") & lastActionResult("failed_counterpart").
+step(X) : lastAction("give") 		 & lastActionResult("successful") <- .print("Give successful!").
+step(X) : lastAction("receive") 	 & lastActionResult("successful") <- .print("Receive successful!").
+step(X) : lastAction("deliver_job") & lastActionResult("successful") <- .print("Job successful!"); incJobCompletedCount.
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P).
