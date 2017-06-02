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
connection(agentA17, connectionA17).
connection(agentA18, connectionA18).
connection(agentA19, connectionA19).
connection(agentA20, connectionA20).
connection(agentA21, connectionA21).
connection(agentA22, connectionA22).
connection(agentA23, connectionA23).
connection(agentA24, connectionA24).
connection(agentA25, connectionA25).
connection(agentA26, connectionA26).
connection(agentA27, connectionA27).
connection(agentA28, connectionA28).

// Plans
+!register : connection(C) <- register(C).
-!register <- .wait(100); !register.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("EIArtifact");
	.print("Successfully focused artifacts").
-!focusArtifacts <- .wait(500); !focusArtifacts.
