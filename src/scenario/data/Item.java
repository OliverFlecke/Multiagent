package scenario.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import scenario.env.info.ItemInfo;

public class Item {

	private String 					name;
	private int						volume;
	private Set<String> 			reqTools;
	private Map<String, Integer> 	reqItems;
	
	private int						reqBaseVolume;
	private Map<String, Integer>	reqBaseItems;
	
	public Item(String name, int volume, Set<String> tools, Map<String, Integer> parts) {
		this.name 		= name;
		this.volume		= volume;
		this.reqTools	= tools;
		this.reqItems	= parts;
	}
	
	// GETTERS
	
	public String getName() {
		return name;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public Set<String> getReqTools() {
		return new HashSet<>(reqTools);
	}
	
	public Map<String, Integer> getReqItems() {
		return new HashMap<>(reqItems);
	}
	
	public Map<String, Integer> getReqBaseItems() 
	{
		if (reqBaseItems == null)
		{
			// If required items is empty, this item is a base item 
			// and the required base items is itself
			if (reqItems.isEmpty())
			{
				reqBaseItems 	= new HashMap<>();
				reqBaseItems	.put(name, 1);
				reqBaseVolume 	= volume;
			}
			// If required items is not empty, the required base items
			// are the required base items for each of the required items
			else
			{
				reqBaseItems	= ItemInfo.get().getBaseItems(reqItems);
				reqBaseVolume	= ItemInfo.get().getVolume(reqBaseItems);
			}
		}
		return new HashMap<>(reqBaseItems);
	}
	
	public int getReqBaseVolume() 
	{
		if (reqBaseItems == null)
		{
			getReqBaseItems();
		}
		return reqBaseVolume;
	}
}
