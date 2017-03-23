!register.

+!register : true <-
	makeArtifact("a", "env.EIArtifact", [], Id);
	register("connectionA1");
	.wait(1000);
	action(goto(shop1)).

//+start : true <- 
//	action(goto(shop1));
//	-+start.
