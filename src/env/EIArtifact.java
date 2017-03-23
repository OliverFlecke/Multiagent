package env;
// Environment code for project multiagent_jason

import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import eis.AgentListener;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;
import eis.iilang.Percept;

public class EIArtifact extends Artifact implements AgentListener {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
    private EnvironmentInterfaceStandard ei;
    
    void init() {
		logger.info("init");		
		try {
			ei = EILoader.fromClassName("massim.eismassim.EnvironmentInterface");
			ei.start();
		} catch (Exception e) {
			e.printStackTrace();
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
			ei.attachAgentListener(agent, this);
		} catch (Exception e) {
			e.printStackTrace();
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


	public void handlePercept(String agentName, Percept percept) {

		if (percept.getName() == "pricedJob")
		{
//			logger.info(agentName + " percieved: " + percept.toString());
		}
	}
}
