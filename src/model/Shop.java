package model;

import java.util.HashMap;
import java.util.Map;

import cartago.Tuple;

// Note: 

public class Shop extends Facility {

	// How often the shop restocks
	private int restock;
	// Tuple contains price and quantity
	private Map<Item, Tuple> items;
	
	/**
	 * 
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
		this.items = new HashMap<Item, Tuple>();
	}
	
	public void addItem(Item item, int price, int quantity)
	{
		Tuple t = new Tuple("asdf", price, quantity);
	}
	
	public int getRestock() {
		return restock;
	}
	
	public Map<Item, Tuple> getItems() {
		return items;
	}
}
