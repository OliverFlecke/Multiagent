package jia;

import java.util.Map;

import cnp.Bid;
import cnp.TaskArtifact;
import env.Translator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import util.ASUtil;

public class delegateJob extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					taskId		= (String) 			 args[0];
		Map<String, Integer> 	items 		= ASUtil.objectToStringMap(args[1]);
		String 					facility 	= (String) 			 args[2];

		Bid bid = TaskArtifact.announceWithResult("assembleRequest", taskId, items, facility);
		
		if (bid == null) return false;
		
		Map<String, Integer> 	rest 		= ASUtil.objectToStringMap(bid.getData()[0]);
		
		return un.unifies(terms[3], ASUtil.mapToTerm(rest));
	}
	
}
