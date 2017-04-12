
!init.

+!init : .my_name(Me) <-
	makeArtifact("EIArtifact", "env.EIArtifact", [], _);
	makeArtifact("ItemArtifact", "env.ItemArtifact", [], _);
	makeArtifact("FacilityArtifact", "env.FacilityArtifact", [], _);
	makeArtifact("StaticInfoArtifact", "env.StaticInfoArtifact", [], _);
	makeArtifact("DynamicInfoArtifact", "env.DynamicInfoArtifact", [], _);
	makeArtifact("TaskArtifact", "cnp.TaskArtifact", [], _);
	.kill_agent(Me).