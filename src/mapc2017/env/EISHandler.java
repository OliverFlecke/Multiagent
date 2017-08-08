package mapc2017.env;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import mapc2017.env.info.DynamicInfo;
import mapc2017.env.perceive.AgentPerceiver;
import mapc2017.env.perceive.IILParser;
import mapc2017.env.perceive.ReqActionPerceiver;
import mapc2017.env.perceive.SimStartPerceiver;
import massim.eismassim.EnvironmentInterface;

public class EISHandler extends Artifact implements AgentListener {

    private static final Logger logger = Logger.getLogger(EISHandler.class.getName());
	
	// Team configs
    private static final String TEAM_A = "conf/eismassimconfig.json";
//	private static final String TEAM_B = "conf/eismassimconfig_team_B.json";
    
    private Map<String, String> agentsToEntities 	= new HashMap<>();    
    private Set<String> 		hasPerformedAction	= new HashSet<>();
	
	// Config in use
    private String configFile = TEAM_A;

    private EnvironmentInterfaceStandard ei;

    void init() 
    {		
		try 
		{
			ei = new EnvironmentInterface(configFile);
			
			ei.start();
			
			registerAgents();
						
			execInternalOp("makeArtifacts");
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in init: " + e.getMessage(), e);
		}
    }
    
    private void registerAgents()
    {
    	try
    	{
    		for (String entity : ei.getEntities())
    		{
    			String agent = getAgent(entity);
    			
        		ei.registerAgent(agent);
        		
        		ei.associateEntity(agent, entity);
        		
        		agentsToEntities.put(agent, entity);
    		}
    		ei.attachAgentListener("agent1", this);
    	}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in register: " + e.getMessage(), e);
		}
    }
    
    @INTERNAL_OPERATION
    private void makeArtifacts()
    {
    	try 
    	{			
			makeArtifact("SimStartPerceiver", 	"mapc2017.env.perceive.SimStartPerceiver", ArtifactConfig.DEFAULT_CONFIG);
			makeArtifact("ReqActionPerceiver", 	"mapc2017.env.perceive.ReqActionPerceiver", ArtifactConfig.DEFAULT_CONFIG);
	    	
			for (String agent : agentsToEntities.keySet())
	    	{
				makeArtifact(agent, "mapc2017.env.perceive.AgentPerceiver", ArtifactConfig.DEFAULT_CONFIG);
	    	}
    	}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in makeArtifacts: " + e.getMessage(), e);
		}
    }
	
	@INTERNAL_OPERATION
	void perceive(boolean initial)
	{
		Set<Percept> allPercepts = new HashSet<>();

		for (String agent : agentsToEntities.keySet())
		{
			Collection<Percept> percepts = getPercepts(agent);
			
			AgentPerceiver.perceive(agent, percepts);

			allPercepts.addAll(percepts);
		}
		
		hasPerformedAction.clear();
		
		if (initial) SimStartPerceiver.perceive(allPercepts);
		
		ReqActionPerceiver.perceive(allPercepts);
	}
    
    @OPERATION
    void performAction(String action)
    {    	
    	String agent = getOpUserName();
    	
		if (hasPerformedAction.contains(agent))
		{
			System.out.println(String.format("[%s] Has performed action: %s", agent, action));
			return;
		}		
		else if (DynamicInfo.isDeadlinePassed())
		{
			System.out.println(String.format("[%s] Too slow: %s", agent, action));
			return;
		}
		
		try 
		{	    	
			ei.performAction(agent, getAction(action));
			
			hasPerformedAction.add(agent);
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in action: " + e.getMessage(), e);
		}
    }
	
	private Collection<Percept> getPercepts(String agent)
	{
		try
		{
			return ei.getAllPercepts(agent).get(getEntity(agent));
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in getPercepts: " + e.getMessage(), e);
			
			return Collections.emptySet();
		}
	}
	
	private Action getAction(String literal)
	{
		Action action = IILTranslator.literalToAction(literal);
		
		if (action.getName().equals("assist_assemble"))
		{
			String arg = IILParser.parseString(action.getParameters().getFirst());
			
			Identifier agentToAssist = new Identifier(getUserName(arg));
			
			action.setParameters(new LinkedList<Parameter>(Arrays.asList(agentToAssist)));
		}
		
		return action;
	}
	
	private String getAgent(String entity)
	{
		return "agent" + entity.substring(11);
	}
	
	private String getEntity(String agent)
	{
		return agentsToEntities.get(agent);
	}
	
	private String getUserName(String agent)
	{
		return "agent" + getEntity(agent).substring(10);
	}

	@Override
	public void handlePercept(String arg0, Percept arg1)
	{
		if (arg1.getName().equals("step"))
		{
			execInternalOp("perceive", IILParser.parseInt(arg1) == 0);
		}
	}
}
