{ include("connections.asl") }
{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }
{ include("requests.asl") }

// Initial beliefs
free.

// Initial goals
!focusArtifacts.

+task(JobId, Items, Storage, Type, CNPId) : 
	free & canSolve(Items) & Type \== "auction" <-
	!doIntention(newTask(JobId, Items, Storage, Type, CNPId)).
	
+!newTask(JobId, Items, Storage, Type, CNPId) : getBid(Items, Bid) <-
	bid(Bid)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];	
	if (Won) {
		jia.getBaseVolume(Items, V);
		?capacity(C);
		.print(JobId, " ", V, " ", C);
		clear("task", 5, CNPId);
		!deliverJob(JobId, Items, Storage);
	}.
	
+!doIntention(_) : not free <- .print("Illegal execution").
+!doIntention(Intention) 	<- -free; !Intention; +free.

//+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait({+step(_)}).
+!doAction(Action) <- performAction(Action); .wait({+step(_)}).

+step(X) : .my_name(agent1) & .print("Step: ", X) & false.

//+step(0) <- !doIntention(acquireTools).
+step(X) : lastAction(A) & A = "deliver_job" & lastActionResult(R) & R = "successful"
		 & lastActionParams(P) <- .print(R, " ", A, " ", P); incJobCompletedCount.
+step(X) : lastActionResult(R) & lastAction(A) & lastActionParams(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" 
		 & not A = "assist_assemble" <- .print(R, " ", A, " ", P).
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParams(P) <- .print(R, " ", A, " ", P).
		 
+reset <- .print("resetting"); .drop_all_desires; .drop_all_events; .drop_all_intentions; -reset.
