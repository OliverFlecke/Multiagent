{ include("agent.asl") }

free.

+shops([], CNPName) : free <-
	getClosestFacility("workshop", Workshop);
	distanceToFacility(Shop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	bid(Distance)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		!!assignFacility(Workshop);
		clearShops(CNPName);
	}.
	
+shops(Shops, CNPName) : free <-
	
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
	!!getToFacility(F); 
	.broadcast(tell, truckFacility(Me, F));
	-free.
	
