!init.
	
+!init : .my_name(Me) <-
	makeArtifact("EISHandler", "mapc2017.env.EISHandler", [], _);
	.
	
+reset <- 
	for (assembleRequest(_, _, _, _, _, CnpId)) { clear("assembleRequest", 6, CnpId); };
	for (retrieveRequest(_, _, _, CnpId)) 		{ clear("retrieveRequest", 4, CnpId); };
	-reset.