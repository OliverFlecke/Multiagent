package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import info.ItemArtifact;
import massim.protocol.scenario.city.data.ItemAmountData;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Job;
import massim.scenario.city.data.facilities.Shop;

public class DataUtil {
	
	private static String itemToString(Entry<Item, ?> entry) {
		return entry.getKey().getName();
	}

	public static Map<String, Integer> itemToStringMap(Map<Item, Integer> items) {
		return items.entrySet().stream()
				.collect(Collectors.toMap(DataUtil::itemToString, Entry::getValue));
	}

	private static Item stringToItem(Entry<String, ?> entry) {
		return ItemArtifact.getItem(entry.getKey());
	}

	public static Map<Item, Integer> stringToItemMap(Map<String, Integer> items) {
		return items.entrySet().stream()
				.collect(Collectors.toMap(DataUtil::stringToItem, Entry::getValue));
	}
    
    private static String shopToString(Entry<Shop, ?> entry) {
    	return entry.getKey().getName();
    }
    
    public static Map<String, Map<String, Integer>> shoppingListToString(Map<Shop, Map<Item, Integer>> map)
    {
    	return map.entrySet().stream()
    			.collect(Collectors.toMap(DataUtil::shopToString, e -> itemToStringMap(e.getValue())));
    }
    
    public static Map<String, Integer> extractItems(Job job) {
		return job.getRequiredItems().toItemAmountData().stream()
				.collect(Collectors.toMap(ItemAmountData::getName, ItemAmountData::getAmount));
    }
	
	/**
	 * @param map The map to add the content to
	 * @param first The element in the first map, which the content should be added to 
	 * @param second The element in the second map, to which the content should be addad
	 * @param content The content to add
	 */
	public static <A,B,C> void addToMapOfMaps(Map<A, Map<B, C>> map, A first, B second, C content) 
	{
		if (!map.containsKey(first))
		{
			map.put(first, new HashMap<B, C>());
		}
		map.get(first).put(second, content);
	}

}
