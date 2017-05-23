package env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eis.iilang.Action;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import eis.iilang.PrologVisitor;
import jason.NoValueException;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;

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
			return new Identifier(string);
		}
		else if (term.isNumeric()) 
		{
			try {
				double number = ((NumberTerm) term).solve();
				if ((int) number == number)
					return new Numeral((int) number);
				else
					return new Numeral(number);
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
	public static Action stringToAction(String action) {
		return literalToAction(Literal.parseLiteral(action));
	}
	
	/**
	 * Converts a literal into a do-able action
	 * @param literal to convert
	 * @return The action which the literal was describing
	 */
	public static Action literalToAction(Literal literal) {	
		Parameter[] parameters = new Parameter[literal.getArity()];
		for (int i = 0; i < parameters.length; i++)
			parameters[i] = termToParameter(literal.getTerm(i));
		return new Action(literal.getFunctor(), parameters);
	}

	/**
	 * Convert a list of parameters into an array of object
	 * @param parameters to convert
	 * @return An array of objects, containing the parameters
	 */
	public static Object[] parametersToArguments(List<Parameter> parameters) {
		Object[] arguments = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++)
		{
			Parameter parameter = parameters.get(i);
			if (parameter instanceof Numeral)
			{
				PrologVisitor visitor = new PrologVisitor();
				arguments[i] = visitor.visit((Numeral) parameter, new Object());
			}
			else
			{
				arguments[i] = PrologVisitor.staticVisit(parameter);
			}
		}
		return arguments;
	}

	/**
	 * 
	 * @param percept
	 * @return
	 */
	public static Literal perceptToLiteral(Percept percept) {
		return Literal.parseLiteral(PrologVisitor.staticVisit(percept));
	}
	
	public static Object[] perceptToObject(Percept p) {
		return (Object[]) termToObject(perceptToLiteral(p));
	}
	
	/**
	 * Convert a Jason term into a CArtAgO/Java Object
	 * 
	 * @param t Jason term
	 * @param lib Java library - each agent has its own one
	 * @return
	 */
	public static Object termToObject(Term t)
	{
		if (t.isAtom())
	    {
			Atom a = (Atom) t;
			
				 if (a.equals(Atom.LTrue))  return Boolean.TRUE;
			else if (a.equals(Atom.LFalse)) return Boolean.FALSE;
			else							return a.getFunctor();
	    } 
	    else if (t.isNumeric())
	    {
			try {
				double d = ((NumberTerm) t).solve();			
					 if ((int)   d == d) return (int)   d;
				else if ((float) d == d) return (float) d;
				else if ((long)  d == d) return (long)  d;
				else					 return 		d;				
			} catch (NoValueException e) 
			{
				String s = t.toString();
				// Remove parenthesis
				return s.substring(1, s.length() - 1);
			}
		} 
	    else if (t.isString())
	    {
			return ((StringTerm) t).getString();
		} 
	    else if (t.isList())
	    {
			ListTerm lt = (ListTerm) t;
			
			Object[] list = new Object[lt.size()];
			
			for (int i = 0; i < lt.size(); i++)
			{
				list[i] = termToObject(lt.get(i));
			}
			return list;
		}
	    else if (t.isLiteral())
	    {
	    	Literal l = (Literal) t;
	    	
	    	Object[] list = new Object[l.getArity()];
	    	
			for (int i = 0; i < l.getArity(); i++)
			{
				list[i] = termToObject(l.getTerm(i));
			}
			return list;
	    }
		return t.toString();
	}
}