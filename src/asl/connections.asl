// Connections 
connection(C) :- .my_name(Me) & connection(Me, C).

connection(agentA1, connectionA1).
connection(agentA2, connectionA2).
connection(agentA3, connectionA3).
connection(agentA4, connectionA4).
connection(agentA5, connectionA5).
connection(agentA6, connectionA6).
connection(agentA7, connectionA7).
connection(agentA8, connectionA8).
connection(agentA9, connectionA9).
connection(agentA10, connectionA10).
connection(agentA11, connectionA11).
connection(agentA12, connectionA12).
connection(agentA13, connectionA13).
connection(agentA14, connectionA14).
connection(agentA15, connectionA15).
connection(agentA16, connectionA16).

// Plans
+!register : connection(C) <- register(C).
-!register <- .wait(100); !register.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("AgentArtifact");
	!focusArtifact("EIArtifact").	
-!focusArtifacts <- .print("Failed focusing artifacts"); .wait(500); !focusArtifacts.
