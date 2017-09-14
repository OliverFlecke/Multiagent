package mapc2017.data.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import mapc2017.data.facility.Shop;
import mapc2017.env.info.ItemInfo;

public class ShoppingList extends HashMap<String, ItemList> {

	private static final long serialVersionUID = 1L;
	
	public ShoppingList() {
		super();
	}
	
	public ShoppingList(String shop, ItemList items) {
		super();
		this.put(shop, items);
	}
	
	public void put(String shop, String item, int amount) {
		if (this.containsKey(shop))	this.get(shop).put(item, amount);
		else						this.put(shop, new ItemList(item, amount));
	}

	/**
	 * Creates and returns a ShoppingList based on the given items.
	 */
	public static ShoppingList getShoppingList(Map<String, Integer> items)
	{	
		ShoppingList shoppingList 	= new ShoppingList();
		ItemInfo 	 iInfo 			= ItemInfo.get();
		
		for (Entry<String, Integer> entry : iInfo.getBaseItems(items).entrySet())
		{			
			String 	item 	= entry.getKey();
			int 	amount 	= entry.getValue();
			
			Collection<Shop> shops = iInfo.getItemLocations(item);
			
			Optional<Shop> shop = shops.stream()
					.filter(x -> x.getAmount(item) > amount).findAny();
			
			if (shop.isPresent())
			{
				shoppingList.put(shop.get().getName(), item, amount);
			}
			else 
			{
				int amountRemaining = amount;
				do
				{
					// If there is only one shop remaining, it should buy the rest
					if (shops.size() == 1)
					{
						shoppingList.put(shops.stream().findAny().get().getName(), item, amountRemaining);
						break;
					}
					
					// Find the shop with the largest number of the item
					shop = shops.stream().max((x, y) -> x.getAmount(item) - y.getAmount(item));
					
					if (shop.isPresent())
					{
						shops.remove(shop.get());
						
						int amountToBuy = Math.min(shop.get().getAmount(item), amountRemaining);
						
						amountRemaining -= amountToBuy;
						
						shoppingList.put(shop.get().getName(), item, amountToBuy);
					}
				}
				while (amountRemaining > 0);
			}
		}		
		
		for (String tool : iInfo.getBaseTools(items))
		{
			Collection<Shop> shops = iInfo.getItemLocations(tool);
			
			Optional<Shop> shopOpt = shops.stream()
							.filter(s -> shoppingList.containsKey(s.getName())).findAny();
			
			String shop = shopOpt.isPresent() 	? shopOpt.get().getName() 
												: shops.stream().findAny().get().getName();
			
			shoppingList.put(shop, tool, 1);
		}
		
		return shoppingList;
	}

}
