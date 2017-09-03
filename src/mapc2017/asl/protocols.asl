	
+!initiateAssembleProtocol(Items) <-
	!wait(.count(assistant	   [source(_)], N) 
		& .count(assistantReady[source(_)], N));
	for (assistant[source(A)]) { .send(A,   tell, assemble); }
	!assembleItems(Items);	
	for (assistant[source(A)]) { .send(A, untell, assemble); }.
	
+!acceptAssembleProtocol(Agent) <-
	.send(Agent, tell, assistantReady);	
	!wait(assemble[source(Agent)], 1000);
	!assistAssemble(Agent);	
	.send(Agent, untell, assistantReady).
	
+!wait(Literal) 		<- while (not Literal) { !doAction(skip) }.
+!wait(Literal, Time)  	<- while (not Literal) { !doAction(skip); .wait(Time) }.
