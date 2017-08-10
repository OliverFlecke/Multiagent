package jia.req;

import java.util.Map;

import cnp.Bid;
import cnp.TaskArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.OpArtifact;

public class delegateItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		String 					shop		= ASLParser.parseString	(args[0]);
		Map<String, Integer> 	items 		= ASLParser.parseMap	(args[1]);
		String 					workshop 	= ASLParser.parseString	(args[2]);
		String 					me	 		= ASLParser.parseString	(args[3]);

		Bid bid = TaskArtifact.announceWithResult("retrieveRequest", shop, items, workshop, me);
		
		if (bid == null) return false;
		
		String					agent		= bid.getAgent();
		Object[] 				data		= bid.getData();
		Map<String, Integer> 	carry 		= OpArtifact.objectToStringMap((Object[]) data[0]);
		Map<String, Integer> 	rest 		= OpArtifact.objectToStringMap((Object[]) data[1]);
		
		System.out.println(String.format("[%s] Helping %s", agent, me));
		
		return un.unifies(args[4], ASSyntax.createAtom(agent)) 
			&& un.unifies(args[5], ASLParser.createMap(carry)) 
			&& un.unifies(args[6], ASLParser.createMap(rest));
	}

}
