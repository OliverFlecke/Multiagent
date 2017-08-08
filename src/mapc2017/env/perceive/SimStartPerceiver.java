package mapc2017.env.perceive;

import java.util.Collection;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import eis.iilang.Percept;
import mapc2017.data.Entity;
import mapc2017.data.Item;
import mapc2017.data.Role;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;

public class SimStartPerceiver extends Artifact {

	// DYNAMIC (but only used initially)
	private static final String ENTITY 				= "entity";
	// ITEM
	private static final String ITEM 				= "item";		
	// STATIC
	private static final String ID 					= "id";
	private static final String MAP 				= "map";
	private static final String MIN_LAT 			= "minLat";
	private static final String MAX_LAT 			= "maxLat";
	private static final String MIN_LON 			= "minLon";
	private static final String MAX_LON 			= "maxLon";	
	private static final String ROLE	 			= "role";
	private static final String SEED_CAPITAL 		= "seedCapital";
	private static final String STEPS 				= "steps";
	private static final String TEAM 				= "team";
	
	// Adopts the singleton pattern
	private static SimStartPerceiver instance;
	
	// Holds sim-start related info
	private ItemInfo		iInfo;
	private StaticInfo		sInfo;
	
	void init()
	{
		instance = this;
		
		iInfo = new ItemInfo();
		sInfo = new StaticInfo();
	}
	
	public static void perceive(Collection<Percept> percepts) 
	{
		instance.execInternalOp("process", percepts);
	}
	
	@INTERNAL_OPERATION
	private void process(Collection<Percept> percepts)
	{
		preprocess();
		
		for (Percept p : percepts)
		{
			switch (p.getName())
			{			
			case ENTITY 			: sInfo.addEntity		(IILParser.parseEntity	(p)); break;
			case ITEM 			    : iInfo.addItem			(IILParser.parseItem	(p)); break;
			case ID 				: sInfo.setId			(IILParser.parseString	(p)); break;
			case MAP 			    : sInfo.setMap			(IILParser.parseString	(p)); break;
			case MIN_LAT            : sInfo.setMinLat       (IILParser.parseDouble  (p)); break;
			case MAX_LAT            : sInfo.setMaxLat       (IILParser.parseDouble  (p)); break;
			case MIN_LON            : sInfo.setMinLon       (IILParser.parseDouble  (p)); break;
			case MAX_LON            : sInfo.setMaxLon       (IILParser.parseDouble  (p)); break;
			case ROLE	 		    : sInfo.addRole			(IILParser.parseRole	(p)); break;
			case SEED_CAPITAL 	    : sInfo.setSeedCapital	(IILParser.parseLong	(p)); break;
			case STEPS 			    : sInfo.setSteps		(IILParser.parseInt		(p)); break;
			case TEAM 			    : sInfo.setTeam			(IILParser.parseString	(p)); break;
			}
		}
		
		postprocess();
	}
	
	private void preprocess()
	{
		
	}
	
	private void postprocess()
	{
		for (Entity entity : sInfo.getTeamEntities())
		{			
			String 	agent 	= "agent" + entity.getName().substring(6);			
			Role 	role 	= sInfo.getRole(entity.getRole());
			
			AgentPerceiver.updateAgentRole(agent, role);
		}
		
		for (Role role : sInfo.getRoles())
		{
			for (String tool : role.getTools())
			{
				iInfo.getTool(tool).addRole(role.getName());
			}
		}
		
		sInfo.initCityMap();
		
		for (Item item : iInfo.getItems())
		{
			item.calculateBaseRequirements();
		}
	}
}
