package jia;

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

public class delegateTools extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String[] 	tools 		= ASUtil.objectToStrings(args[0]);
		String 		workshop 	= (String) 				 args[1];
		String 		me	 		= (String) 				 args[2];

		Bid bid = TaskArtifact.announceWithResult("toolRequest", tools, workshop, me);
		
		if (bid == null) return false;
		
		String		agent	= bid.getAgent();
		Object[] 	data	= bid.getData();
		String[] 	carry 	= CartagoUtil.objectToStringArray((Object[]) data[0]);
		String[]	rest 	= CartagoUtil.objectToStringArray((Object[]) data[1]);
		
		System.out.println(String.format("[%s] Helping %s", agent, me));
		
		return un.unifies(terms[3], ASSyntax.createAtom(agent)) 
			&& un.unifies(terms[4], ASUtil.stringsToTerm(carry)) 
			&& un.unifies(terms[5], ASUtil.stringsToTerm(rest));
	}

}
