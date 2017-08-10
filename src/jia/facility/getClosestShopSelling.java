package jia.facility;

import java.util.Comparator;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import jia.ASLParser;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;

public class getClosestShopSelling extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		int i = 0;
		
		String agent = ASLParser.parseString(args[i++]);
		String item	 = ASLParser.parseString(args[i++]);
		String shop  = ItemInfo.get().getItemLocations(item).stream()
							.min(Comparator.comparingInt(f -> StaticInfo.get()
									.getRouteDuration(AgentInfo.get(agent), f.getLocation())))
							.get().getName();
		
		return un.unifies(args[i], ASSyntax.createAtom(shop));
	}

}
