package jia.req;

import java.util.Map;

import cnp.Bid;
import cnp.TaskArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.OpArtifact;

public class delegateJob extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		String 					taskId		= ASLParser.parseString	(args[0]);
		Map<String, Integer> 	items 		= ASLParser.parseMap	(args[1]);
		String 					facility 	= ASLParser.parseString	(args[2]);

		Bid bid = TaskArtifact.announceWithResult("assembleRequest", taskId, items, facility);
		
		if (bid == null) return false;
		
		Object[] 				data		= bid.getData();		
		Map<String, Integer> 	rest 		= OpArtifact.objectToStringMap((Object[]) data[0]);
		
		return un.unifies(args[3], ASLParser.createMap(rest));
	}
	
}
