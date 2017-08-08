package jia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.StaticInfo;

public class getRouteLength extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		String agent 	= ASLParser.parseString(args[0]);
		String facility = ASLParser.parseString(args[1]);
		
		AgentInfo 		aInfo = AgentInfo.get(agent);
		FacilityInfo 	fInfo = FacilityInfo.get();
		
		int routeLength = StaticInfo.get().getRouteLength(
				aInfo.getLocation(), 
				fInfo.getFacility(facility).getLocation(), 
				aInfo.getPermission());
		
		return un.unifies(args[2], ASSyntax.createNumber(routeLength));
	}
}
