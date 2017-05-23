package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import data.CEntity;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Role;

public class StaticInfoArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(StaticInfoArtifact.class.getName());

	private static final String ENTITY 				= "entity";
	private static final String ID 					= "id";
	private static final String MAP 				= "map";
	private static final String ROLE	 			= "role";
	private static final String SEED_CAPITAL 		= "seedCapital";
	private static final String STEPS 				= "steps";
	private static final String TEAM 				= "team";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ENTITY, ID, MAP, ROLE, SEED_CAPITAL, STEPS, TEAM)));
	
	// Entities are stored in DynamicInfoArtifact
	private static String 				id;
	private static String 				map;
	private static Map<String, Role>	roles = new HashMap<>();
	private static int					seedCapital;
	private static int					steps;
	private static String				team;
	
	@OPERATION
	void getSimulationData(OpFeedbackParam<String> id, OpFeedbackParam<String> map,
			OpFeedbackParam<Integer> seedCapital, OpFeedbackParam<Integer> steps, 
			OpFeedbackParam<String> team)
	{
		id			.set(StaticInfoArtifact.id);
		map			.set(StaticInfoArtifact.map);
		seedCapital	.set(StaticInfoArtifact.seedCapital);
		steps		.set(StaticInfoArtifact.steps);
		team		.set(StaticInfoArtifact.team);
	}

	public static void perceiveInitial(Collection<Percept> percepts)
	{		
		// Roles and team are used when perceiving entities
		percepts.stream().filter(percept -> percept.getName() == ROLE)
						 .forEach(role -> perceiveRole(role));
		
		percepts.stream().filter(percept -> percept.getName() == TEAM)
						 .forEach(team -> perceiveTeam(team));
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case ENTITY: 		perceiveEntity		(percept);	break;
			case ID:			perceiveId			(percept);  break;
			case MAP:			perceiveMap			(percept);  break;
			case SEED_CAPITAL:	perceiveSeedCapital	(percept);  break;
			case STEPS:			perceiveSteps		(percept);  break;
			}
		}

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info("Perceived static info");
			logger.info("Perceived roles: " 		+ roles.keySet());
			logger.info("Perceived team:\t" 		+ team);
			logger.info("Perceived id:\t\t" 		+ id);
			logger.info("Perceived map:\t" 			+ map);
			logger.info("Perceived seedCapital:\t" 	+ seedCapital);
			logger.info("Perceived steps:\t" 		+ steps);
		}		
	}
	
	// Literal(String, String, double, double, String)
	private static void perceiveEntity(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);

		String name = (String) args[0];
		String team = (String) args[1];
		double lon 	= (double) args[2];
		double lat 	= (double) args[3];
		String role = (String) args[4];
		
		// Entity has not been made public
		if (team.equals(StaticInfoArtifact.team))
		{
			AgentArtifact.addEntity(name, new CEntity(roles.get(role), new Location(lon, lat)));
		}
	}
	
	// Literal(String)
	private static void perceiveId(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		id = (String) args[0];
	}

	// Literal(String)
	private static void perceiveMap(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		map = (String) args[0];
	}

	// Literal(String,)
	private static void perceiveRole(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);

		String 		name 	= (String)   args[0];
		int 		speed 	= (int)      args[1];
		int 		load 	= (int)      args[2];
		int 		battery = (int)      args[3];
		Object[] 	tools 	= (Object[]) args[4];
		
		Set<String> permissions = Arrays.stream(tools)
				.map(String.class::cast)
				.collect(Collectors.toSet());
		
		roles.put(name, new Role(name, speed, battery, load, permissions));
	}

	// Literal(int)
	private static void perceiveSeedCapital(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		seedCapital = (int) args[0];
	}

	// Literal(int)
	private static void perceiveSteps(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		steps = (int) args[0];
	}

	// Literal(String)
	private static void perceiveTeam(Percept percept)
	{
		Object[] args = Translator.perceptToObject(percept);
		
		team = (String) args[0];
	}

	/**
	 * @return The roles in this simulation
	 */
	public static Collection<Role> getRoles() 
	{
		return roles.values();
	}	
}
