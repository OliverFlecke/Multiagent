{ include("../agent.asl") }

+shops([], CNPName) : not assigned <-
	getClosestFacility("workshop", Workshop);
	distanceToFacility(Workshop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	bid(Distance)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		!!assignFacility(Workshop);
//		clearShops(CNPName);
	}.
	
+shops(Shops, CNPName) : not assigned <-	
	getClosestShop(Shops, Shop);
	distanceToFacility(Shop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	bid(Distance)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		!!assignFacility(Shop);
		clearShops(CNPName);
		
		?delete(Shop, Shops, NewShops);
		announceShops(NewShops);
	}.
	

+!assignFacility(F) : .my_name(Me) <- 
	+assigned;
	.broadcast(tell, truckFacility(Me, F));
	-free;
	!getToFacility(F); 
	+free.
	
@buy1[atomic]
+buyItems(Items) : free <-
	-free;
	!buyItems(Items);
	+free.
@buy2[atomic]
+buyItems(Items) : intentions(List) <-
	.concat(List, [buyItems(Items)], NewList);
	-+intentions(NewList).
	