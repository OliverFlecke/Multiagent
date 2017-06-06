
+task(TaskId, DeliveryLocation, Items, Type, CNPName) <-
	!announceShoppingList(Items);
	.
	
//+retriever(Agent, [map(Shop,[])|ShoppingList]) 	<- !announceRetrieve(ShoppingList).
//+retriever(Agent, ShoppingList)					<- !announceRetrieve(ShoppingList).

+!announceShoppingList(Items) <- 
	getBaseItems(Items, BaseItems);
	getShoppingList(BaseItems, ShoppingList);
	!requestBuyItems(ShoppingList);
	!announceRetrieve(ShoppingList).
	
+!announceRetrieve([map(Shop,[])|Rest]) <- !announceRetrieve(Rest).
+!announceRetrieve(ShoppingList) : workshopTruck(WorkshopTruck, Workshop) <-
	announceRetrieval(ShoppingList, Workshop).
	
+!announceAssemble(Items) : workshopTruck(WorkshopTruck, Workshop) <-
	announceAssembly(Items, Workshop).

+!announceDeliver(Items, TaskId, DeliveryLocation) <-
	announceDelivery(Items, Workshop, TaskId, DeliveryLocation).
	
+!requestBuyItems([]).
+!requestBuyItems([map(Shop,Items)|Shops]) : truckFacility(Agent, Shop) <-
	.send(Agent, achieve, buyItems(Items));
	!requestBuyItems(Shops).
+!requestBuyItems([map(Shop,Items)|Shops]) <-
	announceBuy(Shop, Items).