package env;
// Environment code for project multiagent_jason


import java.util.*;
import java.util.Map.Entry;
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
import massim.eismassim.EnvironmentInterface;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    private EnvironmentInterfaceStandard ei;
    
    private Map<String, String> connections = new HashMap<>();
    
    private boolean hasPerceivedInitial = false;
    
    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {
		logger.info("init");
		
		try 
		{
			ei = new EnvironmentInterface("conf/eismassimconfig.json");
//			ei = EILoader.fromClassName("massim.eismassim.EnvironmentInterface");
			
			ei.start();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in init: " + e.getMessage(), e);
		}
    }
	
	@INTERNAL_OPERATION
	void register(String entity)  
	{
		String agName = getOpUserName();
		
		logger.info("register " + agName + " on " + entity);
		
		try 
		{			
			ei.registerAgent(agName);
			
			ei.associateEntity(agName, entity);
			
			connections.put(agName, entity);
			
			if (!hasPerceivedInitial && connections.size() == ei.getEntities().size())
			{
				logger.info("Perceiving initial percepts");
				
				Set<Percept> initialPercepts = new HashSet<>();
				
				Map<String, Collection<Percept>> agentPercepts = new HashMap<>();

				for (Entry<String, String> entry : connections.entrySet())
				{
					Collection<Percept> percepts = ei.getAllPercepts(entry.getKey()).get(entry.getValue());
					
					agentPercepts.put(entry.getKey(), percepts);
					
					initialPercepts.addAll(percepts);
				}
				
				// Important to perceive items before facilities
				ItemArtifact        .perceiveInitial(initialPercepts);
				FacilityArtifact    .perceiveInitial(initialPercepts);
				StaticInfoArtifact  .perceiveInitial(initialPercepts);
				
				FacilityArtifact	.perceiveUpdate(initialPercepts);
				DynamicInfoArtifact	.perceiveUpdate(initialPercepts);
				JobArtifact			.perceiveUpdate(initialPercepts);

				for (String agentName : connections.keySet())
				{					
					AgentArtifact.perceiveUpdate(agentName, agentPercepts.get(agentName));
				}
				
				hasPerceivedInitial = true;
				
				// Attach listener for perceiving the following steps
				ei.attachAgentListener(agName, new AgentListener() 
				{				
					@Override
					public void handlePercept(String agent, Percept percept) 
					{
						if (percept.getName().equals("step"))
							execInternalOp("perceiveUpdate");
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
		String agName = getOpUserName();
		
		logger.info(agName + " doing: " + action);
		
		try 
		{	
			Action ac = Translator.stringToAction(action);
			
			ei.performAction(agName, ac);
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}	
	
	@INTERNAL_OPERATION
	void perceiveUpdate() 
	{		
		logger.info("perceive");
		
		try 
		{
			Set<Percept> allPercepts = new HashSet<>();

			for (Entry<String, String> entry : connections.entrySet())
			{
				Collection<Percept> percepts = ei.getAllPercepts(entry.getKey()).get(entry.getValue());
				
				AgentArtifact.perceiveUpdate(entry.getKey(), percepts);
				
				allPercepts.addAll(percepts);
			}
			
//			allPercepts.stream().filter(percept -> JobArtifact.PERCEPTS.contains(percept.getName()))
//								.forEach(percept -> logger.info(percept.toString()));
			
			FacilityArtifact	.perceiveUpdate(allPercepts);
			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
			JobArtifact			.perceiveUpdate(allPercepts);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}	
}
