package jia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

public class ASLParser {
	

	static Atom parseAtom(Term t) {
		return (Atom) t;
	}
	
	static NumberTerm parseNumber(Term t) {
		return (NumberTerm) t;
	}
	
	static ListTerm parseList(Term t) {
		return (ListTerm) t;
	}
	
	static Literal parseLiteral(Term t) {
		return (Literal) t;
	}
	
	static String parseString(Term t) {
		return parseAtom(t).getFunctor();
	}
	
	static double parseDouble(Term t) {
		try {
			return parseNumber(t).solve();
		} catch (NoValueException e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}
	
	static int parseInt(Term t) {
		return (int) parseDouble(t);
	}
	
	static long parseLong(Term t) {
		return (long) parseDouble(t);
	}
	
	static String[] parseArray(Term t) {
		List<String> list = new ArrayList<>();
		for (Term tm : parseList(t)) {
			list.add(parseString(tm));
		}
		return list.toArray(new String[list.size()]);
	}
	
	static Map<String, Integer> parseMap(Term t) {
		Map<String, Integer> map = new HashMap<>();
		for (Term tm : parseList(t)) {
			Literal entry 	= parseLiteral(tm);
			String 	key 	= parseString(entry.getTerm(0));
			int		value	= parseInt(entry.getTerm(1));
			map.put(key, value);
		}
		return map;
	}
	
	
	static Literal createEntry(Entry<String, Integer> entry) 
	{
		return ASSyntax.createLiteral("map", 
				  ASSyntax.createAtom(entry.getKey()),
				  ASSyntax.createNumber(entry.getValue()));
	}
	
	public static ListTerm createMap(Map<String, Integer> map) 
	{		
		return map.entrySet().stream()
				.map(ASLParser::createEntry)
				.collect(Collectors.toCollection(ASSyntax::createList));
	}

}
