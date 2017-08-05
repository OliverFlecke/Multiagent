package scenario.env.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import massim.scenario.city.data.Location;

public class AgentInfo {
	
	private double					lat,
									lon;
	private int 					charge, 
									load;
	private String					facility;
	private Map<String, Integer> 	inventory = new HashMap<>();

	public Location getLocation() {
		return new Location(lat, lon);
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
	
	public Map<String, Integer> getItems() {
		return new HashMap<>(inventory);
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
	
	public void clearInventory() {
		this.inventory.clear();
	}
	
	public void addItem(Entry<String, Integer> item) {
		this.inventory.put(item.getKey(), item.getValue());
	}	
}
