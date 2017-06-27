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
import util.CartagoUtil;

public class delegateItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					shop		= (String) args[0];
		Map<String, Integer> 	items 		= ASUtil.objectToStringMap(args[1]);
		String 					workshop 	= (String) args[2];
		String 					me	 		= (String) args[3];

		Bid bid = TaskArtifact.announceWithResult("retrieveRequest", shop, items, workshop, me);
		
		if (bid == null) return false;
		
		String					agent		= bid.getAgent();
		Object[] 				data		= bid.getData();
		Map<String, Integer> 	carry 		= CartagoUtil.objectToStringMap((Object[]) data[0]);
		Map<String, Integer> 	rest 		= CartagoUtil.objectToStringMap((Object[]) data[1]);
		
		System.out.println(String.format("[%s] Helping %s", agent, me));
		
		return un.unifies(terms[4], ASSyntax.createAtom(agent)) 
			&& un.unifies(terms[5], ASUtil.mapToTerm(carry)) 
			&& un.unifies(terms[6], ASUtil.mapToTerm(rest));
	}

}
