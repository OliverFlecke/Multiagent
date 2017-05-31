package jia;

import env.Translator;
import info.ItemArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;

public class bid extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
		
		Object[] args = Translator.termsToObject(terms);
		
		int speed 	= (int) args[0];
//		int charge 	= (int) args[1];
//		int load 	= (int) args[2];
		int maxLoad = (int) args[3];
		
		Object[] 	itemTuple 	= (Object[]) 	args[4];		
		String 		item 		= (String) 		itemTuple[0];
		int			quantity	= (int) 		itemTuple[1];
		
		int 		volume 		= ItemArtifact.getVolume(ItemArtifact.getBaseItems(item)) * quantity;
		
		if (volume > maxLoad) return false;
		
		return un.unifies(terms[5], ASSyntax.createNumber(100 - speed));
	}

}
