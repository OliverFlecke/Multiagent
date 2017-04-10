package env;
// Environment code for project multiagent_jason


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.batik.ext.awt.image.rendered.TranslateRed;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;
import eis.iilang.Percept;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    private EnvironmentInterfaceStandard ei;
    
    private boolean shouldInitShops = true;
    
    /**
     * Instantiates and starts the environment interface.
     */
    void init() 
    {
		logger.info("init");
		
		try {
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
		logger.info("register " + entity);
		
		try {
			String agent = getOpUserName();
			ei.registerAgent(agent);
			ei.associateEntity(agent, entity);
			
			if (shouldInitShops)
			{
				Collection<Percept> initialPercepts = ei.getAllPercepts(agent).get(entity);
				
				// Important to perceive items before facilities
				ItemArtifact    .perceiveInitial(initialPercepts);
				FacilityArtifact.perceiveInitial(initialPercepts);
				
				shouldInitShops = false;
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
		String agent = getOpUserName();
		
		logger.info(agent + " doing: " + action);
		
		try {	
			Action ac = Translator.stringToAction(action);
			
			ei.performAction(agent, ac);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@INTERNAL_OPERATION
	public void getPercepts(String entity) 
	{
		String agentName = getOpUserName();	
		try 
		{
			Collection<Percept> percepts = ei.getAllPercepts(agentName).get(entity);
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
