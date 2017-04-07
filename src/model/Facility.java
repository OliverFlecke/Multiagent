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
	
	public String getName()
	{
		return this.name;
	}
	
	public double getLongitude()
	{
		return this.longitude;
	}
	
	public double getLatitue()
	{
		return this.latitude;
	}
}
