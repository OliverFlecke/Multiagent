package jia;

import java.util.Map;

import env.Translator;
import info.AgentArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class hasTools extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 		agent		= (String) 		args[0];
		Object[] 	tools 		= (Object[]) 	args[1];
		
		if (tools.length == 0) return true;
		
		Map<String, Integer> 	inventory 	= AgentArtifact.getAgentInventory(agent);

		if (inventory.isEmpty()) return false;
		
		for (Object tool : tools)
		{
			if (!inventory.containsKey(tool)) return false;
		}
		
		return true;
	}

}
