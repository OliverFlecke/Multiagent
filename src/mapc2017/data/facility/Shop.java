package mapc2017.data.facility;

import java.util.Map;
import java.util.Set;

public class Shop extends Facility {
	
	private int 					restock;
	private Map<String, Integer> 	price;
	private Map<String, Integer> 	amount;

	public Shop(Facility facility, int restock, 
			Map<String, Integer> price, Map<String, Integer> amount) {
		super(facility);
		this.restock	= restock;
		this.price		= price;
		this.amount		= amount;
	}
	
	public int getRestock() {
		return restock;
	}
	
	public int getPrice(String item) {
		return price.get(item).intValue();
	}
	
	public int getAmount(String item) {
		return amount.get(item).intValue();
	}
	
	public Set<String> getItems() {
		return amount.keySet();
	}
	
	public Map<String, Integer> getPrice() {
		return price;
	}
	
	public Map<String, Integer> getAmount() {
		return amount;
	}
	
	public void setPrice(Map<String, Integer> price) {
		this.price = price;
	}
	
	public void setAmount(Map<String, Integer> amount) {
		this.amount = amount;
	}

}
