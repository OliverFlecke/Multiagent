package scenario.data.facility;

import massim.scenario.city.data.Location;

public class Facility {
	
	private String 	name;
	private long 	lat, 
					lon;
	
	public Facility(String name, long lat, long lon) {
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
		return new Location(lat, lon);
	}
}
