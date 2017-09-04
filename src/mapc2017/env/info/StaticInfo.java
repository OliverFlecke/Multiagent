package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mapc2017.data.Entity;
import mapc2017.data.Role;
import mapc2017.data.RouteFinder;
import massim.scenario.city.data.Location;
import massim.scenario.city.util.GraphHopperManager;

public class StaticInfo {
	
	private static StaticInfo instance;
	public  static StaticInfo get() { return instance; }
	
	private String 				id, 
								map, 
								team;
	private int					steps, proximity;
	private long				seedCapital;
	private double				minLat, maxLat, minLon, maxLon,
								centerLat, centerLon, cellSize;
	private Map<String, Entity>	entities 	= new HashMap<>();
	private Map<String, Role>	roles 		= new HashMap<>();
	private RouteFinder			routeFinder;
	
	public StaticInfo() {
		instance = this;
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public synchronized String getId() {
		return id;
	}
	
	public synchronized String getMap() {
		return map;
	}
	
	public synchronized String getTeam() {
		return team;
	}
	
	public synchronized long getSeedCapital() {
		return seedCapital;
	}
	
	public synchronized int getSteps() {
		return steps;
	}
	
	public synchronized Location getCenter() {
		return new Location(centerLon, centerLat);
	}
	
	public synchronized Set<Entity> getTeamEntities() {
		return entities.values().stream()
				.filter(e -> e.getTeam().equals(team))
				.collect(Collectors.toSet());
	}
	
	public synchronized Role getRole(String role) {
		return roles.get(role);
	}
	
	public synchronized Collection<Role> getRoles() {
		return roles.values();
	}
	
	public synchronized int getRouteLength(Location from, Location to) {
		return routeFinder.findRoute(from, to, GraphHopperManager.PERMISSION_ROAD)
				.getRouteLength();
	}
	
	public synchronized int getRouteDuration(AgentInfo agent, Location to) {
		return routeFinder.findRoute(agent.getLocation(), to, agent.getPermission())
				.getRouteDuration(agent.getRole().getSpeed());
	}
	
	public synchronized Location getRandomLocation() {
		return routeFinder.getRandomCenterLocation();
	}
	
	/////////////
	// SETTERS //
	/////////////
	
	public synchronized void setId(String id) {
		this.id = id;
	}
	
	public synchronized void setMap(String map) {
		this.map = map;
	}
	
	public synchronized void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public synchronized void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public synchronized void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	public synchronized void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}

	public synchronized void setCenterLat(double centerLat) {
		this.centerLat = centerLat;
	}

	public synchronized void setCenterLon(double centerLon) {
		this.centerLon = centerLon;
	}

	public synchronized void setTeam(String team) {
		this.team = team;
	}
	
	public synchronized void setSeedCapital(long seedCapital) {
		this.seedCapital = seedCapital;
	}

	public synchronized void setCellSize(double cellSize) {
		this.cellSize = cellSize;
	}
	
	public synchronized void setSteps(int steps) {
		this.steps = steps;
	}

	public synchronized void setProximity(int proximity) {
		this.proximity = proximity;
	}
	
	public synchronized void addEntity(Entity entity) {
		this.entities.put(entity.getName(), entity);
	}
	
	public synchronized void addRole(Role role) {
		this.roles.put(role.getName(), role);
	}
	
	public synchronized void initCityMap() {
		this.routeFinder = new RouteFinder(map, cellSize, 
				minLat, maxLat, minLon, maxLon, 
				this.getCenter(), proximity);
	}
}
