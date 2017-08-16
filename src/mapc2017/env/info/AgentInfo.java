package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mapc2017.data.Role;
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
