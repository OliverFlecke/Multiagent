
{ include("connections.asl") }
{ include("stdlib.asl") }
{ include("rules.asl") }

!focusArtifacts.

+task(TaskId, _, Items, _, _) : shops([], _) <-
	.print("New task: ", TaskId);
	!announceShoppingList(Items);
	.

-task(_, _, Items, _, _) <-
	.wait(shops([], _));
	!announceShoppingList(Items).
	

+!announceShoppingList(Items) <- 
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!requestBuyItems(ShoppingList);
	!announceRetrieve(ShoppingList).
	
+!announceRetrieve([map(Shop,[])|Rest]) <- !announceRetrieve(Rest).
+!announceRetrieve(ShoppingList) : workshopTruck(WorkshopTruck, Workshop) <-
	announceRetrieve(ShoppingList, Workshop).
	
+!announceAssemble(Items) : workshopTruck(WorkshopTruck, Workshop) <-
	announce("assemblyRequest", Items, Workshop).

+!announceDeliver(Items, TaskId, DeliveryLocation) <-
	announce("deliveryRequest", Items, Workshop, TaskId, DeliveryLocation).
	
+!requestBuyItems([]).
+!requestBuyItems([map(Shop,Items)|Shops]) : truckFacility(Agent, Shop) <-
	.print("Send buyItem to ", Agent, " ", Items);
	.send(Agent, tell, buyItems(Items));
	!requestBuyItems(Shops).
+!requestBuyItems([map(Shop,Items)|Shops]) <-
	announce("buyRequest", Shop, Items).