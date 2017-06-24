package jia;

import java.util.Map;

import cnp.Bid;
import cnp.TaskArtifact;
import env.Translator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import util.ASUtil;

public class delegateItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					shop		= (String) 			 args[0];
		Map<String, Integer> 	items 		= ASUtil.objectToMap(args[1]);
		String 					workshop 	= (String) 			 args[2];
		String 					me	 		= (String) 			 args[3];

//		Bid bid = TaskArtifact.announceWithResult("retrieveRequest", shop, items, workshop, me);
		Bid bid = TaskArtifact.delegateItems(shop, items, workshop, me);
		
		if (bid == null) return false;
		
		String					agent		= 					 bid.getAgent();
		Map<String, Integer> 	rest 		= ASUtil.objectToMap(bid.getData());
		
		return un.unifies(terms[4], ASSyntax.createAtom(agent)) && 
			   un.unifies(terms[5], ASUtil.mapToTerm(rest));
	}

}
