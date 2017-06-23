package jia;

import java.util.Map;

import cnp.Bid;
import cnp.TaskArtifact;
import env.Translator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;

public class delegateJob extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					taskId		= (String) 					args[0];
		Map<String, Integer> 	items 		= (Map<String, Integer>) 	args[1];
		String 					facility 	= (String) 					args[2];

		Bid bid = TaskArtifact.announceWithResult("assembleRequest",taskId, items, facility);
		
		if (bid == null) return false;
		
		Map<String, Integer> 	rest 		= (Map<String, Integer>) 	bid.getData();

		ListTerm list = ASSyntax.createList();
		
		rest.entrySet().forEach(e -> list.add(ASSyntax.createLiteral("map", 
													ASSyntax.createAtom(e.getKey()),
													ASSyntax.createNumber(e.getValue()))));
		
		return un.unifies(terms[3], list);
	}
	
}
