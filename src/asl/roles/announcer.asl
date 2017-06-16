
{ include("../connections.asl") }
{ include("../stdlib.asl") }
{ include("../rules.asl") }

!focusArtifacts.

+task(TaskId, Type) : Type \== "auction" <-
	getJob(TaskId, Storage, Items);
	.print("New task: ", TaskId, " - ", Items);
	getClosestWorkshopToStorage(Storage, Workshop);
	!announceAssemble(Items, Workshop, TaskId, Storage).
	
+!announceAssemble([], _, _, _).
+!announceAssemble(Items, Workshop, TaskId, Storage) 	 <- announceAssemble(Items, Workshop, TaskId, Storage).
	
+!announceRetrieve(Agent, [map(Shop,[])|Rest], Workshop) <- !announceRetrieve(Agent, Rest, Workshop).
+!announceRetrieve(Agent, ShoppingList		 , Workshop) <- announceRetrieve(Agent, ShoppingList, Workshop).
	
