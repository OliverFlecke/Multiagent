// Includes
{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }
{ include("requests.asl") }

// Initial beliefs
free.

// Initial goals
!register.
!focusArtifacts.

+task(JobId, Type, CNPId) : free & Type \== "auction" <-
	!doIntention(newTask(JobId, Type, CNPId)).
	
+!newTask(JobId, Type, CNPId) : capacity(C) <-
	bid(-C)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];	
	if (Won) {		
		clear("task", 3, CNPId);
		getJob(JobId, Storage, Items);
		!deliverJob(JobId, Items, Storage);
	}.
	
+!doIntention(_) : not free <- .print("Illegal execution").
+!doIntention(Intention) 	<- -free; !Intention; +free.
	
+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait({+step(_)}).

+step(X) : lastActionResult(R) & lastAction(A) & lastActionParam(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" <- .print(R, " ", A, " ", P).

+step(X) : lastAction("assist_assemble") & lastActionResult("failed_counterpart").
+step(X) : lastAction("give") 		 & lastActionResult("successful") <- .print("Give successful!").
+step(X) : lastAction("receive") 	 & lastActionResult("successful") <- .print("Receive successful!").
+step(X) : lastAction("deliver_job") & lastActionResult("successful") <- .print("Job successful!").
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P).
