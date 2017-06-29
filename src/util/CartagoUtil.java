package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import info.ItemArtifact;
import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.parser.ParseException;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Tool;

public class CartagoUtil {

	public static Map<String, Integer> objectToStringMap(Object[] objs)
	{
		Map<String, Integer> map = new HashMap<>();
		
		try 
		{		
			for (Object obj : objs) 
			{
				Literal literal	= ASSyntax.parseLiteral(obj.toString());	
				String 	item 	= literal.getTerm(0).toString().replaceAll("\"", "");				
				int 	amount 	= (int) ((NumberTerm) literal.getTerm(1)).solve();
	
				map.put(item, amount);
			}
		} 
		catch (NoValueException | ParseException e) {
			e.printStackTrace();
		}
		
		return map;
	}

	public static Map<Item, Integer> objectToItemMap(Object[] objs) 
	{
		return DataUtil.stringToItemMap(objectToStringMap(objs));
	}
	
	public static String[] objectToStringArray(Object[] objs)
	{
		return Arrays.stream(objs).map(String.class::cast).toArray(String[]::new);
	}

	public static Tool[] objectToToolArray(Object[] objs) 
	{
		return DataUtil.stringToToolArray(objectToStringArray(objs));
	}

}
