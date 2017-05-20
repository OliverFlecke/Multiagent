package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.GUARD;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import data.CEntity;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import eis.iilang.PrologVisitor;
import env.EIArtifact;
import env.Translator;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import massim.protocol.messagecontent.Action;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Route;
import massim.scenario.city.data.facilities.Facility;

public class AgentArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(AgentArtifact.class.getName());
	
	private static final String ACTION_ID			= "actionID";
	private static final String CHARGE 				= "charge";
	private static final String FACILITY			= "facility";
	private static final String LAST_ACTION 		= "lastAction";
	private static final String LAST_ACTION_PARAMS 	= "lastActionParams";
	private static final String LAST_ACTION_RESULT 	= "lastActionResult";
	private static final String LAT 				= "lat";
	private static final String LON 				= "lon";
	private static final String LOAD				= "load";
	private static final String ROUTE 				= "route";
	private static final String ROUTE_LENGTH 		= "routeLength";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ACTION_ID, CHARGE, FACILITY, LAST_ACTION, LAST_ACTION_PARAMS, 
				LAST_ACTION_RESULT, LAT, LON, LOAD, ROUTE, ROUTE_LENGTH)));
	
	private static Map<String, CEntity> entities = new HashMap<>();

	@OPERATION
	void getPos(OpFeedbackParam<Double> lon, OpFeedbackParam<Double> lat)
	{
		Location l = entities.get(getOpUserName()).getLocation();
		
		lon.set(l.getLon());
		lat.set(l.getLat());
	}
	
	@OPERATION
	void waitUntilInFacility(String facilityName)
	{
		await("inFacility", facilityName);
	}
	
	@OPERATION
	void updateFacility()
	{
		Facility facility = entities.get(getOpUserName()).getFacility();
		
		String facilityName = "none";
		if (facility != null)
		{
			facilityName = facility.getName();
		}
		
		signal(getOpUserId(), "inFacility", facilityName);
	}
	

	
	public static void perceiveUpdate(String agentName, Collection<Percept> percepts)
	{		
		for (Percept percept : percepts)
		{			
			switch (percept.getName())
			{
//			case ACTION_ID: perceiveActionID(percept); break;
			case CHARGE: 				perceiveCharge(agentName, percept); break;
			case FACILITY:				perceiveFacility(agentName, percept); break;
			case LAST_ACTION : 			perceiveLastAction(agentName, percept); break;
//			case LAST_ACTION_PARAMS: 	perceiveLastActionParams(agentName, percept); break;
			case LAST_ACTION_RESULT: 	perceiveLastActionResult(agentName, percept); break;
			case LAT: 					perceiveLat(agentName, percept); break;
			case LON: 					perceiveLon(agentName, percept); break;
//			case LOAD: 					perceiveLoad(agentName, percept); break;
//			case ROUTE: 				perceiveRoute(agentName, percept); break;
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
//		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		AgentArtifact.getEntity(agentName).setCurrentBattery((int) args[0]);
	}
	
	private static void perceiveFacility(String agentName, Percept percept) 
	{
		Parameter param = percept.getParameters().get(0);
		if (!PrologVisitor.staticVisit(param).equals(""))
		{
			Object[] args = Translator.perceptToObject(percept);
			AgentArtifact.getEntity(agentName).setFacility(FacilityArtifact.getFacility((String) args[0]));
		}
		else 
		{
			AgentArtifact.getEntity(agentName).setFacility(null);
		}
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLastAction(String agentName, Percept percept)
	{
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		Action action = new Action(Translator.termToString(terms[0]));
		AgentArtifact.getEntity(agentName).setLastAction(action);
	}
	
	/**
	 * Literal(String)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLastActionResult(String agentName, Percept percept)
	{
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		AgentArtifact.getEntity(agentName).setLastActionResult(Translator.termToString(terms[0]));
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLat(String agentName, Percept percept)
	{
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		AgentArtifact.getEntity(agentName).setLat(Translator.termToDouble(terms[0]));
	}
	
	/**
	 * Literal(int)
	 * @param agentName
	 * @param percept
	 */
	private static void perceiveLon(String agentName, Percept percept)
	{
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		AgentArtifact.getEntity(agentName).setLon(Translator.termToDouble(terms[0]));	
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
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		Route route = new Route();
		for (Term term : terms)
		{
			for (Term arg : Translator.termToTermList(term))
			{
				Term[] values = Translator.termToLiteral(arg).getTermsArray();
	
				double lat = Translator.termToDouble(values[1]);
				double lon = Translator.termToDouble(values[2]);
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
		Term[] terms = Translator.perceptToLiteral(percept).getTermsArray();
		AgentArtifact.getEntity(agentName).setRouteLength(Translator.termToInteger(terms[0]));	
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
