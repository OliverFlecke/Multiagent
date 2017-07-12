package scenario.data.facility;

import java.util.Map;

public class Storage extends Facility {

	int						cap, used;
	Map<String, Integer>	stored;
	Map<String, Integer>	delivered;
	
	public Storage(Facility facility, int cap, int used, 
			Map<String, Integer> stored, Map<String, Integer> delivered) {
		super(facility);
		this.cap 		= cap;
		this.used		= used;
		this.stored		= stored;
		this.delivered	= delivered;
	}

}
