package jia.facility;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.StaticInfo;

public class getDurationToFacility extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		int i = 0;
		
		String agent 	= ASLParser.parseString(args[i++]);
		String facility = ASLParser.parseString(args[i++]);		
		int    duration = StaticInfo.get().getRouteDuration(AgentInfo.get(agent), 
				FacilityInfo.get().getFacility(facility).getLocation());
		
		return un.unifies(args[i], ASSyntax.createNumber(duration));
	}
}
