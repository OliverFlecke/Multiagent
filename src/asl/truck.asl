{ include("agent.asl") }

+free <-
	getClosestFacility("shop", S);
	!getToFacility(S).