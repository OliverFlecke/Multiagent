package env;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import jason.asSyntax.*;
import massim.scenario.city.data.*;
import massim.scenario.city.data.facilities.Shop;

public class ShopArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(ShopArtifact.class.getName());

	private static Map<String, Item>		items 			= new HashMap<>();
	private static Map<String, Shop> 		shops 			= new HashMap<>();
	private static Map<String, Set<Shop>> 	itemLocations 	= new HashMap<>();	
	
	@OPERATION
	void getItems(OpFeedbackParam<Collection<Item>> ret) {
		ret.set(items.values());
	}
	
	@OPERATION
	void getShops(OpFeedbackParam<Collection<Shop>> ret) {
		ret.set(shops.values());
	}
	
	@OPERATION
	void getShopsSelling(String item, OpFeedbackParam<Collection<Shop>> ret) {
		ret.set(itemLocations.get(item));
	}
	
	protected static void perceiveInitial(Collection<Percept> percepts)
	{
		logger.info("Perceiving initial percepts");
		
		Map<Item, Set<List<Term>>> requirements = new HashMap<>();
		
		percepts.stream().filter(percept -> percept.getName() == "item")
						 .forEach(item -> perceiveItem(item, requirements));
		
		// Item requirements has to be added after all items have been created, 
		// since they are not necessarily given in a chronological order.
		// TODO: Tools and items that require assembly have a volume of 0.
		for (Entry<Item, Set<List<Term>>> entry : requirements.entrySet())
		{
			Item item = entry.getKey();
			
			for (List<Term> partTuple : entry.getValue())
			{
				String itemId   = Translator.termToString(partTuple.get(0));
				int    quantity = Translator.termToInteger(partTuple.get(1));
				
				item.addRequirement(items.get(itemId), quantity);
			}
		}
		
		logger.info("Items perceived:");
		
		for (Item item : items.values())
		{
			logger.info(item.toString());
		}
		
		percepts.stream().filter(percept -> percept.getName() == "shop")
		 				 .forEach(shop -> perceiveShop(shop));
		
		logger.info("Shops perceived:");
		
		for (Shop shop : shops.values())
		{
			logger.info(shop.toString());
		}
	}

	// Literal(String, int, Literal(List<String>), Literal(List<List<String, int>>))
	private static void perceiveItem(Percept percept, Map<Item, Set<List<Term>>> requirements)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String     id		= Translator.termToString(args[0]);
		int 	   volume	= Translator.termToInteger(args[1]);
		
		Item item = new Item(id, volume, 0, Collections.emptySet());

		for (Term toolArg : Translator.literalToTermList(args[2]))
		{
			String toolId = Translator.termToString(toolArg);
			
			if (!items.containsKey(toolId))
			{
				items.put(toolId, new Tool(toolId, 0, 0));
			}				
			item.addRequiredTool((Tool) items.get(toolId));
		}
		
		Set<List<Term>> parts = new HashSet<>();

		for (Term partArg : Translator.literalToTermList(args[3]))
		{
			List<Term> partTuple = Translator.termToTermList(partArg);
			
			parts.add(partTuple);	
		}		
		items.put(id, item);
		requirements.put(item, parts);
	}
	
	// Literal(String, Double, Double, int, List<Literal(String, int, int)>)
	private static void perceiveShop(Percept percept) 
	{		
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name    = Translator.termToString(args[0]);
		double lon     = Translator.termToDouble(args[1]);
		double lat     = Translator.termToDouble(args[2]);
		int    restock = Translator.termToInteger(args[3]);
		
		Shop shop = new Shop(name, new Location(lon, lat), restock);
		
		for (Term itemLiteral : Translator.termToTermList(args[4])) 
		{
			Term[] itemArgs = Translator.termToLiteral(itemLiteral).getTermsArray();
			
			String itemId = Translator.termToString(itemArgs[0]);
			int price 	  = Translator.termToInteger(itemArgs[1]);
			int quantity  = Translator.termToInteger(itemArgs[2]);
			
			shop.addItem(items.get(itemId), quantity, price);	
			addItemLocation(itemId, shop);
		}		
		shops.put(name, shop);
	}
	
	private static void addItemLocation(String itemId, Shop shop)
	{
		if (itemLocations.containsKey(itemId))
		{
			itemLocations.get(itemId).add(shop);
		}
		else
		{
			Set<Shop> shops = new HashSet<>(Arrays.asList(shop));
			
			itemLocations.put(itemId, shops);
		}
	}
}
