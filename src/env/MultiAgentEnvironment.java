package env;
// Environment code for project multiagent_jason

import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import eis.AgentListener;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Percept;

public class MultiAgentEnvironment extends Artifact implements AgentListener {

    private static final Logger logger = Logger.getLogger(MultiAgentEnvironment.class.getName());
    
    private EnvironmentInterfaceStandard ei;
    
    void init() {		
		try {
			ei = EILoader.fromClassName("massim.eismassim.EnvironmentInterface");
			ei.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@OPERATION
	void register(String entity)  {
		try {
			String agent = getOpUserName();
			ei.registerAgent(agent);
			ei.associateEntity(agent, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@OPERATION
	void action(String action) {
		try {
			String agent = getOpUserName();
			logger.info(agent + " doing: " + action);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void handlePercept(String agentName, Percept percept) {
		logger.info(agentName + " percieved: " + percept.getName());
	}
}
