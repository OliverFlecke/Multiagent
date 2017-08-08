package mapc2017.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Tool extends Item {
	
	private Set<String> roles = new HashSet<>();

	public Tool(String name, int volume) {
		super(name, volume, Collections.emptySet(), Collections.emptyMap());
	}
	
	public void addRole(String role) {
		roles.add(role);
	}
	
	public Set<String> getRoles() {
		return new HashSet<>(roles);
	}

}
