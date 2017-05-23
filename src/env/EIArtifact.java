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
import cartago.ArtifactId;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OperationException;
import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.Percept;
import massim.eismassim.EnvironmentInterface;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    public static final boolean LOGGING_ENABLED = false;
    
    private EnvironmentInterfaceStandard ei;
    
    private Map<String, String> connections = new HashMap<>();
    
    private ArtifactId agentArtifactId, dynamicInfoArtifactId, facilityArtifactId, itemArtifactId, jobArtifactId, staticInfoArtifactId;

    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {
    	logger.setLevel(Level.FINE);
		
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
		String agName = getOpUserName();
		
		logger.fine("register " + agName + " on " + entity);
		
		try 
		{			
			ei.registerAgent(agName);
			
			ei.associateEntity(agName, entity);
			
			connections.put(agName, entity);
			
			if (connections.size() == ei.getEntities().size())
			{
				agentArtifactId = lookupArtifact("AgentArtifact");
				dynamicInfoArtifactId = lookupArtifact("DynamicInfoArtifact");
				facilityArtifactId = lookupArtifact("FacilityArtifact");
				itemArtifactId = lookupArtifact("ItemArtifact");
				jobArtifactId = lookupArtifact("JobArtifact");
				staticInfoArtifactId = lookupArtifact("StaticInfoArtifact");
				
				// Perceive initial perceptions when all agents have connected
				execInternalOp("perceiveInitial");
				
				// Attach listener for perceiving the following steps
				ei.attachAgentListener(agName, new AgentListener() 
				{				
					@Override
					public void handlePercept(String agent, Percept percept) {
						if (percept.getName().equals("step")) {
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
	void perceiveInitial() throws PerceiveException, NoEnvironmentException, OperationException
	{
		logger.finest("perceiveInitial");
		
		Set<Percept> allPercepts = new HashSet<>();
		
		Map<String, Collection<Percept>> agentPercepts = new HashMap<>();

		for (Entry<String, String> entry : connections.entrySet())
		{
			Collection<Percept> percepts = ei.getAllPercepts(entry.getKey()).get(entry.getValue());
			
			agentPercepts.put(entry.getKey(), percepts);
			
			allPercepts.addAll(percepts);
		}
		
		// Important to perceive items before facilities
//		ItemArtifact        .perceiveInitial(allPercepts);
////		FacilityArtifact    .perceiveInitial(allPercepts);
//		StaticInfoArtifact  .perceiveInitial(allPercepts);
//		
//		FacilityArtifact	.perceiveUpdate(allPercepts);
//		DynamicInfoArtifact	.perceiveUpdate(allPercepts);
//		JobArtifact			.perceiveUpdate(allPercepts);
		
		execLinkedOp(itemArtifactId, "perceiveInitial", allPercepts);
		execLinkedOp(staticInfoArtifactId, "perceiveInitial", allPercepts);

		execLinkedOp(facilityArtifactId, "perceiveUpdate", allPercepts);
		execLinkedOp(dynamicInfoArtifactId, "perceiveUpdate", allPercepts);
		execLinkedOp(jobArtifactId, "perceiveUpdate", allPercepts);

		// Important to perceive agent perceptions after static info
		for (Entry<String, Collection<Percept>> entry : agentPercepts.entrySet())
		{					
//			AgentArtifact.perceiveUpdate(entry.getKey(), entry.getValue());
			execLinkedOp(agentArtifactId, "perceiveUpdate", entry.getKey(), entry.getValue());
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
				
//				AgentArtifact.perceiveUpdate(entry.getKey(), percepts);
				execLinkedOp(agentArtifactId, "perceiveUpdate", entry.getKey(), percepts);
				
//				if (entry.getKey().equals("agentA1")) logger.info(percepts.toString());
				
				allPercepts.addAll(percepts);
			}
			
//			FacilityArtifact	.perceiveUpdate(allPercepts);
//			DynamicInfoArtifact	.perceiveUpdate(allPercepts);
//			JobArtifact			.perceiveUpdate(allPercepts);
			
			execLinkedOp(facilityArtifactId, "perceiveUpdate", allPercepts);
			execLinkedOp(dynamicInfoArtifactId, "perceiveUpdate", allPercepts);
			execLinkedOp(jobArtifactId, "perceiveUpdate", allPercepts);
			
//			FacilityArtifact.logShops();
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in perceive: " + e.getMessage(), e);
		}
	}	
}
