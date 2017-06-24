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
import logging.LoggerFactory;
import massim.eismassim.EnvironmentInterface;
import massim.scenario.city.data.Role;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    public static final boolean LOGGING_ENABLED = false;
    
    private static EnvironmentInterfaceStandard ei;
    private static final String TEAM_A = "conf/eismassimconfig.json";
	private static final String TEAM_B = "conf/eismassimconfig_team_B.json";
    
    private String configFile = TEAM_B;
   
    private static Map<String, String> connections 	= new HashMap<>();
    private static Map<String, String> entities		= new HashMap<>();
    
    private String team;

    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {    	
    	logger.setLevel(Level.SEVERE);
		logger.info("init");
		
		try 
		{
			ei = new EnvironmentInterface(configFile);
			
			// Get the team name from EI. Should be a better way
			this.team = ((String) (ei.getEntities().toArray())[0]).substring(10, 11);
			
			fileLogger = LoggerFactory.createFileLogger(team);
			
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
		String agentName 	= getOpUserName();
		String id 			= agentName.substring(5);
		String connection = "connection" + team + id;
		
		logger.fine("register " + agentName + " on " + connection);
		
		try 
		{			
			ei.registerAgent(agentName);
			
			ei.associateEntity(agentName, connection);
			
			connections	.put(agentName, connection);
			entities	.put(entity, agentName);			

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
		logger.fine("Step " + DynamicInfoArtifact.getStep() + ": " + agentName + " doing " + action);
		
		try 
		{			
			ei.performAction(agentName, action);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in performAction: " + e.getMessage(), e);
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
	
			// Define roles
			for (Role role : StaticInfoArtifact.getRoles())
			{
				defineObsProperty("role", role.getName(), role.getSpeed(), role.getMaxLoad(), 
						role.getMaxBattery(), role.getPermissions().toArray());
			}

			// Perceive agent info
			for (Entry<String, Collection<Percept>> entry : agentPercepts.entrySet())
			{			
				String agentName = entry.getKey();
				
				AgentArtifact.getAgentArtifact(agentName).precevieInitial(entry.getValue());
			}
			
			// Define step
			defineObsProperty("step", DynamicInfoArtifact.getStep());
			
			FacilityArtifact.announceShops();
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
				
				AgentArtifact.getAgentArtifact(entry.getKey()).perceiveUpdate(percepts);
				
				allPercepts.addAll(percepts);
			}
			
			FacilityArtifact	.perceiveUpdate(allPercepts);
			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
			JobArtifact			.perceiveUpdate(allPercepts);

			getObsProperty("step").updateValue(DynamicInfoArtifact.getStep());
			
			logData();

			JobArtifact.announceJobs();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}	
	
	private static Logger fileLogger;
	
	private void logData()
	{
		fileLogger.info("Step: " + DynamicInfoArtifact.getStep() + " - Money: " + DynamicInfoArtifact.getMoney());
		
		if (DynamicInfoArtifact.getStep() == 999)
		{
			fileLogger.info("Completed jobs: " + DynamicInfoArtifact.getJobsCompleted());
		}
	}
	
	public static String getAgentName(String entity)
	{
		return entities.get(entity);
	}
}
