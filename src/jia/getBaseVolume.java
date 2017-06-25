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
import util.ASUtil;

public class getBaseVolume extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{	
		Object[] args = Translator.termsToObject(terms);
		
		Map<Item, Integer> items = ASUtil.objectToItemMap(args[0]);
		
		int volume = ItemArtifact.getBaseVolume(items);
		
		return un.unifies(terms[1], ASSyntax.createNumber(volume));
	}
}
