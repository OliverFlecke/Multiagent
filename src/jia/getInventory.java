package jia;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import mapc2017.env.info.AgentInfo;
import util.ASUtil;

public class getInventory extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		
		String agentName = ASLParser.parseString(args[0]);

		Map<String, Integer> inventory = AgentInfo.get(agentName).getInventory();
		
		return un.unifies(args[1], ASUtil.mapToTerm(inventory));
	}

}
