package scenario.info;

import java.util.Map;
import java.util.Map.Entry;

import cartago.Artifact;
import cartago.OPERATION;
import scenario.data.Entity;

public class AgentInfo extends Artifact {
	
	private static Map<String, AgentInfo> instances;

	private Entity 					entity;
	private int 					charge, 
									load;
	private String					facility;
	private Map<String, Integer> 	items;
	
	public static AgentInfo get(String agent) {
		return instances.get(agent);
	}

	public void init() {
		instances.put(getOpUserName(), this);
		
		defineObsProperty("charge"			, charge);
		defineObsProperty("load"			, load);
		defineObsProperty("facility"		, facility);		
//		defineObsProperty("lastAction"		, "");
//		defineObsProperty("lastActionResult", "");       
//		defineObsProperty("lastActionParam"	, "");
	}
	
	public void preprocess() {
		items.clear();
	}
	
	@OPERATION
	public void postprocess() {
		getObsProperty("charge"				).updateValue(charge);
		getObsProperty("load"				).updateValue(load);
		getObsProperty("facility"			).updateValue(facility);
//		getObsProperty("lastAction"			).updateValue(null);
//		getObsProperty("lastActionResult"	).updateValue(null);
//		getObsProperty("lastActionParam"	).updateValue(null);
	}
	
	public void perceiveCharge(int charge) {
		this.charge = charge;
	}
	
	public void perceiveLoad(int load) {
		this.load = load;
	}
	
	public void perceiveFacility(String facility) {
		this.facility = facility;
	}
	
	public void perceiveHasItem(Entry<String, Integer> item) {
		this.items.put(item.getKey(), item.getValue());
	}
	
	public void perceiveLat(double lat) {
		this.entity.setLat(lat);
	}
	
	public void perceiveLon(double lon) {
		this.entity.setLon(lon);
	}
}
