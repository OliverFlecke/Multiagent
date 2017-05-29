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

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import data.CEntity;
import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.Percept;
import info.AgentArtifact;
import info.DynamicInfoArtifact;
import info.FacilityArtifact;
import info.ItemArtifact;
import info.JobArtifact;
import info.StaticInfoArtifact;
import jason.asSyntax.ASSyntax;
import massim.eismassim.EnvironmentInterface;
import massim.scenario.city.data.Role;
import massim.scenario.city.data.facilities.Facility;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    public static final boolean LOGGING_ENABLED = false;
    
    private EnvironmentInterfaceStandard ei;
    
    private Map<String, String> connections = new HashMap<>();

    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {
    	logger.setLevel(Level.FINE);
		logger.info("init");
		
		try 
		{
			ei = new EnvironmentInterface("conf/eismassimconfig.json");
			
			ei.start();
			
			defineObsProperty("step", 0);
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
							DynamicInfoArtifact.perceiveStep(percept);
							
							
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

	@OPERATION
	void action(String action) 
	{
		String agentName = getOpUserName();
		
		logger.info(agentName + " doing: " + action);
		
		try 
		{	
			Action ac = Translator.stringToAction(action);
			
			ei.performAction(agentName, ac);
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}	
	
	@INTERNAL_OPERATION
	void perceiveInitial() throws PerceiveException, NoEnvironmentException
	{
		logger.finest("perceiveInitial");
		
		Set<Percept> allPercepts = new HashSet<>();
		
		Map<String, Collection<Percept>> agentPercepts = new HashMap<>();

		for (Entry<String, String> entry : connections.entrySet())
		{
			String agentName = entry.getKey();
			
			Collection<Percept> percepts = ei.getAllPercepts(agentName).get(entry.getValue());
			
			agentPercepts.put(agentName, percepts);
			
			defineObsProperty("inFacility", 	agentName, null);
			defineObsProperty("charge", 		agentName, null);
			defineObsProperty("load", 			agentName, null);
			defineObsProperty("routeLength", 	agentName, null);
			defineObsProperty("lastAction", agentName, null);
			defineObsProperty("lastActionResult", agentName, null);
			
			allPercepts.addAll(percepts);
		}
		
		// Important to perceive items before facilities
		ItemArtifact        .perceiveInitial(allPercepts);
//		FacilityArtifact    .perceiveInitial(allPercepts);
		StaticInfoArtifact  .perceiveInitial(allPercepts);
		
		FacilityArtifact	.perceiveUpdate(allPercepts);
		DynamicInfoArtifact	.perceiveUpdate(allPercepts);
		JobArtifact			.perceiveUpdate(allPercepts);
		
		// Important to perceive agent perceptions after static info
		for (Entry<String, Collection<Percept>> entry : agentPercepts.entrySet())
		{					
			AgentArtifact.perceiveUpdate(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, CEntity> agent : AgentArtifact.getEntities().entrySet())
		{
			Role role = agent.getValue().getRole();
			
			defineObsProperty("myRole", agent.getKey(), role.getName());
			defineObsProperty("role", role.getName(), role.getSpeed(), role.getMaxLoad(), role.getMaxBattery(), role.getTools());
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
				String agentName = entry.getKey();
				
				Collection<Percept> percepts = ei.getAllPercepts(agentName).get(entry.getValue());
				
				AgentArtifact.perceiveUpdate(agentName, percepts);
				
				getObsPropertyByTemplate("charge", 		agentName, null).updateValue(1, AgentArtifact.getEntity(agentName).getCurrentBattery());
				getObsPropertyByTemplate("load",   		agentName, null).updateValue(1, AgentArtifact.getEntity(agentName).getCurrentLoad());
				getObsPropertyByTemplate("routeLength", agentName, null).updateValue(1, AgentArtifact.getEntity(agentName).getRouteLength());
				String action = AgentArtifact.getEntity(agentName).getLastAction().getActionType();
				getObsPropertyByTemplate("lastAction",  agentName, null).updateValue(1, action);
				getObsPropertyByTemplate("lastActionResult", agentName, null).updateValue(1, AgentArtifact.getEntity(agentName).getLastActionResult());
				
//				if (entry.getKey().equals("agentA1")) logger.info(percepts.toString());
				allPercepts.addAll(percepts);
			}
			
			FacilityArtifact	.perceiveUpdate(allPercepts);
			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
			JobArtifact			.perceiveUpdate(allPercepts);
			
			for (String agentName : AgentArtifact.getEntitiesNames())
			{
				Facility facility = AgentArtifact.getEntity(agentName).getFacility();
				
				if (facility == null) 	getObsPropertyByTemplate("inFacility", agentName, null).updateValue(1, "none");
				else 					getObsPropertyByTemplate("inFacility", agentName, null).updateValue(1, facility.getName());
			}
			
			getObsProperty("step").updateValue(DynamicInfoArtifact.getStep());
			
//			FacilityArtifact.logShops();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}	
}
