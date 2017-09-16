package mapc2017.jia.facility;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import mapc2017.data.facility.Facility;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.StaticInfo;
import mapc2017.env.parse.ASLParser;

public class getClosestFacility extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) 
	{
		int i = 0;
		
		String from 	= ASLParser.parseFunctor(args[i++]);
		String type 	= ASLParser.parseString	(args[i++]);
		
		Collection<? extends Facility> facilities;
		
		if (type.equals("chargingStation"))
		{
			facilities = FacilityInfo.get().getActiveChargingStations();
		}
		else
		{
			facilities = FacilityInfo.get().getFacilities(type);
		}
		
		Optional<? extends Facility> opt;
		
		if (from.startsWith("agent"))
		{
			opt = facilities.stream().min(Comparator.comparingInt(f -> StaticInfo.get()
					.getRouteDuration(AgentInfo.get(from), f.getLocation())));
		}
		else
		{
			opt = facilities.stream().min(Comparator.comparingInt(f -> StaticInfo.get()
					.getRouteLength(FacilityInfo.get().getFacility(from).getLocation(), f.getLocation())));
		}
		
		if (!opt.isPresent()) return false;
		
		return un.unifies(args[i], ASSyntax.createString(opt.get().getName()));
	}

}
