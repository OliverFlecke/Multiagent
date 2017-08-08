package jia;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import mapc2017.env.info.ItemInfo;

public class getBaseVolume extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{			
		Map<String, Integer> items = ASLParser.parseMap(args[0]);
		
		int volume = ItemInfo.get().getBaseVolume(items);
		
		return un.unifies(args[1], ASSyntax.createNumber(volume));
	}
}
