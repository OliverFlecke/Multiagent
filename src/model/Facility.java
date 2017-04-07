package model;

public abstract class Facility {

	private String name;
	
	private double longitude; 
	private double latitude;
	
	protected Facility(String name, double longitude, double latitude)
	{
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * @return The name of the facility
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @return Longitude of the facility
	 */
	public double getLongitude()
	{
		return this.longitude;
	}
	
	/**
	 * @return The latitude of the facility
	 */
	public double getLatitude()
	{
		return this.latitude;
	}
}
