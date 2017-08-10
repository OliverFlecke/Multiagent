package jia.agent;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.info.AgentInfo;

public class hasAmount extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		int i = 0;
		
		String 	agent		= ASLParser.parseString	(args[i++]);
		String 	item 		= ASLParser.parseString	(args[i++]);
		Integer amountObj	= AgentInfo.get(agent).getInventory().get(item);
		int		amount		= amountObj == null ? 0 : amountObj.intValue();

		return un.unifies(args[i], ASSyntax.createNumber(amount));
	}

}
