package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mapc2017.data.Role;
import mapc2017.data.item.Item;
import mapc2017.data.item.ItemList;
import mapc2017.data.item.Tool;
import massim.scenario.city.data.Location;
import massim.scenario.city.util.GraphHopperManager;

public class AgentInfo {
	
	private static Map<String, AgentInfo> instances = new HashMap<>();	
	public  static AgentInfo get(String agent) { return instances.get(agent); }	
	public  static Collection<AgentInfo> get() { return instances.values(); }
	
	private double					lat,
									lon;
	private int 					charge, 
									load;
	private String					name,
									facility,
									lastAction,
									lastActionResult;
	private String[]				lastActionParams;
	private Role					role;
	private Map<String, Integer> 	inventory = new HashMap<>();
	
	public AgentInfo(String name) { 
		instances.put(name, this); 
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Location getLocation() {
		return new Location(lon, lat);
	}
	
	public int getCharge() {
		return charge;
	}
	
	public int getLoad() {
		return load;
	}
	
	public String getFacility() {
		return facility;
	}
	
	public String getLastAction() {
		return lastAction;
	}

	public String getLastActionResult() {
		return lastActionResult;
	}

	public String[] getLastActionParams() {
		return lastActionParams;
	}
	
	public Role getRole() {
		return role;
	}
	
	public String getPermission() {
		return role.getName().equals("drone") ? 
			GraphHopperManager.PERMISSION_AIR : 
			GraphHopperManager.PERMISSION_ROAD;
	}

	public Map<String, Integer> getInventory() {
		return new HashMap<>(inventory);
	}
	
	public int getCapacity() {
		return role.getLoad() - load;
	}
	
	public Set<String> getUsableTools(Collection<String> tools) {
		Set<String> useableTools = new HashSet<>(tools);
		useableTools.retainAll(role.getTools());
		return useableTools;
	}

	public ItemList getMissingItems(Map<String, Integer> items) {
		ItemList missing = new ItemList(items);		
		missing.subtract(inventory);		
		return missing;
	}
	
	public Set<String> getMissingTools(Collection<String> tools) 
	{
		Set<String> missing = new HashSet<>(tools);
		
		missing.removeAll(inventory.keySet());
		
		return missing;
	}

	public ItemList getItemsToCarry(Map<String, Integer> items)
	{		
		ItemList missing = new ItemList(items);
		missing.subtract(this.getInventory());
		
		ItemList itemsToCarry = new ItemList(items);
		itemsToCarry.subtract(missing);
		
		int capacity = this.getCapacity();

		for (Entry<Item, Integer> entry : ItemInfo.get().stringToItemMap(missing).entrySet())
		{
			Item 	item 	= entry.getKey();
			int 	amount 	= entry.getValue();
			int		volume	= item.getReqBaseVolume();

			int amountToCarry = Math.min(amount, capacity / volume);			
			
			if (amountToCarry > 0) 
			{
				if (item instanceof Tool)
				{
					if (!((Tool) item).getRoles().contains(role.getName()))
					{
						continue;
					}
				}
				
				capacity -= volume * amountToCarry;
				
				itemsToCarry.add(item.getName(), amountToCarry);
			}
		}
		return itemsToCarry;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public void setCharge(int charge) {
		this.charge = charge;
	}
	
	public void setLoad(int load) {
		this.load = load;
	}
	
	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public void setLastActionResult(String lastActionResult) {
		this.lastActionResult = lastActionResult;
	}

	public void setLastActionParams(String[] lastActionParams) {
		this.lastActionParams = lastActionParams;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}

	public void clearInventory() {
		this.inventory.clear();
	}
	
	public void addItem(Entry<String, Integer> item) {
		this.inventory.put(item.getKey(), item.getValue());
	}	
	
	@Override
	public String toString() {
		return name;
	}
}
