package model;

import java.util.Map;
import java.util.Set;

public class Item {
	String name;
	int volume;
	Set<Tool> tools;
	Map<Item, Integer> parts;
	
	public Item(String name, int volume, Set<Tool> tools, Map<Item, Integer> parts)
	{
		this.name   = name;
		this.volume = volume;
		this.tools  = tools;
		this.parts  = parts;
	}	
	
	
}
