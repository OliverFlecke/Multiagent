package mapc2017.jia.items;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.parse.ASLParser;

public class getReqItems extends DefaultInternalAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		int i = 0;
		
		String 					item 	= ASLParser.parseString(args[i++]);		
		Map<String, Integer> 	req 	= ItemInfo.get().getItem(item).getReqItems();
		
		return un.unifies(args[i], ASLParser.createMap(req));
	}

}