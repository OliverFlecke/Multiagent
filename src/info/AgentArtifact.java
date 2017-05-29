package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import data.CEntity;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import eis.iilang.PrologVisitor;
import env.EIArtifact;
import env.Translator;
import massim.protocol.messagecontent.Action;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Route;
import massim.scenario.city.data.facilities.Facility;

public class AgentArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(AgentArtifact.class.getName());
	
	private static final String ACTION_ID			= "actionID";
	private static final String CHARGE 				= "charge";
	private static final String FACILITY			= "facility";
	private static final String HAS_ITEM			= "hasItem";
	private static final String LAST_ACTION 		= "lastAction";
	private static final String LAST_ACTION_PARAMS 	= "lastActionParams";
	private static final String LAST_ACTION_RESULT 	= "lastActionResult";
	private static final String LAT 				= "lat";
	private static final String LON 				= "lon";
	private static final String LOAD				= "load";
	private static final String ROUTE 				= "route";
	private static final String ROUTE_LENGTH 		= "routeLength";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ACTION_ID, CHARGE, FACILITY, HAS_ITEM, LAST_ACTION, LAST_ACTION_PARAMS, 
				LAST_ACTION_RESULT, LAT, LON, LOAD, ROUTE, ROUTE_LENGTH)));
	
	private static Map<String, CEntity> entities = new HashMap<>();
	
	private static AgentArtifact instance;
	
	void init()
	{
		instance = this;
	}
	
	public static AgentArtifact getInstance()
	{
		return instance;
	}

	@OPERATION
	void getPos(OpFeedbackParam<Double> lon, OpFeedbackParam<Double> lat)
	{
		Location l = entities.get(getOpUserName()).getLocation();
		
		lon.set(l.getLon());
		lat.set(l.getLat());
	}
	
	@OPERATION
	void getAgentInventory(String agentName, OpFeedbackParam<Object> ret)
	{
		ret.set(getEntity(agentName).getInventory().toItemAmountData().stream()
				.collect(Collectors.toMap(e -> e.getName(), e -> e.getAmount())));
	}
	
	public static void perceiveUpdate(String agentName, Collection<Percept> percepts)
	{		
		for (Percept percept : percepts)
		{			
			switch (percept.getName())
			{
//			case ACTION_ID: perceiveActionID(percept); break;
			case CHARGE: 				perceiveCharge(agentName, percept); break;
			case FACILITY:				getInstance().perceiveFacility(agentName, percept); break;
			case HAS_ITEM:				perceiveHasItem(agentName, percept); break;
			case LAST_ACTION : 			perceiveLastAction(agentName, percept); break;
//			case LAST_ACTION_PARAMS: 	perceiveLastActionParams(agentName, percept); break;
			case LAST_ACTION_RESULT: 	perceiveLastActionResult(agentName, percept); break;
			case LAT: 					perceiveLat(agentName, percept); break;
			case LON: 					perceiveLon(agentName, percept); break;
//			case LOAD: 					perceiveLoad(agentName, percept); break;
			case ROUTE: 				perceiveRoute(agentName, percept); break;
			case ROUTE_LENGTH: 			perceiveRouteLength(agentName, percept); break;
			}
		}
		
		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info(agentName + " perceived");
		}
	}

	/**
	 * Literal(int)
	 * @param percept
	 */
	private static void perceiveCharge(String agentName, Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);

		AgentArtifact.getEntity(agentName).setCurrentBattery((int) args[0]);
	}
	
	@OPERATION
	public void perceiveFacility(String agentName, Percept percept) 
	{
		Parameter param = percept.getParameters().get(0);
		if (!PrologVisitor.staticVisit(param).equals(""))
		{
			Object[] args = Translator.perceptToObject(percept);
			
			Facility facility = FacilityArtifact.getFacility((String) args[0]);
			
			AgentArtifact.getEntity(agentName).setFacility(facility);
			
		}
		else 
		{
			AgentArtifact.getEntity(agentName).setFacility(null);
		}
	}

	private static void perceiveHasItem(String agentName, Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);

		Item 	item 	= ItemArtifact.getItem((String) args[0]);
		int 	amount 	= (int) args[1];
		
		AgentArtifact.getEntity(agentName).addItem(item, amount);
//		logger.info(AgentArtifact.getEntity(agentName).getInventory().toString());
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLastAction(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		Action action = new Action((String) args[0]);
		
		AgentArtifact.getEntity(agentName).setLastAction(action);
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLastActionResult(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		AgentArtifact.getEntity(agentName).setLastActionResult((String) args[0]);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLat(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		AgentArtifact.getEntity(agentName).setLat((double) args[0]);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLon(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		AgentArtifact.getEntity(agentName).setLon((double) args[0]);	
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
//	private static void perceiveLoad(String agentName, Percept percept)
//	{
//		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
//		AgentArtifact.getEntity(agentName).s.t(Translator.termToDouble(terms[0]));	
//	}
	
	/**
	 * Literal([wp(int, lat, lon)])
	 * @param agentName
	 * @param percept
	 */
	public static void perceiveRoute(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		Route route = new Route();

		for (Object term : args)
		{
			for (Object arg : (Object[]) term)
			{
				Object[] values = (Object[]) arg;
	
				double lat = (double) values[1];
				double lon = (double) values[2];
				route.addPoint(new Location(lon, lat));
			}
		}

		AgentArtifact.getEntity(agentName).setRoute(route);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveRouteLength(String agentName, Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		AgentArtifact.getEntity(agentName).setRouteLength((int) args[0]);	
	}

	protected static void addEntity(String name, CEntity entity)
	{
		entities.put(name, entity);
	}
	
	public static CEntity getEntity(String name)
	{
		return entities.get(name);
	}

	/**
	 * @return All the entities
	 */
	public static Map<String, CEntity> getEntities() {
		return entities;
	}

	/**
	 * @return The names of all the entities
	 */
	public static Set<String> getEntitiesNames() {
		return entities.keySet();
	}
}
