package util;

import java.util.HashMap;
import java.util.Map;

import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.parser.ParseException;
import massim.scenario.city.data.Item;

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
		return DataUtil.stringToItemMap(CartagoUtil.objectToStringMap(objs));
	}

}
