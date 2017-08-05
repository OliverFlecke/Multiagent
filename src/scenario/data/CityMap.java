package scenario.data;

import java.util.Iterator;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

import massim.protocol.scenario.city.util.LocationUtil;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.Route;
import massim.scenario.city.util.GraphHopperManager;
import massim.util.Log;

public class CityMap {
	
	private double 	cellSize,
					minLat,	maxLat,
					minLon,	maxLon;

	public CityMap(String mapName, double cellSize, 
			double minLat, double maxLat, 
			double minLon, double maxLon) 
	{
		this.cellSize 	= cellSize;
		this.minLon 	= minLon;
		this.maxLon 	= maxLon;
		this.minLat 	= minLat;
		this.maxLat 	= maxLat;
		GraphHopperManager.init(mapName);
	}
	
	public Route findRoute(Location from, Location to, String permission)
	{
		if(from == null || to == null) return null;
		if(!isInBounds(to)) return null; // target must be in bounds
		if (permission.equals(GraphHopperManager.PERMISSION_AIR))
			return getNewAirRoute(from, to);
		if (permission.equals(GraphHopperManager.PERMISSION_ROAD) && existsRoute(to, from))
			return getNewCarRoute(from, to);
		Log.log(Log.Level.ERROR, "Cannot find a route with those permissions");
		return null;
	}
	
	private Route getNewAirRoute(Location from, Location to)
	{
		Route route = new Route();
		double fractions = getLength(from, to) / cellSize;
		Location loc = null;
		for (long i = 1; i <= fractions; i++) {
			loc = getIntermediateLoc(from, to, fractions, i);
			route.addPoint(loc);
		}
		if (!to.equals(loc)) { route.addPoint(to); }
		return route;
	}

	private Route getNewCarRoute(Location from, Location to)
	{
		GHResponse rsp = queryGH(from, to);
		if(rsp.hasErrors()) return null;

		Route route = new Route();

		// points, distance in meters and time in millis of the full path
		PointList pointList = rsp.getBest().getPoints();
		Iterator<GHPoint3D> pIterator = pointList.iterator();
		if (!pIterator.hasNext()) return null;
		GHPoint prevPoint = pIterator.next();

		double remainder = 0;
		Location loc = null;
		while (pIterator.hasNext()){
			GHPoint nextPoint = pIterator.next();
			double length = getLength(prevPoint, nextPoint);
			if (length == 0){
				prevPoint = nextPoint;
				continue;
			}

			long i = 0;
			for (; i * cellSize + remainder < length ; i++) {
				loc = getIntermediateLoc(prevPoint, nextPoint, length, i * cellSize + remainder);
				if (!from.equals(loc)) {
					route.addPoint(loc);
				}
			}
			remainder = i * cellSize + remainder - length;
			prevPoint = nextPoint;
		}

		if (!to.equals(loc)) { route.addPoint(to); }

		return route;
	}
	
	private GHResponse queryGH(Location from, Location to)
	{
		GHRequest req = new GHRequest(from.getLat(), from.getLon(), to.getLat(), to.getLon())
				.setWeighting("shortest")
				.setVehicle("car");
		return GraphHopperManager.getHopper().route(req);
	}
	
	private boolean existsRoute(Location from, Location to) 
	{
		GHResponse rsp = queryGH(from, to);
		rsp.getErrors().forEach(error -> System.out.println(error.getMessage()));
		return !rsp.hasErrors() && rsp.getBest().getPoints().size() > 0;
	}

	/**
	 * @param loc the location to check
	 * @return true if the location is within map bounds
	 */
	private boolean isInBounds(Location loc)
	{
		return loc.getLat() > minLat && loc.getLat() < maxLat && loc.getLon() > minLon && loc.getLon() < maxLon;
	}
	
	private double getLength(Location loc1, Location loc2) 
	{
		return LocationUtil.calculateRange(loc1.getLat(), loc1.getLon(), loc2.getLat(), loc2.getLon());
	}
	private Location getIntermediateLoc(Location loc1, Location loc2, double fractions, long i) 
	{
		double lon = (loc2.getLon() - loc1.getLon())*i/fractions + loc1.getLon();
		double lat = (loc2.getLat() - loc1.getLat())*i/fractions + loc1.getLat();
		return new Location(lon,lat);
	}

	private double getLength(GHPoint loc1, GHPoint loc2) 
	{
		return LocationUtil.calculateRange(loc1.getLat(), loc1.getLon(), loc2.getLat(), loc2.getLon());
	}

	private Location getIntermediateLoc(GHPoint loc1, GHPoint loc2, double length, double i) 
	{
		double lon = (loc2.getLon() - loc1.getLon())*i/length + loc1.getLon();
		double lat = (loc2.getLat() - loc1.getLat())*i/length + loc1.getLat();
		return new Location(lon,lat);
	}
}
