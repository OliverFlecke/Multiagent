package env;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import data.CEntity;
import eis.iilang.Percept;
import jason.asSyntax.Term;
import massim.scenario.city.data.Entity;
import massim.scenario.city.data.Location;

public class DynamicInfoArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(DynamicInfoArtifact.class.getName());

	private static final String ACTION_ID			= "actionID";
	private static final String CHARGE 				= "charge";
	private static final String DEADLINE			= "deadline";
	private static final String LAST_ACTION 		= "lastAction";
	private static final String LAST_ACTION_PARAMS 	= "lastActionParams";
	private static final String LAST_ACTION_RESULT 	= "lastActionResult";
	private static final String LAT 				= "lat";
	private static final String LON 				= "lon";
	private static final String LOAD				= "load";
	private static final String MONEY 				= "money";
	private static final String ROUTE 				= "route";
	private static final String ROUTE_LENGTH 		= "routeLength";
	private static final String STEP 				= "step";
	private static final String TIMESTAMP 			= "timestamp";
	
	public static final Set<String>	DYNAMIC_PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ACTION_ID, CHARGE, DEADLINE, LAST_ACTION, LAST_ACTION_PARAMS, 
				LAST_ACTION_RESULT, LAT, LON, LOAD, MONEY, ROUTE, ROUTE_LENGTH, STEP, TIMESTAMP)));

	private static Map<String, CEntity> entities = new HashMap<>();
	private static int					money;
	private static int					step;

	@OPERATION
	void getPos(OpFeedbackParam<Double> lon, OpFeedbackParam<Double> lat)
	{
		Location l = entities.get(getOpUserName()).getLocation();
		
		lon.set(l.getLon());
		lat.set(l.getLat());
	}
	
	@OPERATION
	void getMoney(OpFeedbackParam<Integer> money)
	{
		money.set(DynamicInfoArtifact.money);
	}
	
	

	// Literal(int)
	private static void perceiveStep(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		step = Translator.termToInteger(args[0]);
	}

	// Literal(int)
	private static void perceiveMoney(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		money = Translator.termToInteger(args[0]);
	}
	
	protected static void addEntity(String name, CEntity entity)
	{
		entities.put(name, entity);
	}
	
	protected static CEntity getEntity(String name)
	{
		return entities.get(name);
	}

}
