!init.

+!init1 : .my_name(Me) <-
	makeArtifact("EIArtifact", "env.EIArtifact", [], Id);
	makeArtifact("ItemArtifact", "info.ItemArtifact", [], _);
	makeArtifact("FacilityArtifact", "info.FacilityArtifact", [], _);
	makeArtifact("StaticInfoArtifact", "info.StaticInfoArtifact", [], _);
	makeArtifact("DynamicInfoArtifact", "info.DynamicInfoArtifact", [], _);
	makeArtifact("JobArtifact", "info.JobArtifact", [], _);
	makeArtifact("TaskArtifact", "cnp.TaskArtifact", [], TaskId);
	focus(Id);
	focus(TaskId).
	
+!init : .my_name(Me) <-
	makeArtifact("TaskArtifact", 		"cnp.TaskArtifact", [], _);
	makeArtifact("EISHandler", 			"mapc2017.env.EISHandler", [], _);
	makeArtifact("OpArtifact",			"mapc2017.env.OpArtifact", [], _);
	.
	
+reset <- 
	for (assembleRequest(_, _, _, _, _, CnpId)) { clear("assembleRequest", 6, CnpId); };
	for (retrieveRequest(_, _, _, CnpId)) 		{ clear("retrieveRequest", 4, CnpId); };
	-reset.