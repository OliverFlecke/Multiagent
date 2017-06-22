{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }

!focusArtifacts.

+task(TaskId, Type) : Type \== "auction" <-
	getJob(TaskId, Storage, Items);
	.print("New task: ", TaskId, " - ", Items);
	getClosestWorkshopToStorage(Storage, Workshop);
	!announceAssemble(Items, Workshop, TaskId, Storage, "new").
	
+!announceAssemble([], _, _, _, _).
+!announceAssemble(Items, Workshop, TaskId, Storage, Type) 	 <- announceAssemble(Items, Workshop, TaskId, Storage, Type).
	
+!announceRetrieve(Agent, [map(Shop,[])|Rest], Workshop) <- !announceRetrieve(Agent, Rest, Workshop).
+!announceRetrieve(Agent, ShoppingList		 , Workshop) <- announceRetrieve(Agent, ShoppingList, Workshop).
	
+assembleRequest(_, _, _, _, "new", CNPId) <-
	takeTask(CanTake)[artifact_id(CNPId)];
	if (CanTake)
	{
		clearAssemble(CNPId);
	}.