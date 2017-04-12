package env;
// Environment code for project multiagent_jason


import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;
import eis.iilang.Percept;

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
		
		try {
//			ei = new EnvironmentInterface("conf/eismassimconfig.json");
			ei = EILoader.fromClassName("massim.eismassim.EnvironmentInterface");
			
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
		
		logger.info("register " + agName + " on " + entity);
		
		try 
		{			
			ei.registerAgent(agName);
			ei.associateEntity(agName, entity);
			
			connections.put(agName, entity);
			
			if (!hasPerceivedInitial && connections.size() == ei.getEntities().size())
			{
				Set<Percept> initialPercepts = new HashSet<>();
				
				for (Entry<String, String> entry : connections.entrySet())
				{
					initialPercepts.addAll(ei.getAllPercepts(entry.getKey()).get(entry.getValue()));
				}
				
				// Important to perceive items before facilities
				ItemArtifact      .perceiveInitial(initialPercepts);
				FacilityArtifact  .perceiveInitial(initialPercepts);
				StaticInfoArtifact.perceiveInitial(initialPercepts);
				
				hasPerceivedInitial = true;
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
		
		try {	
			Action ac = Translator.stringToAction(action);
			
			ei.performAction(agName, ac);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@OPERATION
	void getPercepts() 
	{
		String agName = getOpUserName();
		
		try 
		{
			Collection<Percept> percepts = ei.getAllPercepts(agName).get(connections.get(agName));
			// TODO It is WAY to slow to run through all the percepts, as far as I can see
			for (Percept percept : percepts)
			{		
				System.out.println(percept);
				
//				switch (percept.getName())
//				{
//				case "step":
//					Object[] args = Translator.parametersToArguments(percept.getParameters());
//					defineObsProperty(percept.getName(), args);
//					break;
//				case "shop":
//				default:
//					System.out.print(percept.getName() + " ");
//					Object[] args2 = Translator.parametersToArguments(percept.getParameters());
//					for (Object arg : args2)
//						System.out.print(arg + " ");
//					System.out.println();
//					break;
//				}
			}
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}	
}
