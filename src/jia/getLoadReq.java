package jia;

import java.util.Map;

import env.Translator;
import info.ItemArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import massim.scenario.city.data.Item;

public class getLoadReq extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{		
		Object 				 obj	= Translator.termToObject(terms[0]);
		Map<String, Integer> map 	= (Map<String, Integer>) obj;		
		Map<Item  , Integer> items 	= ItemArtifact.getItemMap(map);	
		
		int loadReq = items.keySet().stream()
				.map(Item::getRequiredBaseItems)
				.map(ItemArtifact::getVolume)
				.max(Integer::max).get();
		
		return un.unifies(terms[1], ASSyntax.createNumber(loadReq));
	}

}
