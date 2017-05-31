package env;
// Environment code for project multiagent_jason


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggerFactory;
import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import data.CEntity;
import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;
import eis.iilang.Percept;
import info.AgentArtifact;
import info.DynamicInfoArtifact;
import info.FacilityArtifact;
import info.ItemArtifact;
import info.JobArtifact;
import info.StaticInfoArtifact;
import jason.asSyntax.Literal;
import massim.eismassim.EnvironmentInterface;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Role;
import massim.scenario.city.data.facilities.Shop;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    public static final boolean LOGGING_ENABLED = false;
    
    private static EnvironmentInterfaceStandard ei;
    
    private static Map<String, String> connections = new HashMap<>();

    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {    	
    	logger.setLevel(Level.INFO);
		logger.info("init");
		
		try 
		{
			ei = new EnvironmentInterface("conf/eismassimconfig.json");
			
			ei.start();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in init: " + e.getMessage(), e);
		}
    }
	
	@OPERATION
	void register(String entity)  
	{
		String agentName = getOpUserName();
		
		logger.fine("register " + agentName + " on " + entity);
		
		try 
		{			
			ei.registerAgent(agentName);
			
			ei.associateEntity(agentName, entity);
			
			connections.put(agentName, entity);

			if (connections.size() == ei.getEntities().size())
			{
				// Perceive initial perceptions when all agents have connected
				execInternalOp("perceiveInitial");
				
				// Attach listener for perceiving the following steps
				ei.attachAgentListener(agentName, new AgentListener() 
				{				
					@Override
					public void handlePercept(String agentName, Percept percept) 
					{
						if (percept.getName().equals("step"))
						{
							execInternalOp("perceiveUpdate");
						}
					}
				});
			}
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in register: " + e.getMessage(), e);
		}
	}
	
	public static void performAction(String agentName, Action action)
	{
		try 
		{
			ei.performAction(agentName, action);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in performAction: " + e.getMessage(), e);
		}
	}
	
	@OPERATION
	void action(String action) {
		execInternalOp("doAction", getOpUserName(), action);
	}

	@INTERNAL_OPERATION
	void doAction(String agentName, String action) 
	{		
		logger.info("Step " + DynamicInfoArtifact.getStep() + ": " + agentName + " doing " + action);
		
		try 
		{	
			Action ac = Translator.stringToAction(action);
			
			if (ac.getName().equals("buy"))
			{
				Literal actionLiteral = Literal.parseLiteral(action);
				
				CEntity agent = AgentArtifact.getEntity(agentName);
				
				Shop shop = (Shop) FacilityArtifact.getFacility(agent.getFacilityName());
				
				if (shop != null)
				{
					Item item = ItemArtifact.getItem((String) Translator.termToObject(actionLiteral.getTerm(0)));
					
					int amount = (int) Translator.termToObject(actionLiteral.getTerm(1));
					
					shop.buy(item, amount);					
				}				
			}
			
			ei.performAction(agentName, ac);
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}
	
	@INTERNAL_OPERATION
	void perceiveInitial()
	{
		logger.finest("perceiveInitial");
		
		try 
		{
			Set<Percept> allPercepts = new HashSet<>();
			
			Map<String, Collection<Percept>> agentPercepts = new HashMap<>();
	
			for (Entry<String, String> entry : connections.entrySet())
			{
				String agentName = entry.getKey();
				
				Collection<Percept> percepts = ei.getAllPercepts(agentName).get(entry.getValue());
				
				agentPercepts.put(agentName, percepts);
				
				allPercepts.addAll(percepts);
			}
			
			// Perceive static info
			ItemArtifact        .perceiveInitial(allPercepts);
			StaticInfoArtifact  .perceiveInitial(allPercepts);
			// Perceive dynamic info
			FacilityArtifact	.perceiveUpdate(allPercepts);
			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
			JobArtifact			.perceiveUpdate(allPercepts);
	
			// Perceive agent info
			for (Entry<String, Collection<Percept>> entry : agentPercepts.entrySet())
			{			
				AgentArtifact.perceiveUpdate(entry.getKey(), entry.getValue());
			}
			
			// Define agent properties
			for (Entry<String, CEntity> entry : AgentArtifact.getEntities().entrySet())
			{
				String 		agentName 	= entry.getKey();
				CEntity 	entity		= entry.getValue();
	
				defineObsProperty("inFacility", 		agentName, entity.getFacilityName());               
				defineObsProperty("charge", 			agentName, entity.getCurrentBattery());             
				defineObsProperty("load", 				agentName, entity.getCurrentLoad());                
				defineObsProperty("routeLength", 		agentName, entity.getRouteLength());                
				defineObsProperty("lastAction", 		agentName, entity.getLastAction().getActionType()); 
				defineObsProperty("lastActionResult", 	agentName, entity.getLastActionResult());           
				defineObsProperty("lastActionParam", 	agentName, entity.getLastActionParam());            
				defineObsProperty("myRole", 			agentName, entity.getRole().getName());
			}
			
			// Define roles
			for (Role role : StaticInfoArtifact.getRoles())
			{
				defineObsProperty("role", role.getName(), role.getSpeed(), role.getMaxLoad(), 
										  role.getMaxBattery(), role.getPermissions().toArray());
			}
			
			// Define step
			defineObsProperty("step", DynamicInfoArtifact.getStep());
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}
	
	@INTERNAL_OPERATION
	void perceiveUpdate() 
	{		
		logger.finest("perceiveUpdate");
		
		try 
		{
			Set<Percept> allPercepts = new HashSet<>();

			for (Entry<String, String> entry : connections.entrySet())
			{				
				Collection<Percept> percepts = ei.getAllPercepts(entry.getKey()).get(entry.getValue());
				
				AgentArtifact.perceiveUpdate(entry.getKey(), percepts);
				
				allPercepts.addAll(percepts);
			}
			
			FacilityArtifact	.perceiveUpdate(allPercepts);
			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
			JobArtifact			.perceiveUpdate(allPercepts);
			
			// Update observable properties
			for (Entry<String, CEntity> entry : AgentArtifact.getEntities().entrySet())
			{				
				String 		agentName 	= entry.getKey();
				CEntity 	entity		= entry.getValue();
				
				if (agentName.equals("agentA1"))
				{
					System.out.println("Step " + DynamicInfoArtifact.getStep() + ": " + entity.getFacilityName());
				}
				
				getObsPropertyByTemplate("inFacility", 		 agentName, null).updateValue(1, entity.getFacilityName());
				getObsPropertyByTemplate("charge", 			 agentName, null).updateValue(1, entity.getCurrentBattery());
				getObsPropertyByTemplate("load",   			 agentName, null).updateValue(1, entity.getCurrentLoad());
				getObsPropertyByTemplate("routeLength", 	 agentName, null).updateValue(1, entity.getRouteLength());
				getObsPropertyByTemplate("lastAction",  	 agentName, null).updateValue(1, entity.getLastAction().getActionType());
				getObsPropertyByTemplate("lastActionResult", agentName, null).updateValue(1, entity.getLastActionResult());
				getObsPropertyByTemplate("lastActionParam",  agentName, null).updateValue(1, entity.getLastActionParam());
			}
			
			getObsProperty("step").updateValue(DynamicInfoArtifact.getStep());
			
			logData();

			JobArtifact.announceJobs();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}	
	
	static Logger fileLogger = LoggerFactory.createFileLogger();
	
	private void logData()
	{
		fileLogger.info("Step: " + DynamicInfoArtifact.getStep() + " - Money: " + DynamicInfoArtifact.getMoney());
	}
}
