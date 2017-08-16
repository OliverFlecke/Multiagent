package mapc2017.data;

import java.util.HashMap;
import java.util.Map;

import mapc2017.env.info.ItemInfo;

public class ItemList extends HashMap<String, Integer> {

	private static final long serialVersionUID = 1L;

	public ItemList() {
		super();
	}
	
	public ItemList(String item, int amount) {
		super();
		this.put(item, amount);
	}
	
	/**
	 * Returns itemsToCarry according to the capacity and 
	 * modifies items such as items = items - itemsToCarry.
	 */
	public static ItemList getItemsToCarry(Map<String, Integer> items, int capacity)
	{
		ItemList itemsToCarry = new ItemList();

		for (Entry<String, Integer> entry : items.entrySet())
		{
			Item 	item 	= ItemInfo.get().getItem(entry.getKey());
			int 	amount 	= entry.getValue();
			int		volume	= item.getReqBaseVolume();

			int amountToCarry = Math.min(amount, capacity / volume);			
			
			if (amountToCarry > 0) 
			{
				capacity -= volume * amountToCarry;
				
				itemsToCarry.put(item.getName(), amountToCarry);
			}
		}
		
		for (Entry<String, Integer> entry : itemsToCarry.entrySet())
		{
			String 	item 	= entry.getKey();
			int 	amount 	= items.get(item);
			
			int amountToCarry = entry.getValue();
			
			if (amountToCarry == amount)
			{
				items.remove(item);
			}
			else
			{
				items.put(item, amount - amountToCarry);
			}
		}		
		return itemsToCarry;
	}
}
