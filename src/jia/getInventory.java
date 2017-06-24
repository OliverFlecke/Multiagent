package jia;

import java.util.Map;

import env.Translator;
import info.AgentArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import util.ASUtil;

public class getInventory extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
		
		String agentName = (String) Translator.termToObject(terms[0]);

		Map<String, Integer> inventory = AgentArtifact.getAgentInventory(agentName);
		
		return un.unifies(terms[1], ASUtil.mapToTerm(inventory));
	}

}
