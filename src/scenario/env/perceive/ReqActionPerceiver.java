package scenario.env.perceive;

import java.util.Collection;

import cartago.Artifact;
import cartago.OPERATION;
import eis.iilang.Percept;
import scenario.env.info.*;

public class ReqActionPerceiver extends Artifact {

	// DYNAMIC
	private static final String DEADLINE			= "deadline";
	private static final String ENTITY 				= "entity";
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
	// JOB
	private static final String AUCTION 			= "auction";
	private static final String JOB 				= "job";
	private static final String MISSION 			= "mission";
	private static final String POSTED 				= "posted";	
	
	// Adopts the singleton pattern
	private static ReqActionPerceiver instance;
	
	// Holds request-action related info
	private DynamicInfo 	dInfo;
	private FacilityInfo 	fInfo;
	private JobInfo			jInfo;
	
	void init()
	{
		instance = this;
		
		dInfo = new DynamicInfo();
		fInfo = new FacilityInfo();
		jInfo = new JobInfo();
		
		defineObsProperty("step", "");
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
			case DEADLINE		    : dInfo.setDeadline	(IILParser.parseLong			(p)); break;
			case ENTITY 			:                                                         break;
			case MONEY 			    : dInfo.setMoney	(IILParser.parseInt				(p)); break;
			case STEP 			    : dInfo.setStep		(IILParser.parseInt				(p)); break;
			case TIMESTAMP 		    : dInfo.setTimestamp(IILParser.parseLong			(p)); break;
			case CHARGING_STATION	: fInfo.addFacility	(IILParser.parseChargingStation	(p)); break;
			case DUMP 			    : fInfo.addFacility	(IILParser.parseDump			(p)); break;
			case SHOP 			    : fInfo.addFacility	(IILParser.parseShop            (p)); break;
			case STORAGE 		    : fInfo.addFacility	(IILParser.parseStorage         (p)); break;
			case WORKSHOP 		    : fInfo.addFacility	(IILParser.parseWorkshop        (p)); break;
			case RESOURCE_NODE	    : fInfo.addFacility	(IILParser.parseResourceNode    (p)); break;
			case AUCTION 		    : jInfo.addJob		(IILParser.parseAuction			(p)); break;
			case JOB 			    : jInfo.addJob		(IILParser.parseSimple			(p)); break;
			case MISSION 		    : jInfo.addJob		(IILParser.parseMission			(p)); break;
			case POSTED 			: jInfo.addJob		(IILParser.parsePosted			(p)); break;
			}
		}
		
		postprocess();
	}
	
	private void preprocess()
	{
		
	}
	
	@OPERATION
	private void postprocess()
	{
		getObsProperty("step").updateValue(dInfo.getStep());
	}

}
