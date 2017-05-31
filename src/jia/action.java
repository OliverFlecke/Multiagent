package jia;

import eis.iilang.Action;
import env.EIArtifact;
import env.Translator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;

public class action extends DefaultInternalAction {

	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
		
		String agentName = (String) Translator.termToObject(terms[0]);
		Action action = Translator.literalToAction((LiteralImpl) terms[1]);

		EIArtifact.executeAction(agentName, action);
		
		return true;
	}
}
