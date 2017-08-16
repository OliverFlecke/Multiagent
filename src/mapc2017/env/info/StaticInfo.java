package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mapc2017.data.CityMap;
import mapc2017.data.Entity;
import mapc2017.data.Role;
import massim.scenario.city.data.Location;
import massim.scenario.city.util.GraphHopperManager;

public class StaticInfo {
	
	private static StaticInfo instance;
	public  static StaticInfo get() { return instance; }
	
	private String 				id, 
								map, 
								team;
	private int					steps;
	private long				seedCapital;
	private double				minLat, maxLat,
								minLon, maxLon;
	private Set<Entity>			entities 	= new HashSet<>();
	private Map<String, Role>	roles 		= new HashMap<>();
	private CityMap				cityMap;
	
	public StaticInfo() {
		instance = this;
	}
	
	public String getId() {
		return id;
	}
	
	public String getMap() {
		return map;
	}
	
	public String getTeam() {
		return team;
	}
	
	public long getSeedCapital() {
		return seedCapital;
	}
	
	public int getSteps() {
		return steps;
	}
	
	public Set<Entity> getTeamEntities() {
		return entities.stream()
				.filter(e -> e.getTeam().equals(team))
				.collect(Collectors.toSet());
	}
	
	public Role getRole(String role) {
		return roles.get(role);
	}
	
	public Collection<Role> getRoles() {
		return roles.values();
	}
	
	public int getRouteLength(Location from, Location to) {
		return cityMap.findRoute(from, to, GraphHopperManager.PERMISSION_ROAD)
				.getRouteLength();
	}
	
	public int getRouteDuration(AgentInfo agent, Location to) {
		return cityMap.findRoute(agent.getLocation(), to, agent.getPermission())
				.getRouteDuration(agent.getRole().getSpeed());
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
	
	public void setSeedCapital(long seedCapital) {
		this.seedCapital = seedCapital;
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}
	
	public void addRole(Role role) {
		this.roles.put(role.getName(), role);
	}
	
	public void initCityMap() {
		this.cityMap = new CityMap(map, 200, minLat, maxLat, minLon, maxLon);
	}
}
