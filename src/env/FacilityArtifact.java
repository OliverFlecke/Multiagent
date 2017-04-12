package env;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import jason.asSyntax.Term;
import massim.scenario.city.data.Location;
import massim.scenario.city.data.facilities.*;

public class FacilityArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(FacilityArtifact.class.getName());	

	private static final String CHARGING_STATION 	= "chargingStation";
	private static final String DUMP 				= "dump";
	private static final String SHOP 				= "shop";
	private static final String STORAGE 			= "storage";
	private static final String WORKSHOP 			= "workshop";
	private static final String RESOURCE_NODE		= "resourceNode";
	
	public static final Set<String>	STATIC_PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(CHARGING_STATION, DUMP, SHOP, STORAGE, WORKSHOP)));

	public static final Set<String>	DYNAMIC_PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(RESOURCE_NODE)));
	
	private static Map<String, ChargingStation> chargingStations 	= new HashMap<>();
	private static Map<String, Dump> 			dumps 			 	= new HashMap<>();
	private static Map<String, Shop> 			shops 				= new HashMap<>();
	private static Map<String, Storage> 		storages 			= new HashMap<>();
	private static Map<String, Workshop> 		workshops 			= new HashMap<>();
	private static Map<String, ResourceNode>	resourceNodes		= new HashMap<>();
	
	@OPERATION
	void getClosestFacility(String facilityType, OpFeedbackParam<String> ret)
	{
		// Get agent location, compute closest facility and return 
		// the facility's name
		
		Location agLoc = DynamicInfoArtifact.getEntity(getOpUserName()).getLocation();
		
		String closestFacility = null;
		double closestDistance = Double.MAX_VALUE;
		
		Collection<? extends Facility> facilities = Collections.emptySet();
		
		switch (facilityType)
		{
		case CHARGING_STATION: 	facilities = chargingStations	.values();	break;	
		case DUMP:				facilities = dumps				.values();  break;        
		case SHOP:				facilities = shops				.values();  break;           
		case STORAGE:			facilities = storages			.values();  break;
		case WORKSHOP:			facilities = workshops			.values();  break;
		case RESOURCE_NODE:		facilities = resourceNodes		.values();  break;
		}
		
		for (Facility facility : facilities)
		{
			// Compare locations
		}
		
		if (closestFacility != null)
			ret.set(closestFacility);
		
		// What happens if the feedback parameter is never set?
	}
	
	protected static void perceiveInitial(Collection<Percept> percepts)
	{
		logger.info("Perceiving facilities");
		
		for (Percept percept : percepts)
		{
			switch (percept.getName())
			{
			case CHARGING_STATION: 	perceiveChargingStation	(percept);	break;	
			case DUMP:				perceiveDump			(percept);  break;             
			case SHOP:				perceiveShop			(percept);  break;             
			case STORAGE:			perceiveStorage			(percept);  break;          
			case WORKSHOP:			perceiveWorkshop		(percept);  break;
			}
		}

		logFacilities("Charging station perceived:"	, chargingStations	.values());
		logFacilities("Dumps perceived:"			, dumps				.values());
		logFacilities("Shops perceived:"			, shops				.values());
		logFacilities("Storages perceived:"			, storages			.values());
		logFacilities("Workshops perceived:"		, workshops			.values());
	}
	
	private static void logFacilities(String msg, Collection<? extends Facility> facilities)
	{
		logger.info(msg);
		for (Facility facility : facilities)
			logger.info(facility.toString());
	}
	
	// Literal(String, double, double, int)
	private static void perceiveChargingStation(Percept percept) 
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name = Translator.termToString(args[0]);
		double lon 	= Translator.termToDouble(args[1]);
		double lat 	= Translator.termToDouble(args[2]);
		int rate 	= Translator.termToInteger(args[3]);
		
		chargingStations.put(name, new ChargingStation(name, new Location(lon, lat), rate));
	}

	// Literal(String, double, double)
	private static void perceiveDump(Percept percept) 
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name = Translator.termToString(args[0]);
		double lon 	= Translator.termToDouble(args[1]);
		double lat 	= Translator.termToDouble(args[2]);
		
		dumps.put(name, new Dump(name, new Location(lon, lat)));
	}

	// Literal(String, double, double, int, List<Literal(String, int, int)>)
	private static void perceiveShop(Percept percept) 
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name    = Translator.termToString(args[0]);
		double lon     = Translator.termToDouble(args[1]);
		double lat     = Translator.termToDouble(args[2]);
		int    restock = Translator.termToInteger(args[3]);
		
		Shop shop = new Shop(name, new Location(lon, lat), restock);
		
		for (Term itemLiteral : Translator.termToTermList(args[4])) 
		{
			Term[] itemArgs = Translator.termToLiteral(itemLiteral).getTermsArray();
			
			String itemId = Translator.termToString(itemArgs[0]);
			int price 	  = Translator.termToInteger(itemArgs[1]);
			int quantity  = Translator.termToInteger(itemArgs[2]);
			
			shop.addItem(ItemArtifact.getItem(itemId), quantity, price);	
			ItemArtifact.addItemLocation(itemId, shop);
		}		
		shops.put(name, shop);
	}

	// Literal(String, double, double, int)
	private static void perceiveStorage(Percept percept) 
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name  = Translator.termToString(args[0]);
		double lon 	 = Translator.termToDouble(args[1]);
		double lat 	 = Translator.termToDouble(args[2]);
		int capacity = Translator.termToInteger(args[3]);
		// Set<String> teamNames?
		
		storages.put(name, 
				new Storage(name, new Location(lon, lat), capacity, Collections.emptySet()));
	}

	// Literal(String, double, double)
	private static void perceiveWorkshop(Percept percept) 
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name = Translator.termToString(args[0]);
		double lon 	= Translator.termToDouble(args[1]);
		double lat 	= Translator.termToDouble(args[2]);
		
		workshops.put(name, new Workshop(name, new Location(lon, lat)));
	}
	
	// Literal(String, double, double, String)
	private static void perceiveResourceNode(Percept percept)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String name   = Translator.termToString(args[0]);
		double lon 	  = Translator.termToDouble(args[1]);
		double lat 	  = Translator.termToDouble(args[2]);
		String itemId = Translator.termToString(args[3]);
		
		resourceNodes.put(name, 
				new ResourceNode(name, new Location(lon, lat), ItemArtifact.getItem(itemId), 0));
	}

}
