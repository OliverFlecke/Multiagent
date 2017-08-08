+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts : .my_name(Me) & .term2string(Me, AgentPerceiver) <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("SimStartPerceiver");
	!focusArtifact("ReqActionPerceiver");
	!focusArtifact(AgentPerceiver).
-!focusArtifacts <- .wait(500); !focusArtifacts.
