package info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import massim.scenario.city.data.Item;
import massim.scenario.city.data.Role;
import massim.scenario.city.data.Tool;
import massim.scenario.city.data.facilities.Shop;
import util.CartagoUtil;
import util.DataUtil;

public class ItemArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(ItemArtifact.class.getName());

	private static final String ITEM = "item";

	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ITEM)));

	private static Map<String, Tool> 				tools 			= new HashMap<>();
	private static Map<String, Item>				items 			= new HashMap<>();
	private static Map<String, Map<String, Shop>> 	itemLocations 	= new HashMap<>();
	
	/* OPERATIONS */
	
	@OPERATION
	void getBaseItems(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(DataUtil.itemToStringMap(getBaseItems(CartagoUtil.objectToItemMap(items))));
	}
	
	@OPERATION 
	void getRequiredItems(String item, OpFeedbackParam<Object> ret)
	{
		ret.set(DataUtil.itemToStringMap(getItem(item).getRequiredItems()));
	}
	
	@OPERATION
	void getRequiredTools(Object[] items, OpFeedbackParam<Object> ret)
	{		
		ret.set(getRequiredTools(CartagoUtil.objectToItemMap(items))
				.stream().map(Tool::getName).toArray(String[]::new));
	}
	
	@OPERATION
	void getVolume(Object[] items, OpFeedbackParam<Integer> ret)
	{
		ret.set(getVolume(CartagoUtil.objectToItemMap(items)));
	}
	
	@OPERATION
	void getToolVolume(String tool, OpFeedbackParam<Integer> ret)
	{
		ret.set(getTool(tool).getVolume());
	}
	
	@OPERATION
	void getToolsVolume(Object[] tools, OpFeedbackParam<Integer> ret)
	{
		ret.set(getVolume(CartagoUtil.objectToToolArray(tools)));
	}
	
	@OPERATION
	void sortByPermissionCount(Object[] objTools, OpFeedbackParam<Object> ret)
	{
		List<Tool> tools = Arrays.stream(objTools)
								.map(String.class::cast)
								.map(ItemArtifact::getTool)
								.collect(Collectors.toList());
		
		Collections.shuffle(tools);
		
		ret.set(tools.stream()
				.sorted((t1, t2) -> t1.getRoles().size() - t2.getRoles().size())
				.map(Tool::getName).toArray());
	}
	
	@OPERATION 
	void getBaseVolume(Object[] items, OpFeedbackParam<Integer> ret)
	{
		ret.set(getBaseVolume(CartagoUtil.objectToItemMap(items)));
	}
	
	@OPERATION
	void getClosestShopSelling(String item, OpFeedbackParam<String> ret)
	{
		String agent = getOpUserName();

		ret.set(Collections.min(getShopSelling(item).stream()
				.collect(Collectors.toMap(Shop::getName, shop -> StaticInfoArtifact
									.getRoute(agent, shop.getLocation())
									.getRouteLength())).entrySet(), 
				Map.Entry.comparingByValue()).getKey());
	}
	
	@OPERATION
	void getShoppingList(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(DataUtil.shoppingListToString(getShoppingList(CartagoUtil.objectToItemMap(items))));
	}
	
	@OPERATION
	void getAvailableAmount(String itemName, int quantity, String shopName, OpFeedbackParam<Integer> ret) 
	{
		Item item 	= getItem(itemName);
		Shop shop 	= (Shop) FacilityArtifact.getFacility("shop", shopName);
		
		ret.set(Math.min(quantity, shop.getItemCount(item)));
	}
	
	@OPERATION
	void getItemsToCarry(Object[] items, int capacity, OpFeedbackParam<Object> retCarry, OpFeedbackParam<Object> retRest)
	{
		Map<String, Integer> carry 	= new HashMap<>();
		Map<String, Integer> rest	= new HashMap<>();

		for (Entry<Item, Integer> entry : CartagoUtil.objectToItemMap(items).entrySet())
		{
			Item 	item 	= entry.getKey();
			int 	amount 	= entry.getValue();
			int		volume	= capacity + 1;

			// Find base volume of the item
			if (item.getRequiredBaseItems().isEmpty()) 	
				 volume = item.getVolume();
			else volume = ItemArtifact.getVolume(item.getRequiredBaseItems());

			int amountToCarry = Math.min(amount, capacity / volume);
			
			capacity -= volume * amountToCarry;
			
			if (amountToCarry > 0) carry.put(item.getName(), amountToCarry);
			
			if (amount > amountToCarry) rest.put(item.getName(), amount - amountToCarry);
			
		}
		
		retCarry.set(carry);
		retRest.set(rest);
	}
	
	@OPERATION
	void getMissingItems(Object[] objItems, Object[] objInventory, OpFeedbackParam<Object> ret)
	{
		Map<String, Integer> items 		= CartagoUtil.objectToStringMap(objItems);
		Map<String, Integer> inventory 	= CartagoUtil.objectToStringMap(objInventory);
		
		for (Entry<String, Integer> inv : inventory.entrySet())
		{
			if (items.containsKey(inv.getKey()))
			{
				int hasAmount 	= inv.getValue();
				int needAmount	= items.get(inv.getKey());
				
				if (hasAmount >= needAmount) items.remove(inv.getKey());
				else						 items.put(inv.getKey(), needAmount - hasAmount);
			}
		}
		ret.set(items);
	}
	
	@OPERATION
	void getToolsToCarry(Object[] tools, int capacity, OpFeedbackParam<Object> retCarry, OpFeedbackParam<Object> retRest)
	{
		Set<String> carry 	= new HashSet<>();
		Set<String> rest	= new HashSet<>();
		
		for (String tool : CartagoUtil.objectToStringArray(tools))
		{
			int volume = getTool(tool).getVolume();
			
			if (capacity >= volume)
			{
				carry.add(tool);
				capacity -= volume;
			}
			else
			{
				rest.add(tool);
			}
		}
		
		retCarry.set(carry.toArray(new String[carry.size()]));
		retRest .set(rest .toArray(new String[rest .size()]));
	}
	
	@OPERATION
	void getMissingTools(Object[] tools, Object[] objInventory, OpFeedbackParam<String[]> ret)
	{
		Set<String>			 missing	= new HashSet<>();
		Map<String, Integer> inventory 	= CartagoUtil.objectToStringMap(objInventory);

		for (String tool : CartagoUtil.objectToStringArray(tools))
		{
			if (!inventory.containsKey(tool)) missing.add(tool);
		}
		
		ret.set(missing.toArray(new String[missing.size()]));
	}
	
	@OPERATION
	void collectInventories(Object[] inventories, OpFeedbackParam<Object> ret)
	{
		ret.set(Arrays.stream(inventories).map(inv -> CartagoUtil.objectToStringMap((Object[]) inv).entrySet())
			.flatMap(Collection::stream).collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum)));
	}
	
	public static int getVolume(Tool[] tools)
	{
		return Arrays.stream(tools).mapToInt(Tool::getVolume).sum();
	}
	
	public static Map<Item, Integer> getBaseItems(String item)
	{	
		return getItem(item).getRequiredBaseItems();
	}
	
	public static int getVolume(Entry<Item, Integer> item)
	{
		return item.getKey().getVolume() * item.getValue();
	}

	public static int getVolume(Map<Item, Integer> items)
	{
		return items.entrySet().stream().mapToInt(ItemArtifact::getVolume).sum();
	}
	
	public static int getBaseVolume(Map<Item, Integer> items)
	{
		return getVolume(getBaseItems(items));
	}
	
	public static Map<Item, Integer> getBaseItems(Map<Item, Integer> items)
	{
		return items.entrySet().stream()
				.map(item -> item.getKey().getRequiredBaseItems().entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, 
								entry -> entry.getValue() * item.getValue()))
						.entrySet())
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum));
	}
	
	public static Stream<Tool> getRequiredTools(Entry<Item, Integer> item)
	{
		Map<Item, Integer> reqItems = item.getKey().getRequiredItems();
		
		if (reqItems.isEmpty()) return Stream.empty();
		
		Set<Tool> reqTools = item.getKey().getRequiredTools();
		
		reqTools.addAll(getRequiredTools(reqItems));
		
		return reqTools.stream();
	}
	
	public static Set<Tool> getRequiredTools(Map<Item, Integer> items)
	{
		return items.entrySet().stream()
				.flatMap(ItemArtifact::getRequiredTools)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Converts a map of items to buy into a shopping list
	 * @param items The items to buy along with the amount
	 * @return A map of shops and what to buy where
	 */
	public static Map<Shop, Map<Item, Integer>> getShoppingList(Map<Item, Integer> items)
	{	
		Map<Shop, Map<Item, Integer>> shoppingList = new HashMap<>();
		
		for (Entry<Item, Integer> entry : items.entrySet())
		{
			Collection<Shop> shops = getShopSelling(entry.getKey().getName());
			
			Item item 	= entry.getKey();
			int amount 	= entry.getValue();
			
			Optional<Shop> shop = shops.stream()
					.filter(x -> x.getItemCount(item) > amount)
//					.findAny();
					.min((x, y) -> x.getPrice(item) - y.getPrice(item));
			
			if (shop.isPresent())
			{
				DataUtil.addToMapOfMaps(shoppingList, shop.get(), item, amount);
			}
			else 
			{
				int amountRemaining = amount;
				do
				{
					// If there is only one shop remaining, it should buy the rest
					if (shops.size() == 1)
					{
						DataUtil.addToMapOfMaps(shoppingList, shops.stream().sorted((x,y) -> x.getPrice(item) - y.getPrice(item)).findFirst().get(), item, amountRemaining);
						break;
					}
					
					// Find the shop with the largest number of the item
//					shop = shops.stream().max((x, y) -> x.getItemCount(item) - y.getItemCount(item));
					shop = shops.stream().min((x, y) -> x.getPrice(item) - y.getPrice(item));
					
					if (shop.isPresent())
					{
						shops.remove(shop.get());
						
						int amountToBuy = Math.min(shop.get().getItemCount(item), amountRemaining);
						
						amountRemaining -= amountToBuy;
						
						DataUtil.addToMapOfMaps(shoppingList, shop.get(), item, amountToBuy);
					}
				}
				while (amountRemaining > 0);
			}
		}
		
		return shoppingList;
	}
	
	/**
	 * @param item
	 * @return Get the best price for an item on the market
	 */
	public static int itemPrice(Item item)
	{
		Collection<Shop> shops = getShopSelling(item.getName());
		
		if (shops.isEmpty()) return 0;
		
		int bestPrice = Integer.MAX_VALUE;
		
		for (Shop shop : shops)
			bestPrice = bestPrice > shop.getPrice(item) ? shop.getPrice(item) : bestPrice;
		
		return bestPrice;
	}
	
	public static void perceiveInitial(Collection<Percept> percepts)
	{		
		Map<Item, Set<Object[]>> requirements = new HashMap<>();
		
		Map<Boolean, List<Percept>> groups = percepts.stream()
				.filter(percept -> percept.getName() == ITEM)
				.collect(Collectors.partitioningBy(p -> Translator.perceptToString(p).startsWith("tool", 5)));
		
		groups.get(true)	.forEach(tool -> perceiveTool(tool));
		groups.get(false)	.forEach(item -> perceiveItem(item, requirements));
		
		// Item requirements has to be added after all items have been created, 
		// since they are not necessarily given in a chronological order.
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
	
	private static void perceiveTool(Percept percept) 
	{
		Object[] args = Translator.perceptToObject(percept);
		
		String     id		= (String) args[0];
		int 	   volume	= (int)    args[1];

		Set<Role> roles = StaticInfoArtifact.getRoles().stream()
							.filter(role -> role.getPermissions().contains(id))
							.collect(Collectors.toSet());
		
		Tool tool = new Tool(id, volume, 0, roles.stream().map(Role::getName).toArray(String[]::new));
		
		roles.stream().forEach(role -> role.addTools(Arrays.asList(tool)));
		
		tools.put(id, tool);
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
				logger.log(Level.WARNING, "Tool not perceived: " + toolId);
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
        if (items.containsKey(itemId)) return items.get(itemId);
        return tools.get(itemId);
	}
	
	/**
	 * @param toolName Name of the tool
	 * @return The tool with the given name
	 */
	public static Tool getTool(String toolName)
	{
		return tools.get(toolName);
	}
	
	/**
	 * @param item The item for which a shop selling it should be found
	 * @return A collection of all the shops selling the given item
	 */
	public static Collection<Shop> getShopSelling(String item)
	{
		return itemLocations.get(item).values();
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

	public static void addToolPermession(String toolName, String role) 
	{
		if (tools.containsKey(toolName))
		{
			tools.get(toolName).getRoles().add(role);
		}
	}

	public static void reset() 
	{
		tools 			= new HashMap<>();
		items 			= new HashMap<>();
		itemLocations 	= new HashMap<>();
	}
}
