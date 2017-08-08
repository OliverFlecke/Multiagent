package mapc2017.data.facility;

import massim.scenario.city.data.Location;

public class Facility {
	
	private String 	name;
	private double 	lat, 
					lon;
	
	public Facility(String name, double lat, double lon) {
		this.name 	= name;
		this.lat	= lat;
		this.lon	= lon;
	}
	
	public Facility(Facility facility) {
		this(facility.name, facility.lat, facility.lon);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Location getLocation() {
		return new Location(lon, lat);
	}
}
