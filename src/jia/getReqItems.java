package jia;

import java.util.Map;

import env.Translator;
import info.ItemArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import massim.scenario.city.data.Item;
import util.ASUtil;
import util.DataUtil;

public class getReqItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		String 				item 	= (String) Translator.termToObject(terms[0]);		
		Map<Item, Integer> 	req 	= ItemArtifact.getItem(item).getRequiredItems();
		
		return un.unifies(terms[1], ASUtil.mapToTerm(DataUtil.itemToStringMap(req)));
	}

}
