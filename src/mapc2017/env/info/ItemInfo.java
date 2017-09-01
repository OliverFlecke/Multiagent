package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import mapc2017.data.facility.Shop;
import mapc2017.data.item.Item;
import mapc2017.data.item.Tool;

public class ItemInfo {
	
	private static ItemInfo instance;	
	public  static ItemInfo get() { return instance; }

	private Map<String, Tool> 				tools 			= new HashMap<>();
	private Map<String, Item>				items 			= new HashMap<>();
	private Map<String, Map<String, Shop>> 	itemLocations 	= new HashMap<>();
	
	public ItemInfo() {
		instance = this;
	}

	public synchronized void addItem(Item item)
	{
		if (item instanceof Tool) 	tools.put(item.getName(), (Tool) item);
		else 						items.put(item.getName(), 		 item);
	}
	
	public synchronized void addItemLocation(String item, Shop shop)
	{
		if (itemLocations.containsKey(item))
		{
			itemLocations.get(item).put(shop.getName(), shop);
		}
		else
		{			
			itemLocations.put(item, new HashMap<>());
			itemLocations.get(item).put(shop.getName(), shop);
		}
	}
	
	public synchronized void clearItems()
	{
		items.clear();
		tools.clear();
	}
	
	public synchronized void clearItemLocations()
	{
		itemLocations.clear();
	}
	
	public synchronized Item getItem(String name)
	{
		if (name.startsWith("tool")) return tools.get(name);
		else						 return items.get(name);
	}
	
	public synchronized Tool getTool(String name) {
		return tools.get(name);
	}
	
	public synchronized Collection<Item> getItems() {
		return items.values();
	}
	
	public synchronized Map<String, Map<String, Shop>> getAllItemLocations() {
		return itemLocations;
	}
	
	public synchronized Collection<Shop> getItemLocations(String item) {
		return itemLocations.get(item).values();
	}
	
	public Map<String, Integer> getBaseItems(Map<String, Integer> items)
	{
		return stringToItemMap(items).entrySet().stream()
				.map(item -> item.getKey().getReqBaseItems().entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue() * item.getValue())).entrySet())
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum));
	}
	
	public Set<String> getBaseTools(Map<String, Integer> items)
	{
		return stringToItemMap(items).keySet().stream()
				.map(Item::getReqBaseTools)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}
	
	public int getVolume(Collection<String> tools)
	{
		return tools.stream()
				.map(this::getTool)
				.mapToInt(Tool::getVolume)
				.sum();
	}
	
	public int getVolume(Map<String, Integer> items) {
		return getItemVolume(stringToItemMap(items));
	}
	
	public int getBaseVolume(Map<String, Integer> items)
	{
		return getVolume(getBaseItems(items));
	}

	private Item getItem(Entry<String, ?> entry) {
		return this.getItem(entry.getKey());
	}

	public Map<Item, Integer> stringToItemMap(Map<String, Integer> items) {
		return items.entrySet().stream().collect(Collectors.toMap(this::getItem, Entry::getValue));
	}
	
	public static int getItemVolume(Map<Item, Integer> items) {
		return items.entrySet().stream().mapToInt(item -> 
			item.getKey().getVolume() * item.getValue()).sum();
	}
}
