package env;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import data.CEntity;
import eis.iilang.Percept;
import jason.asSyntax.Term;
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
	
	public static final Set<String>	STATIC_PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ENTITY, ID, MAP, ROLE, SEED_CAPITAL, STEPS, TEAM)));
	
	private static String 				id;
	private static String 				map;
	private static Map<String, Role>	roles = new HashMap<>();
	private static int					seedCapital;
	private static int					steps;
	private static String				team;
	
	@OPERATION
	void getSimulationData(OpFeedbackParam<String> id, OpFeedbackParam<String> map,
			OpFeedbackParam<Integer> seedCapital, OpFeedbackParam<String> team)
	{
		id			.set(StaticInfoArtifact.id);
		map			.set(StaticInfoArtifact.map);
		seedCapital	.set(StaticInfoArtifact.seedCapital);
		team		.set(StaticInfoArtifact.team);
	}

	protected static void perceiveInitial(Collection<Percept> percepts)
	{
		logger.info("Perceiving facilities");
		
		// Important to perceive roles first
		percepts.stream().filter(percept -> percept.getName() == ROLE)
						 .forEach(role -> perceiveRole(role));
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case ENTITY: 		perceiveEntity		(percept);	break;
			case ID:			perceiveId			(percept);  break;
			case MAP:			perceiveMap			(percept);  break;
			case SEED_CAPITAL:	perceiveSeedCapital	(percept);  break;
			case STEPS:			perceiveSteps		(percept);  break;
			case TEAM:			perceiveTeam		(percept);  break;
			}
		}
	}
	
	protected static void perceiveUpdate(Collection<Percept> percepts)
	{
		// Perceive and update dynamic data such as money and step
		// Dynamic data should probably be defined as observable properties
	}
	
	// Literal(String, String, double, double, String)
	private static void perceiveEntity(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();

		String name = Translator.termToString(args[0]);
		String team = Translator.termToString(args[1]);
		double lon 	= Translator.termToDouble(args[2]);
		double lat 	= Translator.termToDouble(args[3]);
		String role = Translator.termToString(args[4]);
		
		// Entity has not been made public
		if (team.equals(StaticInfoArtifact.team))
		{
			DynamicInfoArtifact.addEntity(name, new CEntity(roles.get(role), new Location(lon, lat)));
		}
	}
	
	// Literal(String)
	private static void perceiveId(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		id = Translator.termToString(args[0]);
	}

	// Literal(String)
	private static void perceiveMap(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		map = Translator.termToString(args[0]);
	}

	// Literal(String,)
	private static void perceiveRole(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();

		String 		name 	= Translator.termToString(args[0]);
		int 		speed 	= Translator.termToInteger(args[1]);
		int 		load 	= Translator.termToInteger(args[2]);
		int 		battery = Translator.termToInteger(args[3]);
		List<Term> 	tools 	= Translator.termToTermList(args[4]);
		
		Set<String> permissions = tools.stream().map(tool -> Translator.termToString(tool))
									   .collect(Collectors.toSet());
		
		roles.put(name, new Role(name, speed, battery, load, permissions));
	}

	// Literal(int)
	private static void perceiveSeedCapital(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		seedCapital = Translator.termToInteger(args[0]);
	}

	// Literal(int)
	private static void perceiveSteps(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		steps = Translator.termToInteger(args[0]);
	}

	// Literal(String)
	private static void perceiveTeam(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		team = Translator.termToString(args[0]);
	}	
}
