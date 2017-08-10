package jia.req;

import cnp.Bid;
import cnp.TaskArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.OpArtifact;

public class delegateTools extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		String[] 	tools 		= ASLParser.parseArray	(args[0]);
		String 		workshop 	= ASLParser.parseString	(args[1]);
		String 		me	 		= ASLParser.parseString	(args[2]);

		Bid bid = TaskArtifact.announceWithResult("toolRequest", tools, workshop, me);
		
		if (bid == null) return false;
		
		String		agent	= bid.getAgent();
		Object[] 	data	= bid.getData();
		String[] 	carry 	= OpArtifact.objectToStringArray((Object[]) data[0]);
		String[]	rest 	= OpArtifact.objectToStringArray((Object[]) data[1]);
		
		System.out.println(String.format("[%s] Helping %s", agent, me));
		
		return un.unifies(args[3], ASSyntax.createAtom(agent)) 
			&& un.unifies(args[4], ASLParser.createArray(carry)) 
			&& un.unifies(args[5], ASLParser.createArray(rest));
	}

}
