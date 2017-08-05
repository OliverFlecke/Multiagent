package scenario.env.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scenario.data.facility.*;

public class FacilityInfo {
	
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

	private List<Map<String, ? extends Facility>> every = new ArrayList<>(
			Arrays.asList(chargingStations, dumps, shops, storages, workshops, resourceNodes));
	
	public void addFacility(Facility f) 
	{
		if (f instanceof ChargingStation)
		{
			chargingStations.put(f.getName(), (ChargingStation) f);
		}
		else if (f instanceof Dump)
		{
			dumps.put(f.getName(), (Dump) f);
		}
		else if (f instanceof Shop)
		{
			shops.put(f.getName(), (Shop) f);
		}
		else if (f instanceof Storage)
		{
			storages.put(f.getName(), (Storage) f);
		}
		else if (f instanceof Workshop)
		{
			workshops.put(f.getName(), (Workshop) f);
		}
		else if (f instanceof ResourceNode)
		{
			resourceNodes.put(f.getName(), (ResourceNode) f);
		}
		else throw new UnsupportedOperationException("Unsupported facility: " + f.getName());
	}

	public Facility getFacility(String type, String name) 
	{		
		Map<String, ? extends Facility> facilities = new HashMap<>();
		
		switch (type)
		{
		case CHARGING_STATION: 	facilities = chargingStations	;  break;	
		case DUMP:				facilities = dumps				;  break;        
		case SHOP:				facilities = shops				;  break;           
		case STORAGE:			facilities = storages			;  break;
		case WORKSHOP:			facilities = workshops			;  break;
		case RESOURCE_NODE:		facilities = resourceNodes		;  break;
		}
		
		return facilities.get(name);
	}
	
	public Facility getFacility(String name)
	{
		if (name == "none") return null;
		
		return every.stream().filter(type -> type.containsKey(name))
				.findFirst().get().get(name);
	}
}
