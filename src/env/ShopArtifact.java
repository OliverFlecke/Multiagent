package env;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;

public class ShopArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(ShopArtifact.class.getName());

	private static HashSet<String> 			shops;
	private static HashMap<String, Integer> itemsAndPrices;
	
	/**
	 * Instantiates shops and itemsAndPrices.
	 */
	void init() 
	{
		logger.info("init");

		shops 		   = new HashSet<String>();
		itemsAndPrices = new HashMap<String, Integer>();
	}
	
	/**
	 * Adds the shop to the Artifact's set of shops and maps the 
	 * available items with their corresponding prices.
	 * @param shop - The shop's ID
	 * @param items - The items sold in the shop
	 */
	public static synchronized void addItems(String shop, String... items)
	{
		logger.info("Adding items from shop: " + shop);
		
		if (shops.add(shop))
		{
			for (String item : items)
			{
				itemsAndPrices.put(item, 0);
			}			
		}
		
	}
	
	/**
	 * Defines observable properties for each item with their corresponding prices.
	 */
	@OPERATION
	void addPrices() 
	{
		logger.info("Adding prices");
		
		for (Entry<String, Integer> entry : itemsAndPrices.entrySet())
		{
			// Does this overwrite or extend the observable property?
			defineObsProperty("price", entry.getKey(), entry.getValue());
		}
	}
}
