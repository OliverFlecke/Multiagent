package scenario.data.facility;

import java.util.Map;

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

}
