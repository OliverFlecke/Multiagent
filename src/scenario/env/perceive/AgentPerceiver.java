package scenario.env.perceive;

import java.util.Collection;
import java.util.Map;

import cartago.Artifact;
import cartago.OPERATION;
import eis.iilang.Percept;
import scenario.env.info.AgentInfo;

public class AgentPerceiver extends Artifact {
	
	// AGENT
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
	
	// The artifact's observable properties
	private static final String[] PROPERTIES = new String[] {
			CHARGE,
			LOAD,
			FACILITY,
			LAST_ACTION,
			LAST_ACTION_RESULT,
			LAST_ACTION_PARAMS
	};
	
	// Adopts the singleton pattern
	private static Map<String, AgentPerceiver> instances;
	
	// Holds agent related info
	private AgentInfo aInfo;

	public void init()
	{
		instances.put(getOpUserName(), this);
		
		aInfo = new AgentInfo();

		for (String property : PROPERTIES)
		{
			defineObsProperty(property, "");
		}
	}
	
	public static void perceive(String agent, Collection<Percept> percepts)
	{
		instances.get(agent).process(percepts);
	}
	
	private void process(Collection<Percept> percepts)
	{		
		preprocess();
		
		for (Percept p : percepts)
		{
			switch (p.getName())
			{
			case ACTION_ID			:                                                   break;
			case CHARGE 			: aInfo.setCharge	(IILParser.parseInt		(p));	break; 
			case FACILITY			: aInfo.setFacility	(IILParser.parseString	(p));   break;
			case HAS_ITEM			: aInfo.addItem		(IILParser.parseEntry	(p));   break;
			case LAST_ACTION 		:                                                   break;
			case LAST_ACTION_PARAMS :	                                                break;
			case LAST_ACTION_RESULT :	                                                break;
			case LAT 				: aInfo.setLat		(IILParser.parseDouble	(p));   break;
			case LON 				: aInfo.setLon		(IILParser.parseDouble	(p));   break;
			case LOAD				: aInfo.setLoad		(IILParser.parseInt		(p));   break;
			case ROUTE 				:                                                   break;
			case ROUTE_LENGTH 		:                                                   break;
			}			
		}

		postprocess();
	}
	
	private void preprocess()
	{
		aInfo.clearInventory();
	}
	
	@OPERATION
	private void postprocess()
	{
		for (String property : PROPERTIES)
		{
			getObsProperty(property).updateValue(getValue(property));
		}
	}
	
	private Object getValue(String property)
	{
		switch (property)
		{
		case CHARGE            	: return aInfo.getCharge	();
		case LOAD              	: return aInfo.getLoad     	();
		case FACILITY         	: return aInfo.getFacility 	();
		}
		throw new UnsupportedOperationException("Unsupported property: " + property);
	}

}
