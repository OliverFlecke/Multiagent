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
import util.ASUtil;
import util.DataUtil;

public class hasBaseItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					agent		= (String) 			 args[0];
		Map<String, Integer> 	items 		= ASUtil.objectToStringMap(args[1]);
		
		if (items.isEmpty()) return true;
		
		Map<String, Integer> 	inventory 	= AgentArtifact.getAgentInventory(agent);
		
		if (inventory.isEmpty()) return false;
		
		Map<Item, Integer> 		baseItems	= ItemArtifact.getBaseItems(DataUtil.stringToItemMap(items));	
		
		for (Entry<Item, Integer> item : baseItems.entrySet())
		{
			Integer hasAmount = inventory.get(item.getKey().getName());
			
			if (hasAmount == null) return false;
			
			if (hasAmount < item.getValue()) return false;
		}

		return true;
	}

}
