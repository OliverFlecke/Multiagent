package jia.facility;

import java.util.Comparator;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.StaticInfo;
import mapc2017.env.parse.ASLParser;

public class getClosestFacility extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		int i = 0;
		
		String from 	= ASLParser.parseFunctor(args[i++]);
		String type 	= ASLParser.parseString	(args[i++]);
		String closest;
		
		if (from.startsWith("agent"))
		{
			closest = FacilityInfo.get().getFacilities(type).stream()
					.min(Comparator.comparingInt(f -> StaticInfo.get().getRouteDuration(
							AgentInfo.get(from), f.getLocation()))).get().getName();			
		}
		else
		{
			closest = FacilityInfo.get().getFacilities(type).stream()
					.min(Comparator.comparingInt(f -> StaticInfo.get().getRouteLength(
							FacilityInfo.get().getFacility(from).getLocation(), 
							f.getLocation()))).get().getName();			
		}
		
		return un.unifies(args[i], ASSyntax.createString(closest));
	}

}
