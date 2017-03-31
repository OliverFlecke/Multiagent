// Connections 
connection(C) :- .my_name(Me) & .term2string(Me, Name) & connection(Name, C).
connection("agentA1", "connectionA1").

// Insert other connections
