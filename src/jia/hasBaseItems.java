package jia;

import java.util.Map;
import java.util.Map.Entry;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.ItemInfo;

public class hasBaseItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		
		String 					agent		= ASLParser.parseString	(args[0]);
		Map<String, Integer> 	items 		= ASLParser.parseMap	(args[1]);
		
		if (items.isEmpty()) return true;
		
		Map<String, Integer> 	inventory 	= AgentInfo.get(agent).getInventory();
		
		if (inventory.isEmpty()) return false;
		
		Map<String, Integer> 	baseItems	= ItemInfo.get().getBaseItems(items);	
		
		for (Entry<String, Integer> item : baseItems.entrySet())
		{
			Integer hasAmount = inventory.get(item.getKey());
			
			if (hasAmount == null) return false;
			
			if (hasAmount < item.getValue()) return false;
		}

		return true;
	}

}
