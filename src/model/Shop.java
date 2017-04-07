package model;

import java.util.HashMap;
import java.util.Map;

// Note: 

public class Shop extends Facility {

	// How often the shop restocks
	private int restock;
	// Tuple contains price and quantity
	private Map<Item, Tuple<Integer>> items;
	
	/**
	 * Constructs a shop containing a map of the items sold, where
	 * each item has a price and a quantity.
	 * @param name - Name
	 * @param lon - Longtitude
	 * @param lat - Latitude
	 * @param restock - If a shop has restock 5, one piece of each 
	 * missing item is added to the shop's stock each 5 steps.
	 * @param items - Map of the items sold as key, and a tuple 
	 * with price and quantity
	 */
	public Shop(String name, double lon, double lat, int restock)
	{
		super(name, lon, lat);
		this.restock = restock;
		this.items = new HashMap<Item, Tuple<Integer>>();
	}
	
	public void addItem(Item item, int price, int quantity) {
		items.put(item, new Tuple<Integer>(price, quantity));
	}
	
	public int getPrice(Item item) {
		return items.get(item).getContent(0);
	}
	
	public void setPrice(Item item, int price) {
		items.get(item).setContent(0, price);
	}
	
	public int getQuantity(Item item) {
		return items.get(item).getContent(1);
	}
	
	public void setQuantity(Item item, int quantity) {
		items.get(item).setContent(1, quantity);
	}
	
	public int getRestock() {
		return restock;
	}
	
	// Shouldn't be necessary
//	public Map<Item, Tuple<Integer>> getItems() {
//		return items;
//	}
}
