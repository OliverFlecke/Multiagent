
{ include("../connections.asl") }
{ include("../stdlib.asl") }
{ include("../rules.asl") }

!focusArtifacts.

+task(TaskId, Type) : shops([], _) <-
	.print("New task: ", TaskId);
	-+tasks(NumberOfTasks+1);
	!announce(TaskId, Type).

+task(TaskId, Type) : not shops([], _) <-
	.wait(shops([], _));
	!announce(TaskId, Type).
	
+!announce(TaskId, Type) <-
	getJob(TaskId, DeliveryLocation, Items);
	!announceShoppingList(Items);
	!announceAssemble(Items, TaskId, DeliveryLocation).

+!announceShoppingList(Items) <- 
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!requestBuyItems(ShoppingList);
	!announceRetrieve(ShoppingList).
	
+!announceRetrieve([map(Shop,[])|Rest]) <- !announceRetrieve(Rest).
+!announceRetrieve(ShoppingList) : workshopTruck(WorkshopTruck, Workshop) <-
	announceRetrieve(ShoppingList, Workshop).
	
+!announceAssemble([], _, _).
+!announceAssemble([Item|Items], TaskId, DeliveryLocation) 
	: workshopTruck(WorkshopTruck, Workshop) 
	& hasBaseItems(WorkshopTruck, [Item]) <-
	announceAssemble([Item], Workshop, TaskId, DeliveryLocation);
	!announceAssemble(Items, TaskId, DeliveryLocation).
	
-!announceAssemble([Item|Items], TaskId, DeliveryLocation) : step(X) <-
	!announceAssemble(Items, TaskId, DeliveryLocation);
	.wait(step(X+5));
	!announceAssemble([Item], TaskId, DeliveryLocation).

+!announceDeliver(Items, TaskId, DeliveryLocation) <-
	announce("deliveryRequest", Items, Workshop, TaskId, DeliveryLocation).
	
+!requestBuyItems([]).
+!requestBuyItems([map(Shop,Items)|Shops]) : truckFacility(Agent, Shop) <-
	.print("Send buyItem to ", Agent, " ", Items);
	.send(Agent, tell, buyItems(Items));
	!requestBuyItems(Shops).
+!requestBuyItems([map(Shop,Items)|Shops]) <-
	announce("buyRequest", Shop, Items).