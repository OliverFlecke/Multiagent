package model;

public class ChargingStation extends Facility {

	private int chargingRate;
	
	public ChargingStation(String name, double longitude, double latitude, int chargingRate) {
		super(name, longitude, latitude);
		this.chargingRate = chargingRate;
	}

	public int getChargingRate()
	{
		return this.chargingRate;
	}
	
}
