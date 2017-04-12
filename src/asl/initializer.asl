
!init.

+!init : .my_name(Me) <-
	makeArtifact("EIArtifact"			, "info.EIArtifact", [], _);
	makeArtifact("ItemArtifact"			, "info.ItemArtifact", [], _);
	makeArtifact("FacilityArtifact"		, "info.FacilityArtifact", [], _);
	makeArtifact("StaticInfoArtifact"	, "info.StaticInfoArtifact", [], _);
	makeArtifact("DynamicInfoArtifact"	, "info.DynamicInfoArtifact", [], _);
	makeArtifact("TaskArtifact"			, "cnp.TaskArtifact", [], _);
	.kill_agent(Me).