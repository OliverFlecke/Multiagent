package env;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AgentArtifact {
	
	private static final String ACTION_ID			= "actionID";
	private static final String CHARGE 				= "charge";
	private static final String LAST_ACTION 		= "lastAction";
	private static final String LAST_ACTION_PARAMS 	= "lastActionParams";
	private static final String LAST_ACTION_RESULT 	= "lastActionResult";
	private static final String LAT 				= "lat";
	private static final String LON 				= "lon";
	private static final String LOAD				= "load";
	private static final String ROUTE 				= "route";
	private static final String ROUTE_LENGTH 		= "routeLength";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ACTION_ID, CHARGE, LAST_ACTION, LAST_ACTION_PARAMS, 
				LAST_ACTION_RESULT, LAT, LON, LOAD, ROUTE, ROUTE_LENGTH)));

}
