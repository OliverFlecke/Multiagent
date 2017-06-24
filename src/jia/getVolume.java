package jia;

import java.util.Map;

import env.Translator;
import info.ItemArtifact;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import util.ASUtil;
import util.DataUtil;

public class getVolume extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception 
	{	
		int volume = -1;
		
		Object obj = Translator.termToObject(terms[0]);
		
		if (obj instanceof String)
		{
			volume = ItemArtifact.getItem((String) obj).getVolume();
		}
		else if (obj instanceof Map<?, ?>)
		{
			volume = ItemArtifact.getVolume(DataUtil.stringToItemMap(ASUtil.objectToMap(obj)));
		}
		
		if (volume < 0) System.out.println("Something not taken into account? jia.getVolume");
		
		if (volume < 0) return false;
		
		return un.unifies(ASSyntax.createNumber(volume), terms[1]);
	}
}
