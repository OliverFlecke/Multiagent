package scenario.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import massim.scenario.city.data.Location;
import scenario.data.CityMap;
import scenario.data.Role;

public class StaticInfo {
	
	private String 				id, 
								map, 
								team;
	private int					seedCapital, 
								steps;
	private double				minLat, maxLat,
								minLon, maxLon;
	private Map<String, Role>	roles = new HashMap<>();
	private CityMap				cityMap;
	
	public String getId() {
		return id;
	}
	
	public String getMap() {
		return map;
	}
	
	public String getTeam() {
		return team;
	}
	
	public int getSeedCapital() {
		return seedCapital;
	}
	
	public int getSteps() {
		return steps;
	}
	
	public Collection<Role> getRoles() {
		return roles.values();
	}
	
	public int getRouteLength(Location from, Location to, String permission) {
		return cityMap.findRoute(from, to, permission).getRouteLength();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setMap(String map) {
		this.map = map;
	}
	
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	
	public void setSeedCapital(int seedCapital) {
		this.seedCapital = seedCapital;
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public void addRole(Role role) {
		this.roles.put(role.getName(), role);
	}
	
	public void initCityMap() {
		this.cityMap = new CityMap(map, 200, minLat, maxLat, minLon, maxLon);
	}
}
