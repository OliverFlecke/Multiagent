package model;

import java.util.HashMap;
import java.util.Map;


public class Storage extends Facility {

	private int totalCapacity;
	private int usedCapacity;
	
	/*
	 * This is what the items field should represent: 
	 * <item delivered="3" name="item0" stored="0"/>
	 * Not sure what the stored element is
	 */
	private Map<String, Tuple<Integer>> items; 
	
	
	public Storage(String name, double longitude, double latitude, int totalCapacity) {
		super(name, longitude, latitude);
		this.totalCapacity = totalCapacity;
		this.usedCapacity = 0;
		this.items = new HashMap<String, Tuple<Integer>>();
	}
	
	public int getTotalCapacity()
	{
		return this.totalCapacity;
	}
	
	public int getUsedCapacity()
	{
		return this.usedCapacity;
	}
	
	public Map<String, Tuple<Integer>> getItems()
	{
		return this.items;
	}

	public void storeItem(String name, int stored)
	{
		if (this.items.containsKey(name))
		{
			this.items.get(name).setContent(1, stored);;
		}
		else
		{			
			this.items.put(name, new Tuple<Integer>(0, stored));
		}
	}
	
	public void retriveItem(String name, int quantity) 
	{
		Tuple<Integer> content = this.items.get("name");
	}
	
}
