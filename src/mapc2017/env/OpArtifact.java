package mapc2017.env;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import mapc2017.data.*;
import mapc2017.data.facility.*;
import mapc2017.env.info.*;
import massim.scenario.city.data.Location;
import massim.scenario.city.util.GraphHopperManager;
import util.CartagoUtil;
import util.DataUtil;

public class OpArtifact extends Artifact {
	
	private String getClosestFacility(Location from, 
			Collection<? extends Facility> facilities, String permission)
	{
		StaticInfo 	sInfo = StaticInfo.get();
		
		return facilities.stream().min(Comparator.comparingInt(f -> 
				sInfo.getRouteLength(
					from, 
					f.getLocation(), 
					permission))).get().getName();
	}

	@OPERATION
	void getClosestFacility(String type, OpFeedbackParam<String> ret)
	{		
		AgentInfo 	aInfo = AgentInfo.get(getOpUserName());
		
		Collection<? extends Facility> facilities = FacilityInfo.get().getFacilities(type);

		ret.set(getClosestFacility(aInfo.getLocation(), facilities, aInfo.getPermission()));
	}
	
	@OPERATION
	void getClosestWorkshopToStorage(String storage, OpFeedbackParam<String> workshop)
	{
		Location from = FacilityInfo.get().getFacility(storage).getLocation();
		
		Collection<? extends Facility> facilities = FacilityInfo.get().getFacilities("workshop");

		workshop.set(getClosestFacility(from, facilities, GraphHopperManager.PERMISSION_ROAD));
	}
	
	@OPERATION
	void distanceToFacility(String name, OpFeedbackParam<Integer> ret)
	{
		AgentInfo 	aInfo = AgentInfo.get(getOpUserName());
		StaticInfo 	sInfo = StaticInfo.get();
		
		Facility facility = FacilityInfo.get().getFacility(name);
		
		ret.set(sInfo.getRouteLength(aInfo.getLocation(), facility.getLocation(), aInfo.getPermission()));
	}
	
	@OPERATION
	void incJobCompletedCount()
	{
		DynamicInfo.get().incJobsCompleted();
	}
	
	@OPERATION
	void getBaseItems(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(ItemInfo.get().getBaseItems(CartagoUtil.objectToStringMap(items)));
	}
	
	@OPERATION 
	void getRequiredItems(String item, OpFeedbackParam<Object> ret)
	{
		ret.set(ItemInfo.get().getItem(item).getReqItems());
	}
	
	@OPERATION
	void getRequiredTools(Object[] items, OpFeedbackParam<Object> ret)
	{		
		ret.set(CartagoUtil.objectToStringMap(items)
				.keySet().stream()
				.map(ItemInfo.get()::getItem)
				.map(Item::getReqBaseTools)
				.flatMap(Collection::stream)
				.toArray(String[]::new));
	}
	
	@OPERATION
	void getVolume(Object[] items, OpFeedbackParam<Integer> ret)
	{
		ret.set(ItemInfo.get().getVolume(CartagoUtil.objectToStringMap(items)));
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
		
		ret.set(iInfo.getBaseVolume(CartagoUtil.objectToStringMap(items)));
	}
	
	@OPERATION
	void getClosestShopSelling(String item, OpFeedbackParam<String> ret)
	{
		AgentInfo agent = AgentInfo.get(getOpUserName());

		ret.set(ItemInfo.get().getItemLocations(item).stream()
				.min(Comparator.comparingInt(shop -> StaticInfo.get().getRouteLength(
						agent.getLocation(), 
						shop.getLocation(), 
						agent.getPermission())))
				.get().getName());
	}
	
	@OPERATION
	void getShoppingList(Object[] items, OpFeedbackParam<Object> ret)
	{
		ret.set(getShoppingList(CartagoUtil.objectToStringMap(items)));
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

		for (Entry<String, Integer> entry : CartagoUtil.objectToStringMap(items).entrySet())
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
				DataUtil.addToMapOfMaps(shoppingList, shop.get().getName(), item, amount);
			}
			else 
			{
				int amountRemaining = amount;
				do
				{
					// If there is only one shop remaining, it should buy the rest
					if (shops.size() == 1)
					{
						DataUtil.addToMapOfMaps(shoppingList, shops.stream().findAny().get().getName(), item, amountRemaining);
						break;
					}
					
					// Find the shop with the largest number of the item
					shop = shops.stream().max((x, y) -> x.getAmount(item) - y.getAmount(item));
					
					if (shop.isPresent())
					{
						shops.remove(shop.get());
						
						int amountToBuy = Math.min(shop.get().getAmount(item), amountRemaining);
						
						amountRemaining -= amountToBuy;
						
						DataUtil.addToMapOfMaps(shoppingList, shop.get().getName(), item, amountToBuy);
					}
				}
				while (amountRemaining > 0);
			}
		}
		
		return shoppingList;
	}

}
