package env;

import java.util.*;
import eis.iilang.*;
import jason.NoValueException;
import jason.asSyntax.*;

public class Translator 
{
	/**
	 * 
	 * @param term
	 * @return
	 */
	public static Parameter termToParameter(Term term)
	{
		if (term.isString())
		{
			String string = ((StringTerm) term).getString();
			System.out.println(string);
			return new Identifier(string);
		}
		else if (term.isNumeric()) 
		{
			try {
				return new Numeral(((NumberTerm) term).solve());
			} catch (NoValueException e) {
				e.printStackTrace();
			}
		} 
		else if (term.isList()) 
		{
			Collection<Parameter> terms = new ArrayList<Parameter>();
			for (Term listTerm : (ListTerm) term)
				terms.add(termToParameter(listTerm));
			return new ParameterList(terms);
		} 
		else if (term.isLiteral()) 
		{
			Literal l = (Literal) term;
			if (!l.hasTerm()) 
			{
				return new Identifier(l.getFunctor());
			} 
			else 
			{
				Parameter[] terms = new Parameter[l.getArity()];
				for (int i = 0; i < l.getArity(); i++)
					terms[i] = termToParameter(l.getTerm(i));
				return new Function(l.getFunctor(), terms);
			}
		}
		return new Identifier(term.toString());
	}
	
	/**
	 * 
	 * @param action
	 * @return
	 */
	public static Action stringToAction(String action)
	{
		return literalToAction(Literal.parseLiteral(action));
	}
	
	/**
	 * 
	 * @param literal
	 * @return
	 */
	public static Action literalToAction(Literal literal)
	{	
		// TODO Implement correctly
		Parameter[] pars = new Parameter[literal.getArity()];
		for (int i = 0; i < pars.length; i++)
			pars[i] = termToParameter(literal.getTerm(i));
		return new Action(literal.getFunctor(), new Identifier("shop1"));
	}

	public static Literal perceptToLiteral(Percept percept) {
		return Literal.parseLiteral(percept.getName());
	}

	public static Object[] parametersToArguments(List<Parameter> parameters) {
		Object[] out = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++)
		{
			out[i] = parameters.get(i).toString();
		}
		return out;
	}
}
