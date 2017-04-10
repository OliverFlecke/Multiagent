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
		return Literal.parseLiteral(PrologVisitor.staticVisit((Percept) percept));
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
	
	public static String termToString(Term term) {
		return ((Atom) term).getFunctor();
	}
	
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
	
	public static int termToInteger(Term term) {
		return (int) termToDouble(term);
	}
	
	public static List<Term> termToTermList(Term term) {
		return ((ListTermImpl) term).getAsList();
	}
	
	public static List<Term> literalToTermList(Term term) {
		return ((ListTermImpl) ((LiteralImpl) term).getTerm(0)).getAsList();
	}
	
	public static Literal termToLiteral(Term term) {
		return (LiteralImpl) term;
	}
}