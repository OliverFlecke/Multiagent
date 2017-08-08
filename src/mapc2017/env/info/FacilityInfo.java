package mapc2017.env.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mapc2017.data.facility.ChargingStation;
import mapc2017.data.facility.Dump;
import mapc2017.data.facility.Facility;
import mapc2017.data.facility.ResourceNode;
import mapc2017.data.facility.Shop;
import mapc2017.data.facility.Storage;
import mapc2017.data.facility.Workshop;

public class FacilityInfo {
	
	private static FacilityInfo instance;
	
	public FacilityInfo() {	
		instance = this; 
	}
	
	public static FacilityInfo get() { 
		return instance; 
	}
	
	private static final String CHARGING_STATION 	= "chargingStation";
	private static final String DUMP 				= "dump";
	private static final String SHOP 				= "shop";
	private static final String STORAGE 			= "storage";
	private static final String WORKSHOP 			= "workshop";
	private static final String RESOURCE_NODE		= "resourceNode";	
	
	private Map<String, ChargingStation> chargingStations 	= new HashMap<>();
	private Map<String, Dump> 			 dumps 			 	= new HashMap<>();
	private Map<String, Shop> 			 shops 				= new HashMap<>();
	private Map<String, Storage> 		 storages 			= new HashMap<>();
	private Map<String, Workshop> 		 workshops 			= new HashMap<>();
	private Map<String, ResourceNode>	 resourceNodes		= new HashMap<>();

	public void addFacility(Facility f) 
	{
			 if (f instanceof ChargingStation) chargingStations.put(f.getName(), (ChargingStation) f);
		else if (f instanceof Dump           ) dumps           .put(f.getName(), (Dump           ) f);
		else if (f instanceof Shop           ) shops           .put(f.getName(), (Shop           ) f);
		else if (f instanceof Storage        ) storages        .put(f.getName(), (Storage        ) f);
		else if (f instanceof Workshop       ) workshops       .put(f.getName(), (Workshop       ) f);
		else if (f instanceof ResourceNode   ) resourceNodes   .put(f.getName(), (ResourceNode   ) f);
		else throw new UnsupportedOperationException("Unsupported facility: " + f.getName());
	}

	public Facility getFacility(String name) 
	{		
			 if (name.startsWith(CHARGING_STATION)) return chargingStations.get(name);
		else if (name.startsWith(DUMP            )) return dumps           .get(name);
		else if (name.startsWith(SHOP            )) return shops           .get(name);
		else if (name.startsWith(STORAGE         )) return storages        .get(name);
		else if (name.startsWith(WORKSHOP        )) return workshops       .get(name);
		else if (name.startsWith(RESOURCE_NODE   )) return resourceNodes   .get(name);
		else throw new UnsupportedOperationException("Unknown facility: " + name);
	}
	
	public Collection<? extends Facility> getFacilities(String type)
	{
			 if (type.equals(CHARGING_STATION)) return chargingStations.values();	
		else if (type.equals(DUMP			 ))	return dumps	       .values();        
		else if (type.equals(SHOP			 ))	return shops	       .values();           
		else if (type.equals(STORAGE		 )) return storages	       .values();
		else if (type.equals(WORKSHOP		 ))	return workshops       .values();
		else if (type.equals(RESOURCE_NODE	 ))	return resourceNodes   .values();
		else throw new UnsupportedOperationException("Unknown type: " + type);
	}
	
	public Collection<Shop> getShops()
	{
		return shops.values();
	}
}
