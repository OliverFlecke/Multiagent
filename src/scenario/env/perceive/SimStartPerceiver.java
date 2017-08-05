package scenario.env.perceive;

import java.util.Collection;

import cartago.Artifact;
import cartago.OPERATION;
import eis.iilang.Percept;
import scenario.data.Role;
import scenario.env.info.*;

public class SimStartPerceiver extends Artifact {
	
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
		instance.process(percepts);
	}
	
	private void process(Collection<Percept> percepts)
	{
		preprocess();
		
		for (Percept p : percepts)
		{
			switch (p.getName())
			{
			case ITEM 			    : iInfo.addItem			(IILParser.parseItem	(p)); break;
			case ID 				: sInfo.setId			(IILParser.parseString	(p)); break;
			case MAP 			    : sInfo.setMap			(IILParser.parseString	(p)); break;
			case MIN_LAT            : sInfo.setMinLat       (IILParser.parseDouble  (p)); break;
			case MAX_LAT            : sInfo.setMaxLat       (IILParser.parseDouble  (p)); break;
			case MIN_LON            : sInfo.setMinLon       (IILParser.parseDouble  (p)); break;
			case MAX_LON            : sInfo.setMaxLon       (IILParser.parseDouble  (p)); break;
			case ROLE	 		    : sInfo.addRole			(IILParser.parseRole	(p)); break;
			case SEED_CAPITAL 	    : sInfo.setSeedCapital	(IILParser.parseInt		(p)); break;
			case STEPS 			    : sInfo.setSteps		(IILParser.parseInt		(p)); break;
			case TEAM 			    : sInfo.setTeam			(IILParser.parseString	(p)); break;
			}
		}
		
		postprocess();
	}
	
	private void preprocess()
	{
		removeObsProperty("role");
	}
	
	@OPERATION
	private void postprocess()
	{
		for (Role r : sInfo.getRoles())
		{
			defineObsProperty("role", r.getRoleData());
		}
		sInfo.initCityMap();
	}
}
