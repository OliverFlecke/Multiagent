package scenario.info.bridge;

import eis.iilang.Percept;
import scenario.info.AgentInfo;

public class IILPerceiver {
	
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
	// DYNAMIC
	private static final String DEADLINE			= "deadline";
	private static final String MONEY 				= "money";
	private static final String STEP 				= "step";
	private static final String TIMESTAMP 			= "timestamp";	
	// FACILITY
	private static final String CHARGING_STATION 	= "chargingStation";
	private static final String DUMP 				= "dump";
	private static final String SHOP 				= "shop";
	private static final String STORAGE 			= "storage";
	private static final String WORKSHOP 			= "workshop";
	private static final String RESOURCE_NODE		= "resourceNode";	
	// ITEM
	private static final String ITEM 				= "item";		
	// JOB
	private static final String AUCTION 			= "auction";
	private static final String JOB 				= "job";
	private static final String MISSION 			= "mission";
	private static final String POSTED 				= "posted";		
	// STATIC
	private static final String ENTITY 				= "entity";
	private static final String ID 					= "id";
	private static final String MAP 				= "map";
	private static final String ROLE	 			= "role";
	private static final String SEED_CAPITAL 		= "seedCapital";
	private static final String STEPS 				= "steps";
	private static final String TEAM 				= "team";

	private IILPerceiver() {}
	
	static void perceive(String agent, Percept p)
	{
		AgentInfo info = AgentInfo.get(agent);
		
		info.preprocess();
		
		switch (p.getName())
		{
		case ACTION_ID			:                                                           break;
		case CHARGE 			: 	info.perceiveCharge		(IILParser.parseInt		(p));	break; 
		case FACILITY			:	info.perceiveFacility	(IILParser.parseString	(p));   break;
		case HAS_ITEM			:	info.perceiveHasItem	(IILParser.parseEntry	(p));   break;
		case LAST_ACTION 		:                                                           break;
		case LAST_ACTION_PARAMS :	                                                        break;
		case LAST_ACTION_RESULT :	                                                        break;
		case LAT 				:	info.perceiveLat		(IILParser.parseDouble	(p));   break;
		case LON 				:	info.perceiveLon		(IILParser.parseDouble	(p));   break;
		case LOAD				:                                                           break;
		case ROUTE 				:                                                           break;
		case ROUTE_LENGTH 		:                                                           break;
		}
	}
	
	static void perceive(Percept p) 
	{
		switch(p.getName())
		{
		case DEADLINE		    :
		case MONEY 			    :
		case STEP 			    :
		case TIMESTAMP 		    :
		case CHARGING_STATION	:
		case DUMP 			    :
		case SHOP 			    :
		case STORAGE 		    :
		case WORKSHOP 		    :
		case RESOURCE_NODE	    :
		case ITEM 			    :
		case AUCTION 		    :
		case JOB 			    :
		case MISSION 		    :
		case POSTED 			:
		case ENTITY 			:
		case ID 				:
		case MAP 			    :
		case ROLE	 		    :
		case SEED_CAPITAL 	    :
		case STEPS 			    :
		case TEAM 			    :
		}
	}

}
