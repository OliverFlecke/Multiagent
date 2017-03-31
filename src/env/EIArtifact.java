package env;
// Environment code for project multiagent_jason

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.batik.ext.awt.image.rendered.TranslateRed;

import c4jason.CartagoEnvironment;
import cartago.AgentId;
import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import eis.AgentListener;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import jade.tools.logging.ontology.GetAllLoggers;
import jason.asSyntax.Literal;

public class EIArtifact extends Artifact {

    private static final Logger logger = Logger.getLogger(EIArtifact.class.getName());
    
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
			System.out.println(agent + " doing: " + action);
			signal("inFacility");
			Action ac = Translator.stringToAction(action);
			ei.performAction(agent, ac);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@INTERNAL_OPERATION
	public void getPercepts(String connection) 
	{
		String agentName = getOpUserName();	
		try 
		{
			Collection<Percept> percepts = ei.getAllPercepts(agentName).get(connection);
			for (Percept percept : percepts)
			{		
				Object[] parameters = Translator.parametersToArguments(percept.getParameters());
				signal(percept.getName(), parameters);
			}
		} 
		catch (PerceiveException e) 
		{
			e.printStackTrace();
		} 
		catch (NoEnvironmentException e) 
		{
			e.printStackTrace();
		}
	}	
}
