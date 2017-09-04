{ include("rules.asl") }
{ include("plans.asl") }
{ include("protocols.asl") }

!init.

+!init : .my_name(Me) & .term2string(Me, AgentPerceiver) <-
	!focusArtifact("SimStartPerceiver");
	!focusArtifact("ReqActionPerceiver");
	!focusArtifact(AgentPerceiver);
	!focusArtifact("JobDelegator").
-!init <- .wait(500); !init.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).

+!doAction(Action) <- performAction(Action); .wait({+step(_)}).

+task(Id, Items, Storage, ShoppingList, Workshop) <- 
	!task(doTask(Id, Items, Storage, ShoppingList, Workshop)).
+task(AgentStr, ShoppingList, Workshop) : .term2string(Agent, AgentStr) <-
	!task(doTask(Agent, ShoppingList, Workshop)).
+task(Id, Bid) <-
	!task(doTask(Id, Bid)).
	
+!task(Task) : .print(Task) & false.
+!task(Task) <-	!stop; !Task; !free.

+!stop  <- .drop_all_desires; .drop_all_intentions.
+!free  <- free; !charge; !gather; !random; !skip.
+!reset <- !stop; .drop_all_events;
	for (assemble	   [source(A)]) { -assemble      [source(A)] };
	for (assistant     [source(A)]) { -assistant     [source(A)] };
	for (assistantReady[source(A)]) { -assistantReady[source(A)] };.

//+step(0) : not .my_name(agent1) <- .send(agent1, tell, hello).
//+step(1) : .my_name(agent1) <- .count(hello[source(_)], N); .print(N).

+step(X) : .my_name(agent1) & .print("Step: ", X) & false.
+step(0) <- !reset; !free.
+step(X) : lastAction("gather").// & lastActionResult("successful_partial").
+step(X) : lastActionResult(R) & lastAction(A) & lastActionParams(P)
		 & not A = "goto" & not A = "noAction" & not A = "charge" & not A = "skip"
		 & not A = "assist_assemble" <- .print(R, " ", A, " ", P).
+step(X) : lastActionResult(R) &   not lastActionResult("successful") 
		 & lastAction(A) & lastActionParams(P) <- .print(R, " ", A, " ", P).
		 
