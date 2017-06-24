package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;

public class ASUtil {
	
	private static Term entryToTerm(Entry<String, Integer> entry) 
	{
		return ASSyntax.createLiteral("map", 
				  ASSyntax.createAtom(entry.getKey()),
				  ASSyntax.createNumber(entry.getValue()));
	}
	
	public static Term mapToTerm(Map<String, Integer> map) 
	{		
		return map.entrySet().stream()
				.map(ASUtil::entryToTerm)
				.collect(Collectors.toCollection(ASSyntax::createList));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Integer> objectToMap(Object obj)
	{
		if (obj instanceof Map<?, ?>) return (Map<String, Integer>) obj;
		else 						  return new HashMap<String, Integer>();
	}

}
