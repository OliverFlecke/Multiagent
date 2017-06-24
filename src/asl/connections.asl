// Connections 
connection(A, C) :- .my_name(Me) & connection(Me, A, C).

connection(car1,        agentA1 , connectionA1 ).
connection(car2,        agentA2 , connectionA2 ).
connection(car3,        agentA3 , connectionA3 ).
connection(car4,        agentA4 , connectionA4 ).
connection(car5,        agentA5 , connectionA5 ).
connection(car6,        agentA6 , connectionA6 ).
connection(car7,        agentA7 , connectionA7 ).
connection(car8,        agentA8 , connectionA8 ).
connection(drone1,      agentA9 , connectionA9 ).
connection(drone2,      agentA10, connectionA10).
connection(drone3,      agentA11, connectionA11).
connection(drone4,      agentA12, connectionA12).
connection(motorcycle1, agentA13, connectionA13).
connection(motorcycle2, agentA14, connectionA14).
connection(motorcycle3, agentA15, connectionA15).
connection(motorcycle4, agentA16, connectionA16).
connection(motorcycle5, agentA17, connectionA17).
connection(motorcycle6, agentA18, connectionA18).
connection(motorcycle7, agentA19, connectionA19).
connection(motorcycle8, agentA20, connectionA20).
connection(truck1,      agentA21, connectionA21).
connection(truck2,      agentA22, connectionA22).
connection(truck3,      agentA23, connectionA23).
connection(truck4,      agentA24, connectionA24).
connection(truck5,      agentA25, connectionA25).
connection(truck6,      agentA26, connectionA26).
connection(truck7,      agentA27, connectionA27).
connection(truck8,      agentA28, connectionA28).

// Plans
+!register : .my_name(Me) & .term2string(Me, Name) <- register(Me).
-!register <- .wait(100); !register.

+!focusArtifact(Name) <- lookupArtifact(Name, Id); focus(Id).
+!focusArtifacts : .my_name(Me) & .term2string(Me, Name) <-
	!focusArtifact("TaskArtifact");
	!focusArtifact("EIArtifact");
	makeArtifact(Name, "info.AgentArtifact", [], _);
	!focusArtifact(Name);
	.print("Successfully focused artifacts").
-!focusArtifacts <- .wait(500); !focusArtifacts.
