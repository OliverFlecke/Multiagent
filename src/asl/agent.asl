{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }

!init.

+!init : .my_name(Me) & .term2string(Me, AgentPerceiver) <-
	!focusArtifact("SimStartPerceiver");
	!focusArtifact("ReqActionPerceiver");
	!focusArtifact(AgentPerceiver);
	!focusArtifact("JobDelegator");	free.
-!init <- .wait(500); !init.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).

+!doAction(Action) <- performAction(Action); .wait({+step(_)}).

+job(Id, Items, Storage, ShoppingList, Workshop) <- 
	.drop_all_desires; .drop_all_intentions;
	!solveJob(Id, Items, Storage, ShoppingList, Workshop); free; !charge.
+job(AgentStr, ShoppingList, Workshop) : .term2string(Agent, AgentStr) <-
	.drop_all_desires; .drop_all_intentions;
	!helpJob(Agent, ShoppingList, Workshop); free; !charge.
+job(Id, Bid) <-
	!bidJob(Id, Bid); free.

+step(X) : .my_name(agent1) & .print("Step: ", X) & false.
+step(0) <- .drop_all_desires; .drop_all_events; .drop_all_intentions.
+step(X) : lastActionResult(R) & lastAction(A) & lastActionParams(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" 
		 & not A = "assist_assemble" <- .print(R, " ", A, " ", P).
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParams(P) <- .print(R, " ", A, " ", P).
		 
