package scenario.data.facility;

public class Facility {
	
	String 	name;
	long 	lat, 
			lon;
	
	public Facility(String name, long lat, long lon) {
		this.name 	= name;
		this.lat	= lat;
		this.lon	= lon;
	}
	
	public Facility(Facility facility) {
		this(facility.name, facility.lat, facility.lon);
	}

}
