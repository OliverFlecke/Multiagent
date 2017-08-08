package mapc2017.data.facility;

import java.util.Map;
import java.util.Set;

public class Shop extends Facility {
	
	int 					restock;
	Map<String, Integer> 	price;
	Map<String, Integer> 	amount;

	public Shop(Facility facility, int restock, 
			Map<String, Integer> price, Map<String, Integer> amount) {
		super(facility);
		this.restock	= restock;
		this.price		= price;
		this.amount		= amount;
	}
	
	public int getAmount(String item)
	{
		return amount.get(item).intValue();
	}
	
	public Set<String> getItems()
	{
		return amount.keySet();
	}

}
