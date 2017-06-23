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

// Shop, Items, Workshop, Agent, Rest
public class delegateItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					shop		= (String) 					args[0];
		Map<String, Integer> 	items 		= (Map<String, Integer>) 	args[1];
		String 					workshop 	= (String) 					args[2];
		String 					me	 		= (String) 					args[3];

		Bid bid = TaskArtifact.announceWithResult("retrieveRequest", shop, items, workshop, me);
		
		if (bid == null) return false;
		
		String					agent		= 							bid.getAgent();
		Map<String, Integer> 	rest 		= (Map<String, Integer>) 	bid.getData();

		ListTerm list = ASSyntax.createList();
		
		rest.entrySet().forEach(e -> list.add(ASSyntax.createLiteral("map", 
													ASSyntax.createAtom(e.getKey()),
													ASSyntax.createNumber(e.getValue()))));
		
		return un.unifies(terms[4], ASSyntax.createAtom(agent)) && 
			   un.unifies(terms[5], list);
	}

}
