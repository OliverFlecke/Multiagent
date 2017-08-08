package jia;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import mapc2017.env.info.AgentInfo;

public class hasAmount extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		String 					agent		= ASLParser.parseString	(args[0]);
		String 					item 		= ASLParser.parseString	(args[1]);		
		Map<String, Integer> 	inventory 	= AgentInfo.get(agent).getInventory();
		
		Integer hasAmount = inventory.get(item);

		return un.unifies(args[2], ASSyntax.createNumber(hasAmount == null ? 0 : hasAmount.intValue()));
	}

}
