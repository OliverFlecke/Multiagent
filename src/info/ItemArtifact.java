package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Tool;
import massim.scenario.city.data.facilities.Shop;

public class ItemArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(ItemArtifact.class.getName());

	private static final String ITEM = "item";

	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ITEM)));

    private static Map<String, Tool> 				tools 			= new HashMap<>();
	private static Map<String, Item>				items 			= new HashMap<>();
	private static Map<String, Map<String, Shop>> 	itemLocations 	= new HashMap<>();	
	
	@OPERATION
	void getItems(OpFeedbackParam<Collection<Item>> ret) {
		ret.set(items.values());
	}
	
	private static Map<String, Integer> getBaseItem(String name)
	{	
		return items.get(name).getRequiredBaseItems().entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue()));				
	}

	@OPERATION
	void getBaseItems(Object[] items, OpFeedbackParam<Object> ret)
	{	
		ret.set(Arrays.stream(items)
				.map(item -> getBaseItem((String) item).entrySet()).flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
	}
	
	@OPERATION 
	void getRequiredItems(Object itemName, OpFeedbackParam<Object> ret)
	{
		ret.set(items.get(itemName).getRequiredItems());
	}
	
	
	@OPERATION
	void getShopsSelling(String item, OpFeedbackParam<Collection<Shop>> ret) {
		ret.set(itemLocations.get(item).values());
	}
	
	@OPERATION
	void getShopSelling(String itemName, int quantity, OpFeedbackParam<String> retShop, OpFeedbackParam<Integer> retQuantity) 
	{
		Item 				item 	= items.get(itemName);
		Collection<Shop> 	shops 	= itemLocations.get(itemName).values();
		
		List<Shop> sortedShops = shops.stream().sorted((s1, s2) -> s2.getItemCount(item) - s1.getItemCount(item)).collect(Collectors.toList());

		retShop.set(sortedShops.get(0).getName());
		retQuantity.set(Math.min(quantity, sortedShops.get(0).getItemCount(item)));
	}
	
	@OPERATION
	void getClosestFacilitySelling(String item, OpFeedbackParam<String> ret)
	{
		Location agLoc = AgentArtifact.getEntity(getOpUserName()).getLocation();
		
		Collection<Shop> shops = itemLocations.get(item).values();
		
		ret.set(FacilityArtifact.getClosestFacility(agLoc, shops));
	}
	
	public static void perceiveInitial(Collection<Percept> percepts)
	{		
		Map<Item, Set<Object[]>> requirements = new HashMap<>();
		
		percepts.stream().filter(percept -> percept.getName() == ITEM)
						 .forEach(item -> perceiveItem(item, requirements));
		
		// Item requirements has to be added after all items have been created, 
		// since they are not necessarily given in a chronological order.
		// TODO: Tools and items that require assembly have a volume of 0.
		for (Entry<Item, Set<Object[]>> entry : requirements.entrySet())
		{
			Item item = entry.getKey();
			
			for (Object[] part : entry.getValue())
			{
				String itemId   = (String) part[0];
				int    quantity = (int)    part[1];
				
				item.addRequirement(items.get(itemId), quantity);
			}
		}

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info("Perceived items:");		
			for (Item item : items.values())
				logger.info(item.toString());
		}
	}

	// Literal(String, int, Literal(List<String>), Literal(List<List<String, int>>))
	private static void perceiveItem(Percept percept, Map<Item, Set<Object[]>> requirements)
	{				
		Object[] args = Translator.perceptToObject(percept);
		
		String     id		= (String) args[0];
		int 	   volume	= (int)    args[1];
		
		Item item = new Item(id, volume, 0, Collections.emptySet());

		for (Object toolArg : ((Object[]) ((Object[]) args[2])[0]))
		{
			String toolId = (String) toolArg;
			
			if (!tools.containsKey(toolId))
			{
				tools.put(toolId, new Tool(toolId, 0, 0));
			}				
			item.addRequiredTool(tools.get(toolId));
		}
		
		Set<Object[]> parts = new HashSet<>();

		for (Object part : ((Object[]) ((Object[]) args[3])[0]))
		{			
			parts.add((Object[]) part);	
		}
		items.put(id, item);
		requirements.put(item, parts);
	}
	
	// Used by the FacilityArtifact when adding items to shops.
	public static Item getItem(String itemId)
	{
        if (items.containsKey(itemId)) 
        {
        	return items.get(itemId);
        }
        return tools.get(itemId);
	}
	
	// Used by the FacilityArtifact when adding shops
	protected static void addItemLocation(String itemId, Shop shop)
	{
		if (itemLocations.containsKey(itemId))
		{
			itemLocations.get(itemId).put(shop.getName(), shop);
		}
		else
		{			
			itemLocations.put(itemId, new HashMap<>());
			itemLocations.get(itemId).put(shop.getName(), shop);
		}
	}
}
