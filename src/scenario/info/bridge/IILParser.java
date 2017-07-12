package scenario.info.bridge;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eis.iilang.*;
import scenario.data.*;
import scenario.data.facility.*;
import scenario.data.job.AuctionJob;
import scenario.data.job.Job;
import scenario.data.job.MissionJob;
import scenario.data.job.PostedJob;

public class IILParser {
	
	/***********************/
	/** PARAMETER METHODS **/
	/***********************/
	
	////////////
	// IILANG //
	////////////
	
	static Identifier parseIdentifier(Parameter p) {
		return (Identifier) p;
	}
	
	static Numeral parseNumeral(Parameter p) {
		return (Numeral) p;
	}
	
	static ParameterList parseParams(Parameter p) {
		return (ParameterList) p;
	}
	
	//////////////
	// JAVALANG //
	//////////////
	
	static String parseString(Parameter p) {
		return parseIdentifier(p).getValue();
	}
	
	static int parseInt(Parameter p) {
		return (int) parseNumeral(p).getValue();
	}
	
	static long parseLong(Parameter p) {
		return (long) parseNumeral(p).getValue();
	}
	
	static double parseDouble(Parameter p) {
		return (double) parseNumeral(p).getValue();
	}
	
	static Set<String> parseSet(Parameter p) {
		Set<String> set = new HashSet<>();
		for (Parameter pm : parseParams(p)) {
			set.add(parseString(pm));
		}
		return set;
	}
	
	static List<String> parseList(Parameter p) {
		List<String> list = new ArrayList<>();
		for (Parameter pm : parseParams(p)) {
			list.add(parseString(pm));
		}
		return list;
	}
	
	static Map<String, Integer> parseMap1(Parameter p) {
		Map<String, Integer> map = new HashMap<>();
		for (Parameter pm : parseParams(p)) {
			ParameterList 	entry = parseParams(pm);
			String 			key = parseString(entry.get(0));
			int 			value = parseInt(entry.get(1));
			map.put(key, value);
		}	
		return map;
	}
	
	static Map<String, Integer> parseMap2(Parameter p) {
		Map<String, Integer> map = new HashMap<>();
		for (Parameter pm : parseParams(p)) {
			ParameterList 	entry = parseParams(pm);
			String 			key = parseString(entry.get(0));
			int 			value = parseInt(entry.get(2));
			map.put(key, value);
		}	
		return map;
	}
	
	/*********************/
	/** PERCEPT METHODS **/
	/*********************/
	
	static String parseString(Percept p) {
		return parseString(p.getParameters().getFirst());
	}
	
	static int parseInt(Percept p) {
		return parseInt(p.getParameters().getFirst());
	}
	
	static long parseLong(Percept p) {
		return parseLong(p.getParameters().getFirst());
	}
	
	static double parseDouble(Percept p) {
		return parseDouble(p.getParameters().getFirst());
	}
	
	static List<String> parseList(Percept p) {
		return parseList(p.getParameters().getFirst());
	}
	
	/////////////
	// OBJECTS //
	/////////////
	
	static Entry<String, Integer> parseEntry(Percept p) {
		List<Parameter> params 	= p.getParameters();
		String 			name 	= parseString(params.get(0));
		int				amount 	= parseInt(params.get(1));
		return new AbstractMap.SimpleEntry<String, Integer>(name, amount);
	}
	
	static Role parseRole(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		String 					name 	= parseString	(params.get(0));
		int						speed 	= parseInt		(params.get(1));
		int						load	= parseInt		(params.get(2));
		int						battery = parseInt		(params.get(3));
		Set<String> 			tools 	= parseSet		(params.get(4));
		return new Role(name, speed, load, battery, tools);
	}
	
	static Item parseItem(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		String 					name 	= parseString	(params.get(0));
		int						volume 	= parseInt		(params.get(1));
		Set<String> 			tools 	= parseSet		(params.get(2));
		Map<String, Integer> 	parts 	= parseMap1		(params.get(3));
		return new Item(name, volume, tools, parts);
	}
	
	static Entity parseEntity(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		String 					name 	= parseString	(params.get(0));
		String 					team 	= parseString	(params.get(1));
		long					lat 	= parseLong		(params.get(2));
		long					lon 	= parseLong		(params.get(3));
		String					role 	= parseString	(params.get(4));
		return new Entity(name, team, lat, lon, role);		
	}
	
	////////////////
	// FACILITIES //
	////////////////
	
	static Facility parseFacility(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		String 					name 	= parseString	(params.get(0));
		long					lat 	= parseLong		(params.get(1));
		long					lon 	= parseLong		(params.get(2));
		return new Facility(name, lat, lon);
	}
	
	static ChargingStation parseChargingStation(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		int						rate	= parseInt		(params.get(3));
		return new ChargingStation(parseFacility(p), rate);
	}
	
	static Dump parseDump(Percept p) {
		return new Dump(parseFacility(p));
	}
	
	static Shop parseShop(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		int						restock	= parseInt		(params.get(3));
		Map<String, Integer>	price	= parseMap1		(params.get(4));
		Map<String, Integer>	amount	= parseMap2		(params.get(5));
		return new Shop(parseFacility(p), restock, price, amount);
	}
	
	static Storage parseStorage(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		int						cap		= parseInt		(params.get(3));
		int						used	= parseInt		(params.get(4));
		Map<String, Integer>	stored	= parseMap1		(params.get(4));
		Map<String, Integer>	delivrd	= parseMap2		(params.get(5));
		return new Storage(parseFacility(p), cap, used, stored, delivrd);
	}
	
	static Workshop parseWorkshop(Percept p) {
		return new Workshop(parseFacility(p));
	}
	
	static ResourceNode parseResourceNode(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		String					resrce	= parseString	(params.get(3));
		return new ResourceNode(parseFacility(p), resrce);
	}
	
	//////////
	// JOBS //
	//////////
	
	static Job parseJob(Percept p) {
		LinkedList<Parameter> 	params 	= p.getParameters();
		String					id		= parseString	(params.get(0));
		String					storage	= parseString	(params.get(1));
		int						reward	= parseInt		(params.get(2));
		long					start	= parseLong		(params.get(3));
		long					end		= parseLong		(params.get(4));
		Map<String, Integer>	items	= parseMap1		(params.getLast());
		return new Job(id, storage, reward, start, end, items);
	}
	
	static PostedJob parsePosted(Percept p) {
		return new PostedJob(parseJob(p));
	}
	
	static AuctionJob parseAuction(Percept p) {
		List<Parameter> 		params 	= p.getParameters();
		int						fine	= parseInt		(params.get(5));
		int						bid		= parseInt		(params.get(6));
		int						steps	= parseInt		(params.get(7));
		return new AuctionJob(parseJob(p), fine, bid, steps);
	}
	
	static MissionJob parseMission(Percept p) {
		return new MissionJob(parseAuction(p));
	}
	
}
