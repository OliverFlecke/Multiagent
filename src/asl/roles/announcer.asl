
{ include("../connections.asl") }
{ include("../stdlib.asl") }
{ include("../rules.asl") }

tasks(0).

!focusArtifacts.

+task(TaskId, Type) : Type \== "auction" & tasks(N) & N < 5 <-
	.print("New task: ", TaskId); -+tasks(N+1);
	getJob(TaskId, Storage, Items);
	getClosestWorkshopToStorage(Storage, Workshop);
	!announceAssemble(Items, Workshop, TaskId, Storage).
	
+!announceAssemble([], _, _, _).
+!announceAssemble(Items, Workshop, TaskId, Storage) 	 <- announceAssemble(Items, Workshop, TaskId, Storage).
	
+!announceRetrieve(Agent, [map(Shop,[])|Rest], Workshop) <- !announceRetrieve(Agent, Rest, Workshop).
+!announceRetrieve(Agent, ShoppingList		 , Workshop) <- announceRetrieve(Agent, ShoppingList, Workshop).
	
