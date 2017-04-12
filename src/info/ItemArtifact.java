package info;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import eis.iilang.Percept;
import env.EIArtifact;
import env.Translator;
import jason.asSyntax.*;
import massim.scenario.city.data.*;
import massim.scenario.city.data.facilities.Shop;

public class ItemArtifact extends Artifact {
	
	private static final Logger logger = Logger.getLogger(ItemArtifact.class.getName());

	private static final String ITEM = "item";

	public static final Set<String>	PERCEPTS = Collections.unmodifiableSet(
		new HashSet<String>(Arrays.asList(ITEM)));

    private static Map<String, Tool> 		tools 			= new HashMap<>();
	private static Map<String, Item>		items 			= new HashMap<>();
	private static Map<String, Set<Shop>> 	itemLocations 	= new HashMap<>();	
	
	@OPERATION
	void getItems(OpFeedbackParam<Collection<Item>> ret) {
		ret.set(items.values());
	}
	
	@OPERATION
	void getBaseItem(String name, OpFeedbackParam<String[]> ret)
	{
		Item item = items.get(name);
		String[] baseItems = new String[item.getRequiredBaseItems().size()];
//		for (Item baseItem : item.getRequiredBaseItems())
//		{
//			
//		}
	
				
	}
	
	@OPERATION
	void getShopsSelling(String item, OpFeedbackParam<Collection<Shop>> ret) {
		ret.set(itemLocations.get(item));
	}
	
	@OPERATION
	void getClosestFacilitySelling(String item, OpFeedbackParam<String> ret)
	{
		Location agLoc = AgentArtifact.getEntity(getOpUserName()).getLocation();
		
		Collection<Shop> shops = itemLocations.get(item);
		
		ret.set(FacilityArtifact.getClosestFacility(agLoc, shops));
	}
	
	public static void perceiveInitial(Collection<Percept> percepts)
	{		
		Map<Item, Set<List<Term>>> requirements = new HashMap<>();
		
		percepts.stream().filter(percept -> percept.getName() == ITEM)
						 .forEach(item -> perceiveItem(item, requirements));
		
		// Item requirements has to be added after all items have been created, 
		// since they are not necessarily given in a chronological order.
		// TODO: Tools and items that require assembly have a volume of 0.
		for (Entry<Item, Set<List<Term>>> entry : requirements.entrySet())
		{
			Item item = entry.getKey();
			
			for (List<Term> partTuple : entry.getValue())
			{
				String itemId   = Translator.termToString(partTuple.get(0));
				int    quantity = Translator.termToInteger(partTuple.get(1));
				
				item.addRequirement(items.get(itemId), quantity);
			}
		}

		if (EIArtifact.LOGGING_ENABLED)
		{
			logger.info("Perceived items:");		
			for (Item item : items.values())
				logger.info(item.toString());
		}
	}

	// Literal(String, int, Literal(List<String>), Literal(List<List<String, int>>))
	private static void perceiveItem(Percept percept, Map<Item, Set<List<Term>>> requirements)
	{
		Term[] args = Translator.perceptToLiteral(percept).getTermsArray();
		
		String     id		= Translator.termToString(args[0]);
		int 	   volume	= Translator.termToInteger(args[1]);
		
		Item item = new Item(id, volume, 0, Collections.emptySet());

		for (Term toolArg : Translator.literalToTermToTermList(args[2]))
		{
			String toolId = Translator.termToString(toolArg);
			
			if (!tools.containsKey(toolId))
			{
				tools.put(toolId, new Tool(toolId, 0, 0));
			}				
			item.addRequiredTool(tools.get(toolId));
		}
		
		Set<List<Term>> parts = new HashSet<>();

		for (Term partArg : Translator.literalToTermToTermList(args[3]))
		{
			List<Term> partTuple = Translator.termToTermList(partArg);
			
			parts.add(partTuple);	
		}		
		items.put(id, item);
		requirements.put(item, parts);
	}
	
	// Used by the FacilityArtifact when adding items to shops.
	protected static Item getItem(String itemId)
	{
        if (items.containsKey(itemId)) 
        {
        	return items.get(itemId);
        }
        return tools.get(itemId);
	}
	
	// Used by the FacilityArtifact when adding shops
	protected static void addItemLocation(String itemId, Shop shop)
	{
		if (itemLocations.containsKey(itemId))
		{
			itemLocations.get(itemId).add(shop);
		}
		else
		{
			Set<Shop> shops = new HashSet<>(Arrays.asList(shop));
			
			itemLocations.put(itemId, shops);
		}
	}
}
