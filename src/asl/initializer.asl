!init.

+!init : .my_name(Me) <-
	makeArtifact("EIArtifact", "env.EIArtifact", [], _);
	makeArtifact("ItemArtifact", "info.ItemArtifact", [], _);
	makeArtifact("FacilityArtifact", "info.FacilityArtifact", [], _);
	makeArtifact("StaticInfoArtifact", "info.StaticInfoArtifact", [], _);
	makeArtifact("DynamicInfoArtifact", "info.DynamicInfoArtifact", [], _);
	makeArtifact("JobArtifact", "info.JobArtifact", [], _);
	makeArtifact("TaskArtifact", "cnp.TaskArtifact", [], _);
	.kill_agent(Me).