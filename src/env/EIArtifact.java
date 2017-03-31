package env;
// Environment code for project multiagent_jason

import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    private EnvironmentInterfaceStandard ei;
    
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
//			ei.attachAgentListener(agent, this);
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
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}
}
