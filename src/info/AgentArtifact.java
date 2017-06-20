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
	private static Map<String, AgentArtifact> artifacts = new HashMap<>();
	
	private String agentName;
	
	void init()
	{
		this.agentName = this.getId().getName();
		
		artifacts.put(this.agentName, this);
		
		defineObsProperty("inFacility", 		"none");               
		defineObsProperty("charge", 			250);
		defineObsProperty("load", 				0);                
		defineObsProperty("routeLength", 		0);                
		defineObsProperty("lastAction", 		"noAction"); 
		defineObsProperty("lastActionResult", 	"successful");           
		defineObsProperty("lastActionParam", 	"[]");            
	}
	
	/**
	 * Get the artifact related to the agent
	 * @param agentName Name of the agent
	 * @return 
	 */
	public static AgentArtifact getAgentArtifact(String agentName) 
	{
		return artifacts.get(agentName);
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
	
	protected static void addEntity(String name, CEntity entity)
	{
		entities.put(name, entity);
	}
	
	public static CEntity getEntity(String name)
	{
		return entities.get(name);
	}
	
	/**
	 * @return The entity associated with this agent artifact
	 */
	private CEntity getEntity()
	{
		return entities.get(this.agentName);
	}

	@OPERATION
	void getPos(OpFeedbackParam<Double> lon, OpFeedbackParam<Double> lat)
	{
		Location l = entities.get(getOpUserName()).getLocation();
		
		lon.set(l.getLon());
		lat.set(l.getLat());
	}
	
	@OPERATION
	void getAgentInventory(OpFeedbackParam<Object> ret)
	{
		ret.set(getAgentInventory(getOpUserName()));
	}
	
	public static Map<String, Integer> getAgentInventory(String agent) 
	{
		return getEntity(agent).getInventory().toItemAmountData().stream()
				.collect(Collectors.toMap(e -> e.getName(), e -> e.getAmount()));
	}
	
	public void precevieInitial(Collection<Percept> percepts)
	{
		defineObsProperty("myRole", this.getEntity().getRole().getName());
		
		perceiveUpdate(percepts);
	}
	
	public void perceiveUpdate(Collection<Percept> percepts)
	{
		execInternalOp("update", percepts);
	}
	
	@OPERATION
	private void update(Collection<Percept> percepts)
	{		
		CEntity entity = getEntity(agentName);
		
		entity.clearInventory();
		
		for (Percept percept : percepts)
		{			
			switch (percept.getName())
			{
//			case ACTION_ID: perceiveActionID(percept); break;
			case CHARGE: 				perceiveCharge(percept); break;
			case FACILITY:				perceiveFacility(percept); break;
			case HAS_ITEM:				perceiveHasItem(percept); break;
			case LAST_ACTION : 			perceiveLastAction(percept); break;
			case LAST_ACTION_PARAMS: 	perceiveLastActionParams(percept); break;
			case LAST_ACTION_RESULT: 	perceiveLastActionResult(percept); break;
			case LAT: 					perceiveLat(percept); break;
			case LON: 					perceiveLon(percept); break;
			case ROUTE: 				perceiveRoute(percept); break;
			case ROUTE_LENGTH: 			perceiveRouteLength(percept); break;
			}
		}
		
		getObsProperty("inFacility"			).updateValue(entity.getFacilityName());
		getObsProperty("charge"				).updateValue(entity.getCurrentBattery());
		getObsProperty("load"  				).updateValue(entity.getCurrentLoad());
		getObsProperty("routeLength" 		).updateValue(entity.getRouteLength());
		getObsProperty("lastAction"			).updateValue(entity.getLastAction().getActionType());
		getObsProperty("lastActionResult" 	).updateValue(entity.getLastActionResult());
		getObsProperty("lastActionParam"  	).updateValue(entity.getLastActionParam());

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info(agentName + " perceived");
		}
	}

	private void perceiveLastActionParams(Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);

		this.getEntity().setLastActionParam((Object[]) args[0]);
	}

	/**
	 * Literal(int)
	 * @param percept
	 */
	private void perceiveCharge(Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);

		this.getEntity().setCurrentBattery((int) args[0]);
	}
	
	
	public void perceiveFacility(Percept percept) 
	{
		Parameter param = percept.getParameters().get(0);
		if (!PrologVisitor.staticVisit(param).equals(""))
		{
			Object[] args = Translator.perceptToObject(percept);
			
			Facility facility = FacilityArtifact.getFacility((String) args[0]);
			
			this.getEntity().setFacility(facility);	
		}
		else 
		{
			this.getEntity().setFacility(null);
		}
	}

	private void perceiveHasItem(Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);

		Item 	item 	= ItemArtifact.getItem((String) args[0]);
		int 	amount 	= (int) args[1];
		
		this.getEntity().addItem(item, amount);
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private void perceiveLastAction(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		Action action = new Action((String) args[0]);
		
		this.getEntity().setLastAction(action);
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private void perceiveLastActionResult(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		this.getEntity().setLastActionResult((String) args[0]);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private void perceiveLat(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		this.getEntity().setLat((double) args[0]);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private void perceiveLon(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		this.getEntity().setLon((double) args[0]);	
	}
	
	/**
	 * Literal([wp(int, lat, lon)])
	 * @param agentName
	 * @param percept
	 */
	public void perceiveRoute(Percept percept)
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

		this.getEntity().setRoute(route);
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private void perceiveRouteLength(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		this.getEntity().setRouteLength((int) args[0]);	
	}
}