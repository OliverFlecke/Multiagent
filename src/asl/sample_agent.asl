!register.

+!register : true <-
	makeArtifact("a", "env.EIArtifact", [], Id);
	register("connectionA1");
	.wait(1000);
	!start.

+!start : true <- 
	action(goto(shop0));
	-+start.
	
+shop(ShopId, Lat, Lng, Items) <- .print("Shop", ShopId).
