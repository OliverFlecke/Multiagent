package jia;

import java.util.Map;
import java.util.Map.Entry;

import env.Translator;
import info.AgentArtifact;
import info.ItemArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import massim.scenario.city.data.Item;

public class hasBaseItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		// If the map is empty, it is translated to a list
		if (!(args[1] instanceof Map<?, ?>)) return true;
		
		String 					agent		= (String) 					args[0];
		Map<String, Integer> 	items 		= (Map<String, Integer>) 	args[1];
		Map<String, Integer> 	inventory 	= AgentArtifact.getAgentInventory(agent);
		
		if (inventory.isEmpty()) return false;
		
		Map<Item, Integer> 		baseItems	= ItemArtifact.getBaseItems(ItemArtifact.getItemMap(items));	
		
		for (Entry<Item, Integer> item : baseItems.entrySet())
		{
			Integer hasAmount = inventory.get(item.getKey().getName());
			
			if (hasAmount == null) return false;
			
			if (hasAmount < item.getValue()) return false;
		}

		return true;
	}

}
