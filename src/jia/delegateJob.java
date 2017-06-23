package jia;

import java.util.Map;

import env.Translator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class delegateJob extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{
		Object[] args = Translator.termsToObject(terms);
		
		String 					id 			= (String) 					args[0];
		Map<String, Integer> 	items 		= (Map<String, Integer>) 	args[1];
		String 					facility 	= (String) 					args[2];
		
		
		// TODO Auto-generated method stub
		return super.execute(ts, un, terms);
	}
	
}
