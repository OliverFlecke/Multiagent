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

+task(JobId, Items, Storage, Type, CNPId) : 
	free & canSolve(Items) & Type \== "auction" <-
	!doIntention(newTask(JobId, Items, Storage, Type, CNPId)).
	
+!newTask(JobId, Items, Storage, Type, CNPId) : getBid(Items, Bid) <-
	bid(Bid)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];	
	if (Won) {
		clear("task", 5, CNPId);
		!deliverJob(JobId, Items, Storage);
	}.
	
+!doIntention(_) : not free <- .print("Illegal execution").
+!doIntention(Intention) 	<- -free; !Intention; +free.
	
+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait({+step(_)}).

+step(X) : lastActionResult(R) & lastAction(A) & lastActionParam(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" 
		 & not A = "assist_assemble" <- .print(R, " ", A, " ", P).
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P).
	
// Percepts	
+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait({+step(_)}).
		 
+reset <- .print("resetting"); .drop_all_desires; .drop_all_events; .drop_all_intentions; -reset.
