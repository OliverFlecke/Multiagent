package info;

import java.util.*;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import env.Translator;
import jason.asSyntax.Term;

public class DynamicInfoArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(DynamicInfoArtifact.class.getName());

	private static final String DEADLINE			= "deadline";
	private static final String MONEY 				= "money";
	private static final String STEP 				= "step";
	private static final String TIMESTAMP 			= "timestamp";
	
	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(DEADLINE, MONEY, STEP, TIMESTAMP)));

	private static long					deadline;
	private static int					money;
	private static int					step;
	private static long					timestamp;

	@OPERATION
	void getDeadline(OpFeedbackParam<Long> ret)
	{
		ret.set(deadline);
	}
	
	@OPERATION
	void getMoney(OpFeedbackParam<Integer> ret)
	{
		ret.set(money);
	}
	
	@OPERATION
	void getStep(OpFeedbackParam<Integer> ret)
	{
		ret.set(step);
	}
	
	@OPERATION
	void getTimestamp(OpFeedbackParam<Long> ret)
	{
		ret.set(timestamp);
	}
	
	public static void perceiveUpdate(Collection<Percept> percepts)
	{
		logger.info("Perceiving dynamic info");
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case DEADLINE:   perceiveDeadline	(percept);	break;
			case MONEY:      perceiveMoney		(percept);  break;
			case STEP:       perceiveStep		(percept);  break;
			case TIMESTAMP:  perceiveTimestamp	(percept);  break;
			}
		}
		
//		logger.info("Perceived deadline:\t" + deadline);
//		logger.info("Perceived money:\t" + money);
//		logger.info("Perceived step:\t" + step);
//		logger.info("Perceived timestamp:\t" + timestamp);
	}
	
	// Literal(long)
	private static void perceiveDeadline(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		deadline = Translator.termToLong(args[0]);
	}

	// Literal(int)
	private static void perceiveMoney(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		money = Translator.termToInteger(args[0]);
	}

	// Literal(int)
	private static void perceiveStep(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		step = Translator.termToInteger(args[0]);
	}

	// Literal(long)
	private static void perceiveTimestamp(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		timestamp = Translator.termToLong(args[0]);
	}
}
