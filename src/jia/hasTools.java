package jia;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import mapc2017.env.info.AgentInfo;

public class hasTools extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		String 		agent		= ASLParser.parseString	(args[0]);
		String[] 	tools 		= ASLParser.parseArray	(args[1]);
		
		if (tools.length == 0) return true;
		
		Map<String, Integer> 	inventory 	= AgentInfo.get(agent).getInventory();

		if (inventory.isEmpty()) return false;
		
		for (Object tool : tools)
		{
			if (!inventory.containsKey(tool)) return false;
		}
		
		return true;
	}

}
