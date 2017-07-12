package scenario.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Item {

	private String 					name;
	private int						volume;
	private Set<String> 			tools;
	private Map<String, Integer> 	parts;
	
	public Item(String name, int volume, Set<String> tools, Map<String, Integer> parts) {
		this.name 	= name;
		this.volume	= volume;
		this.tools	= tools;
		this.parts	= parts;
	}
	
	// GETTERS
	
	public String getName() {
		return name;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public Set<String> getTools() {
		return new HashSet<>(tools);
	}
	
	public Map<String, Integer> getParts() {
		return new HashMap<>(parts);
	}
}
