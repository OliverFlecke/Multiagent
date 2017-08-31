package mapc2017.data;

import java.util.Arrays;
import java.util.HashSet;

import massim.scenario.city.CityMap;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Route;

public class RouteFinder extends CityMap {
	
	private static final long serialVersionUID = 1L;

	public RouteFinder(String mapName, double cellSize, 
			double minLat, double maxLat, double minLon, double maxLon,
			Location center, int proximity) {
		super(mapName, cellSize, minLat, maxLat, minLon, maxLon, center);
		Location.setProximity(proximity);
	}
	
	public Route findRoute(Location from, Location to, String permission) {
		return super.findRoute(from, to, new HashSet<>(Arrays.asList(permission)));
	}
}
