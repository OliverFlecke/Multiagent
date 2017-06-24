package jia;

import java.util.Map;
import java.util.Map.Entry;

import env.Translator;
import info.AgentArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import util.ASUtil;

public class hasItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					agent		= (String) 			 args[0];
		Map<String, Integer> 	items 		= ASUtil.objectToMap(args[1]);
		
		if (items.isEmpty()) return true;
		
		Map<String, Integer> 	inventory 	= AgentArtifact.getAgentInventory(agent);
		
		if (inventory.isEmpty()) return false;
		
		for (Entry<String, Integer> item : items.entrySet())
		{
			Integer hasAmount = inventory.get(item.getKey());
			
			if (hasAmount == null) return false;
			
			if (hasAmount < item.getValue()) return false;
		}

		return true;
	}

}
