package mapc2017.data.facility;

import java.util.Map;
import java.util.Set;

import mapc2017.data.item.ItemList;

public class Shop extends Facility {
	
	private int 		restock;
	private ItemList 	price;
	private ItemList 	amount;
	private ItemList 	local;

	public Shop(Facility facility, int restock, 
			Map<String, Integer> price, Map<String, Integer> amount) {
		super(facility);
		this.restock	= restock;
		this.price		= new ItemList(price);
		this.amount		= new ItemList(amount);
		this.local		= new ItemList();
	}
	
	public void update(Shop s) {
		this.price  = s.price;
		this.amount = s.amount;
	}
	
	public int getRestock() {
		return restock;
	}
	
	public int getPrice(String item) {
		return price.get(item);
	}
	
	public int getAmount(String item) {
		return amount.get(item);
	}
	
	public int getAvailableAmount(String item) {
		Integer reserved = local.get(item);
		return amount.get(item) - (reserved != null ? reserved : 0);
	}
	
	public Set<String> getItems() {
		return amount.keySet();
	}
	
	public void addReserved(String item, int amount) {
		this.local.add(item, amount);
	}
	
	public void remReserved(String item, int amount) {
		this.local.subtract(item, amount);
	}

}
