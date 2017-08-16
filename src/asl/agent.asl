{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }
{ include("requests.asl") }

// Initial beliefs
free.

// Initial goals
!init.
	
+!doIntention(_) : not free <- .print("Illegal execution").
+!doIntention(Intention) 	<- -free; !Intention; +free.

+!doAction(Action) <- performAction(Action); .wait({+step(_)}).

+job(Id, Items, Storage, ShoppingList, Workshop) <- 
	!solveJob(Id, Items, Storage, ShoppingList, Workshop).

+job(AgentStr, ShoppingList, Workshop) : .term2string(Agent, AgentStr) <-
	!helpJob(Agent, ShoppingList, Workshop).

//+step(X) : .my_name(agent1) & .print("Step: ", X) & false.
//+step(1) <- ?getClosestFacility("dump"		, A) !getToFacility(A);
//			?getClosestFacility("shop"		, B) !getToFacility(B);
//			?getClosestFacility("workshop"	, C) !getToFacility(C).

+step(X) : lastActionResult(R) & lastAction(A) & lastActionParams(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" 
		 & not A = "assist_assemble" <- .print(R, " ", A, " ", P).
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParams(P) <- .print(R, " ", A, " ", P).
		 
+reset <- .print("resetting"); .drop_all_desires; .drop_all_events; .drop_all_intentions; -reset.
