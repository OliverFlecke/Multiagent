package scenario.data.facility;

public class ChargingStation extends Facility {
	
	int rate;
	
	public ChargingStation(Facility facility, int rate) {
		super(facility);
		this.rate = rate;
	}

}
