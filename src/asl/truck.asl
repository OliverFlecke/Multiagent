{ include("agent.asl") }

+shops(Shops, CNPName) : not shop(_) <-
	
	getClosestShop(Shops, Shop);
	distanceToFacility(Shop, Distance);
	
	lookupArtifact(CNPName, CNPId);
	bid(Distance)[artifact_id(CNPId)];
	winner(Won)[artifact_id(CNPId)];
	
	if (Won)
	{
		clearShops(CNPName);
		
		+shop(Shop);
		?delete(Shop, Shops, NewShops);
		
		if (not NewShops = []) 
		{
			announceShops(NewShops);
		}
	}.
	
+shop(Shop) : .my_name(Me) <- !!getToFacility(Shop); .broadcast(tell, shop(Shop, Me)).

+!buy(Item, Amount) : shop(Shop) & inFacility(Shop) <- !doAction(buy(Item, Amount)).
+!buy(Item, Amount) : shop(Shop) <- !getToFacility(Shop); !buy(Item, Amount).

+!give(Item, Amount) <- !doAction(give(Item, Amount)).
