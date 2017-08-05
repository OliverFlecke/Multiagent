package scenario.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import scenario.data.Item;

public class ItemInfo {

	private Map<String, Item> 				tools 			= new HashMap<>();
	private Map<String, Item>				items 			= new HashMap<>();
//	private Map<String, Map<String, Shop>> 	itemLocations 	= new HashMap<>();
	
	private static ItemInfo instance;
	
	public ItemInfo() {
		instance = this;
	}
	
	public static ItemInfo get() {
		return instance;
	}

	public void addItem(Item item)
	{
		if (item.getName().startsWith("tool"))
		{
			tools.put(item.getName(), item);
		}
		else
		{
			items.put(item.getName(), item);
		}
	}
	
	public Map<String, Integer> getBaseItems(Map<String, Integer> items)
	{
		return stringToItemMap(items).entrySet().stream()
				.map(item -> item.getKey().getReqBaseItems().entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue() * item.getValue())).entrySet())
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum));
	}
	
	public int getVolume(Map<String, Integer> items)
	{
		return stringToItemMap(items).entrySet().stream().mapToInt(item -> 
			item.getKey().getVolume() * item.getValue()).sum();
	}

	private Item getItem(Entry<String, ?> entry) {
		return items.get(entry.getKey());
	}

	private Map<Item, Integer> stringToItemMap(Map<String, Integer> items) {
		return items.entrySet().stream().collect(Collectors.toMap(this::getItem, Entry::getValue));
	}
}
