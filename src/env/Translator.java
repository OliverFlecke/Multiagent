package env;

import java.util.*;
import java.util.logging.Logger;

import eis.iilang.*;
import jason.NoValueException;
import jason.asSyntax.*;

public class Translator 
{
	
	private static final Logger logger = Logger.getLogger(Translator.class.getName());

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
	 * Converts a literal into a do-able action
	 * @param literal to convert
	 * @return The action which the literal was describing
	 */
	public static Action literalToAction(Literal literal)
	{	
		Parameter[] parameters = new Parameter[literal.getArity()];
		for (int i = 0; i < parameters.length; i++)
			parameters[i] = termToParameter(literal.getTerm(i));
		return new Action(literal.getFunctor(), parameters);
	}

	/**
	 * 
	 * @param percept
	 * @return
	 */
	public static Literal perceptToLiteral(Percept percept) {
		return Literal.parseLiteral(PrologVisitor.staticVisit(percept));
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
	 * Casts a term to an atom and returns the functor.
	 * @param term
	 * @return
	 */
	public static String termToString(Term term) {
		return ((Atom) term).getFunctor();
	}
	
	/**
	 * Casts a term to a number term and returns the solved result.
	 * Returns 0.0 if the term is not a number term.
	 * @param term
	 * @return
	 */
	public static double termToDouble(Term term) {
		try {
			return ((NumberTerm) term).solve();
		}
		catch (NoValueException e) 
		{
			logger.warning("termToDouble: " + e);
			return 0.0;
		}		
	}
	
	/**
	 * Casts the result of calling termToDouble to an integer.
	 * @param term
	 * @return
	 */
	public static int termToInteger(Term term) {
		return (int) termToDouble(term);
	}
	
	/**
	 * Casts the result of calling termToDouble to a long
	 * @param term
	 * @return
	 */
	public static long termToLong(Term term) {
		return (long) termToDouble(term);
	}
	
	/**
	 * Casts the term to a ListTerm and returns it as a list.
	 * @param term
	 * @return
	 */
	public static List<Term> termToTermList(Term term) {
		return ((ListTermImpl) term).getAsList();
	}
	
	/**
	 * Calls termToLiteral, gets the first term and returns the 
	 * result of calling termToTermList.
	 * @param term
	 * @return
	 */
	public static List<Term> literalToTermToTermList(Term term) {
		return termToTermList(termToLiteral(term).getTerm(0));
	}
	
	public static List<Term> literalToTermList(Term term) {
		return termToLiteral(term).getTerms();
	}
	
	/**
	 * Casts the term to a literal and returns the result.
	 * @param term
	 * @return
	 */
	public static Literal termToLiteral(Term term) {
		return (LiteralImpl) term;
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
			} catch (NoValueException e) {
				e.printStackTrace();
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