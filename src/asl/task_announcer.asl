!announce.

+!announce <- 
	!setupTaskArtifact(Id);
	announceTask("description", 5000, CNPName)[artifact_id(Id)];
	.print("Announced task: ", CNPName).
	
+!setupTaskArtifact(A) <-
	makeArtifact("TaskArtifact", "cnp.TaskArtifact", [], A).