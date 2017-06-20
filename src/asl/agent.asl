// Includes
{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }

// Initial beliefs
intentions([]).
free.

// Initial goals
!register.
!focusArtifacts.
	
// Percepts
@plan1[atomic]
+free : intentions([First|Rest]) <-	-+intentions(Rest); -free; !First; +free.

@plan2[atomic]
+!addIntentionFirst(Intention) : intentions(List) <- -+intentions([Intention|List]).
+!addIntentionLast (Intention) : intentions([]) & Intention = acceptReceiveProtocol(Agent, Items, InitStep) <-
	.wait(intentions(L) & .length(L, N) & N > 0);
	!addIntentionLast(Intention).
@plan3[atomic]
+!addIntentionLast (Intention) : intentions(List) <- .concat(List, [Intention], NewList); -+intentions(NewList).
	
+!doAction(Action) : .my_name(Me) <- jia.action(Me, Action); .wait(step(_)).

//+step(0) <- +free.
+step(X) : lastAction("give") 		 & lastActionResult("successful") <- .print("Give successful!").
+step(X) : lastAction("receive") 	 & lastActionResult("successful") <- .print("Receive successful!").
+step(X) : lastAction("deliver_job") & lastActionResult("successful") <- .print("Job successful!").
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParam(P) <- .print(R, " ", A, " ", P);
	if (A = "buy")
	{
		P = [Item, Amount];
	}
	if (A = "deliver_job")
	{
		P = [TaskId];
		
	}
	if (A = "assemble")
	{
		P = [Item];	
	}.
	
