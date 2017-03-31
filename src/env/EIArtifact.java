package env;
// Environment code for project multiagent_jason


import java.util.*;
import java.util.Map.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.batik.ext.awt.image.rendered.TranslateRed;

import c4jason.CartagoEnvironment;
import cartago.AgentId;
import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
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
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
	}	
}
