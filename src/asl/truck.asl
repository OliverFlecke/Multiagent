{ include("agent.asl") }

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
	
+buyItems(Items) : free <-
	-free;
	!buyItems(Items);
	+free.
+buyItems(Items) <-
	.wait({+free});
	!buyItems(Items).
	
//	.suspend(buyItems(Items)).
	
//+free <-
//	.print("Resuming intentions");
//	for ( .suspended(G, _) )
//	{
//		.resume(G);
//	}.

	
//-!buyItems(Items) <- 
//	.print("Suspending ", Items);
//	.suspend(buyItems(Items)).

//+inFacility(Me, Shop) : .my_name(Me) & truckFacility(Me, Shop) <-
//	.print(Me, " in ", Shop);
//	for ( .suspended(G) )
//	{
//		.resume(G);
//	}.
