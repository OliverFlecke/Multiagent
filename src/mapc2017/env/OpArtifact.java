package mapc2017.env;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.parser.ParseException;
import mapc2017.data.Item;
import mapc2017.data.Tool;
import mapc2017.data.facility.Facility;
import mapc2017.data.facility.Shop;
import mapc2017.env.info.AgentInfo;
import mapc2017.env.info.DynamicInfo;
import mapc2017.env.info.FacilityInfo;
import mapc2017.env.info.ItemInfo;
import mapc2017.env.info.StaticInfo;
import massim.scenario.city.data.Location;

public class OpArtifact extends Artifact {

	@OPERATION
	void getClosestFacility(String type, OpFeedbackParam<String> ret)
	{		
		ret.set(FacilityInfo.get().getFacilities(type).stream()
				.min(Comparator.comparingInt(f -> 
					StaticInfo.get().getRouteDuration(
						AgentInfo.get(getOpUserName()), 
						f.getLocation())))
				.get().getName());
	}
	
	@OPERATION
	void getClosestWorkshopToStorage(String storage, OpFeedbackParam<String> ret)
	{
		Location from = FacilityInfo.get().getFacility(storage).getLocation();

		ret.set(FacilityInfo.get().getFacilities("workshop").stream()
				.min(Comparator.comparingInt(f -> 
						StaticInfo.get().getRouteLength(
								from, 
								f.getLocation())))
				.get().getName());
	}
	
	@OPERATION
	void distanceToFacility(String name, OpFeedbackParam<Integer> ret)
	{
		AgentInfo 	aInfo = AgentInfo.get(getOpUserName());
		StaticInfo 	sInfo = StaticInfo.get();
		
		Facility facility = FacilityInfo.get().getFacility(name);
		
		ret.set(sInfo.getRouteDuration(aInfo, facility.getLocation()));
	}
	
	@OPERATION
	void incJobCompletedCount()
	{
		DynamicInfo.get().incJobsCompleted();
	}
	
	@OPERATION
	void getBaseItems(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(ItemInfo.get().getBaseItems(objectToStringMap(items)));
	}
	
	@OPERATION 
	void getRequiredItems(String item, OpFeedbackParam<Object> ret)
	{
		ret.set(ItemInfo.get().getItem(item).getReqItems());
	}
	
	@OPERATION
	void getRequiredTools(Object[] items, OpFeedbackParam<Object> ret)
	{		
		ret.set(objectToStringMap(items)
				.keySet().stream()
				.map(ItemInfo.get()::getItem)
				.map(Item::getReqBaseTools)
				.flatMap(Collection::stream)
				.toArray(String[]::new));
	}
	
	@OPERATION
	void getVolume(Object[] items, OpFeedbackParam<Integer> ret)
	{
		ret.set(ItemInfo.get().getVolume(objectToStringMap(items)));
	}
	
	@OPERATION
	void getToolVolume(String name, OpFeedbackParam<Integer> ret)
	{
		ret.set(ItemInfo.get().getItem(name).getVolume());
	}
	
	@OPERATION
	void getToolsVolume(Object[] tools, OpFeedbackParam<Integer> ret)
	{
		ret.set(Arrays.stream(tools)
				.map(String.class::cast)
				.map(ItemInfo.get()::getItem)
				.mapToInt(Item::getVolume).sum());
	}
	
	@OPERATION
	void sortByPermissionCount(Object[] objTools, OpFeedbackParam<Object> ret)
	{
		List<Tool> tools = Arrays.stream(objTools)
								.map(String.class::cast)
								.map(ItemInfo.get()::getItem)
								.map(Tool.class::cast)
								.collect(Collectors.toList());
		
		Collections.shuffle(tools);
		
		ret.set(tools.stream()
				.sorted((t1, t2) -> t1.getRoles().size() - t2.getRoles().size())
				.map(Tool::getName).toArray());
	}
	
	@OPERATION 
	void getBaseVolume(Object[] items, OpFeedbackParam<Integer> ret)
	{
		ItemInfo iInfo = ItemInfo.get();
		
		ret.set(iInfo.getBaseVolume(objectToStringMap(items)));
	}
	
	@OPERATION
	void getClosestShopSelling(String item, OpFeedbackParam<String> ret)
	{
		AgentInfo agent = AgentInfo.get(getOpUserName());

		ret.set(ItemInfo.get().getItemLocations(item).stream()
				.min(Comparator.comparingInt(shop -> StaticInfo.get().getRouteDuration(
						agent, 
						shop.getLocation())))
				.get().getName());
	}
	
	@OPERATION
	void getShoppingList(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(getShoppingList(objectToStringMap(items)));
	}
	
	@OPERATION
	void getAvailableAmount(String item, int quantity, String shopName, OpFeedbackParam<Integer> ret) 
	{
		Shop shop 	= (Shop) FacilityInfo.get().getFacility(shopName);
		
		ret.set(Math.min(quantity, shop.getAmount(item)));
	}
	
	@OPERATION
	void getItemsToCarry(Object[] items, int capacity, OpFeedbackParam<Object> retCarry, OpFeedbackParam<Object> retRest)
	{
		Map<String, Integer> carry 	= new HashMap<>();
		Map<String, Integer> rest	= new HashMap<>();

		for (Entry<String, Integer> entry : objectToStringMap(items).entrySet())
		{
			Item 	item 	= ItemInfo.get().getItem(entry.getKey());
			int 	amount 	= entry.getValue();
			int		volume	= capacity + 1;

			// Find base volume of the item
			if (item.getReqBaseItems().isEmpty()) 	
				 volume = item.getVolume();
			else volume = ItemInfo.get().getVolume(item.getReqBaseItems());

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
		Map<String, Integer> items 		= objectToStringMap(objItems);
		Map<String, Integer> inventory 	= objectToStringMap(objInventory);
		
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
		
		for (String tool : objectToStringArray(tools))
		{
			int volume = ItemInfo.get().getTool(tool).getVolume();
			
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
		Map<String, Integer> inventory 	= objectToStringMap(objInventory);

		for (String tool : objectToStringArray(tools))
		{
			if (!inventory.containsKey(tool)) missing.add(tool);
		}
		
		ret.set(missing.toArray(new String[missing.size()]));
	}
	
	@OPERATION
	void collectInventories(Object[] inventories, OpFeedbackParam<Object> ret)
	{
		ret.set(Arrays.stream(inventories).map(inv -> objectToStringMap((Object[]) inv).entrySet())
			.flatMap(Collection::stream).collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum)));
	}
	
	public Map<String, Map<String, Integer>> getShoppingList(Map<String, Integer> items)
	{	
		Map<String, Map<String, Integer>> shoppingList = new HashMap<>();
		
		for (Entry<String, Integer> entry : items.entrySet())
		{
			Collection<Shop> shops = ItemInfo.get().getItemLocations(entry.getKey());
			
			String 	item 	= entry.getKey();
			int 	amount 	= entry.getValue();
			
			Optional<Shop> shop = shops.stream()
					.filter(x -> x.getAmount(item) > amount).findAny();
			
			if (shop.isPresent())
			{
				addToMapOfMaps(shoppingList, shop.get().getName(), item, amount);
			}
			else 
			{
				int amountRemaining = amount;
				do
				{
					// If there is only one shop remaining, it should buy the rest
					if (shops.size() == 1)
					{
						addToMapOfMaps(shoppingList, shops.stream().findAny().get().getName(), item, amountRemaining);
						break;
					}
					
					// Find the shop with the largest number of the item
					shop = shops.stream().max((x, y) -> x.getAmount(item) - y.getAmount(item));
					
					if (shop.isPresent())
					{
						shops.remove(shop.get());
						
						int amountToBuy = Math.min(shop.get().getAmount(item), amountRemaining);
						
						amountRemaining -= amountToBuy;
						
						addToMapOfMaps(shoppingList, shop.get().getName(), item, amountToBuy);
					}
				}
				while (amountRemaining > 0);
			}
		}
		
		return shoppingList;
	}

	public static Map<String, Integer> objectToStringMap(Object[] objs)
	{
		Map<String, Integer> map = new HashMap<>();
		
		try 
		{		
			for (Object obj : objs) 
			{
				Literal literal	= ASSyntax.parseLiteral(obj.toString());	
				String 	item 	= literal.getTerm(0).toString().replaceAll("\"", "");				
				int 	amount 	= (int) ((NumberTerm) literal.getTerm(1)).solve();
	
				map.put(item, amount);
			}
		} 
		catch (NoValueException | ParseException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	public static String[] objectToStringArray(Object[] objs)
	{
		return Arrays.stream(objs).map(String.class::cast).toArray(String[]::new);
	}
	
	public static <A,B,C> void addToMapOfMaps(Map<A, Map<B, C>> map, A first, B second, C content) 
	{
		if (!map.containsKey(first))
		{
			map.put(first, new HashMap<B, C>());
		}
		map.get(first).put(second, content);
	}

}
