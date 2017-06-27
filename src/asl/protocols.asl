+give(Items, InitStep)[source(Agent)] : not free <-
	!addIntentionFirst(acceptReceiveProtocol(Agent, Items, InitStep)).
	
+give(Items, InitStep)[source(Agent)] <-
	-free; !acceptReceiveProtocol(Agent, Items, InitStep); +free.
	
+receive(Items, InitStep)[source(Agent)] : not free <-
	!addIntentionFirst(acceptGiveProtocol(Agent, Items, InitStep)).
	
+receive(Items, InitStep)[source(Agent)] <-
	-free; !acceptGiveProtocol(Agent, Items, InitStep); +free.

+!initiateReceiveProtocol(Agent, Items) : step(MyStep) <-
	.send(Agent, tell, give(Items, MyStep));
	.wait(readyToGive(ReadyStep));
	.print("Waiting for step ", ReadyStep, " to retrieve ", Items);
	.wait(step(ReadyStep));
	!receiveItems(Items).
	
+!acceptReceiveProtocol(Agent, Items, InitStep) : not hasItems(Items) <-
	!addIntentionLast(acceptReceiveProtocol(Agent, Items, InitStep)).
+!acceptReceiveProtocol(Agent, Items, InitStep) : step(MyStep) <-
	.max([InitStep, MyStep], MaxStep);
	ReadyStep = MaxStep + 1; 
	.send(Agent, tell, readyToGive(ReadyStep));
	.print("Waiting for step ", ReadyStep, " to give ", Items);
	.wait(step(ReadyStep));
	!giveItems(Agent, Items).
	
+!initiateGiveProtocol(Agent, Items) : step(MyStep) <-
	.send(Agent, tell, receive(Items, MyStep));
	.wait(readyToReceive(ReadyStep));
	.print("Waiting for step ", ReadyStep, " to give ", Items);
	.wait(step(ReadyStep));
	!giveItems(Agent, Items).
	
+!acceptGiveProtocol(Agent, Items, InitStep) : step(MyStep) <-
	.max([InitStep, MyStep], MaxStep);
	ReadyStep = MaxStep + 1; 
	.send(Agent, tell, readyToReceive(ReadyStep));
	.print("Waiting for step ", ReadyStep);
	.wait(step(ReadyStep));
	!receiveItems(Items).	
	
+!initiateAssembleProtocol(Items) <-
	.wait(.count(itemRetriever(_, _), N) & .count(retrieverReady(_), N));
	!beginAssembleProtocol;	
	!assembleItems(Items);	
	!!completeAssembleProtocol.
	
+!beginAssembleProtocol : step(X) & ReadyStep = X + 1 <-
	for (itemRetriever(A, _)) {
		.send(A, tell, assembleReady(ReadyStep));
	}
	.wait({+step(ReadyStep)}).
	
+!completeAssembleProtocol <-
	.findall(X, itemRetriever(X, _), L);	
	
	for (itemRetriever(A, I)) {
		-itemRetriever(A, I);
		.send(A, tell, assembleComplete);
	}	
	.wait({+step(_)});
	.wait({+step(_)});
	
	for (.member(A, L)) {
		.send(A, untell, assembleReady(_));
		.send(A, untell, assembleComplete);
	}.

	
+!acceptAssembleProtocol(Agent) : .my_name(Me) <-
	.send(Agent, tell, retrieverReady(Me));
	
	.wait({+assembleReady(ReadyStep)});
	.wait({+step(ReadyStep)});
	
	!assistAssemble(Agent); // Waits for assembleComplete
	
	.send(Agent, untell, retrieverReady(Me)).
