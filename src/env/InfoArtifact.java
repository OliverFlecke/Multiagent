package env;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import jason.asSyntax.Term;
import massim.scenario.city.data.Entity;
import massim.scenario.city.data.Location;

public class InfoArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(InfoArtifact.class.getName());

	private static final String ENTITY 			= "entity";
	private static final String ID 				= "id";
	private static final String MAP 			= "map";
	private static final String MONEY 			= "money";
	private static final String SEED_CAPITAL 	= "seedCapital";
	private static final String STEP 			= "step";
	private static final String STEPS 			= "steps";
	private static final String TEAM 			= "team";
	
	public static final String[] PERCEPTS = new String[] {
			ENTITY, ID, MAP, MONEY, SEED_CAPITAL, STEP, STEPS, TEAM
	};
	
	private static Map<String, Entity> 	entities = new HashMap<>();
	private static String 				id;
	private static String 				map;
	private static int					money;
	private static int					seedCapital;
	private static int					step;
	private static int					steps;
	private static String				team;
	
	/* Example operations */
	
	@OPERATION
	void getPos(OpFeedbackParam<Double> lon, OpFeedbackParam<Double> lat)
	{
		Location l = entities.get(getOpUserName()).getLocation();
		
		lon.set(l.getLon());
		lat.set(l.getLat());
	}
	
	@OPERATION
	void getMoney(OpFeedbackParam<Integer> money)
	{
		money.set(InfoArtifact.money);
	}
	
	@OPERATION
	void getRemainingSteps(OpFeedbackParam<Integer> remainingSteps)
	{
		remainingSteps.set(InfoArtifact.steps - InfoArtifact.step);
	}
	
	@OPERATION
	void getSimulationData(OpFeedbackParam<String> id, OpFeedbackParam<String> map,
			OpFeedbackParam<Integer> seedCapital, OpFeedbackParam<String> team)
	{
		id			.set(InfoArtifact.id);
		map			.set(InfoArtifact.map);
		seedCapital	.set(InfoArtifact.seedCapital);
		team		.set(InfoArtifact.team);
	}

	protected static void perceiveInitial(Collection<Percept> percepts)
	{
		logger.info("Perceiving facilities");
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case ENTITY: 		perceiveEntity		(percept);	break;
			case ID:			perceiveId			(percept);  break;
			case MAP:			perceiveMap			(percept);  break;
			case MONEY:			perceiveMoney		(percept);  break;
			case SEED_CAPITAL:	perceiveSeedCapital	(percept);  break;
			case STEP:			perceiveStep		(percept);  break;
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
//		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
//
//		String name = Translator.termToString(args[0]);
//		String team = Translator.termToString(args[1]);
//		double lon 	= Translator.termToDouble(args[2]);
//		double lat 	= Translator.termToDouble(args[3]);
//		String role = Translator.termToString(args[4]);
//		
//		// Entity has not been made public
//		entities.put(name, new Entity())
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

	// Literal(int)
	private static void perceiveMoney(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		money = Translator.termToInteger(args[0]);
	}

	// Literal(int)
	private static void perceiveSeedCapital(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		seedCapital = Translator.termToInteger(args[0]);
	}

	// Literal(int)
	private static void perceiveStep(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		step = Translator.termToInteger(args[0]);
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
