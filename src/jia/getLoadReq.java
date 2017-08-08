package jia;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import mapc2017.data.Item;
import mapc2017.env.info.ItemInfo;

public class getLoadReq extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{		
		Map<String, Integer> items = ASLParser.parseMap(args[0]);
		
		int loadReq = items.keySet().stream()
				.map(ItemInfo.get()::getItem)
				.mapToInt(Item::getReqBaseVolume)
				.max().getAsInt();
		
		return un.unifies(args[1], ASSyntax.createNumber(loadReq));
	}

}
