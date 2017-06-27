package jia;

import java.util.Map;

import env.Translator;
import info.AgentArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;

public class hasAmount extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					agent		= (String) args[0];
		String 					item 		= (String) args[1];		
		Map<String, Integer> 	inventory 	= AgentArtifact.getAgentInventory(agent);
		
		Integer hasAmount = inventory.get(item);

		return un.unifies(terms[2], ASSyntax.createNumber(hasAmount == null ? 0 : hasAmount.intValue()));
	}

}
