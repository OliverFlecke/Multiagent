package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Term;
import massim.scenario.city.data.Item;

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
	public static Map<String, Integer> objectToStringMap(Object obj)
	{
		if (obj instanceof Object[]	&& ((Object[]) obj).length == 0)
		{
			return new HashMap<String, Integer>();			
		}			 
		return (Map<String, Integer>) obj;
	}

	public static Map<Item, Integer> objectToItemMap(Object obj) 
	{
		return DataUtil.stringToItemMap(ASUtil.objectToStringMap(obj));
	}
	
	public static Term stringsToTerm(String[] strings)
	{
		return Arrays.stream(strings)
				.map(ASSyntax::createAtom)
				.collect(Collectors.toCollection(ASSyntax::createList));
	}
	
	public static String[] objectToStrings(Object obj)
	{
		return Arrays.stream((Object[]) obj)
				.map(String.class::cast)
				.toArray(String[]::new);
	}

}
