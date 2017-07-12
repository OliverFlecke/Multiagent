package scenario.data;

import java.util.Set;

public class Role {
	
	String 		name;
	int 		speed, 
				load, 
				battery;
	Set<String> tools;
	
	public Role(String name, int speed, int load, int battery, Set<String> tools) {
		this.name		= name;
		this.speed		= speed;
		this.load		= load;
		this.battery	= battery;
		this.tools		= tools;
	}

}
