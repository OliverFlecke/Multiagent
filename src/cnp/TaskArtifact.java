package cnp;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.OPERATION;
import env.Translator;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.parser.ParseException;
import massim.scenario.city.data.AuctionJob;
import massim.scenario.city.data.facilities.Shop;

public class TaskArtifact extends Artifact {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());

	
	private static TaskArtifact instance;
	private static int 			cnpId;
	
	void init()
	{
		instance = this;
	}
	
	public static void announceJob(String taskId, String type) 
	{		
		instance.execInternalOp("announceJobOp", taskId, type); 
	}
	
	public static void announceAuction(String taskId, AuctionJob auction) 
	{
		instance.execInternalOp("announceAuction", taskId);		
	}
	
	public static void announceShops(Collection<Shop> shops)
	{
		Object shopNames = shops.stream().map(Shop::getName).toArray(String[]::new);
				
		instance.execInternalOp("announceShops", shopNames);
	}
	
	@OPERATION
	void announceJobOp(String taskId, String type)
	{
		instance.define("task", taskId, type);
	}
	
	@OPERATION
	void announceAuction(String taskId)
	{
		instance.announce("auction", taskId);
	}
	
	@OPERATION
	void announceShops(Object shops)
	{
		instance.announce("shops", shops);
	}
	
	@OPERATION
	void announceBuy(String item, String amount, String shop)
	{
		instance.announce("buyRequest", item, amount, shop);
	}
	
	@OPERATION
	void announceRetrieve(String agent, Object shoppingList, String workshop)
	{
		instance.announce("retrieveRequest", agent, toItemMap(shoppingList), workshop);
	}
	
	@OPERATION
	void announceAssemble(Object items, String workshop, String taskId, String deliveryLocation)
	{
		instance.announce("assembleRequest", items, workshop, taskId, deliveryLocation);
	}
	
	private void define(String property, Object... args)
	{
		defineObsProperty(property, args);
	}
	
	private void announce(String property, Object... args)
	{
		try 
		{
			String cnpName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(cnpName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			Object[] properties = new Object[args.length + 1];
			
			for (int i = 0; i < args.length; i++)
			{
				properties[i] = args[i];
			}
			properties[args.length] = cnpName;
			
			defineObsProperty(property, properties);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	@OPERATION
	void clearTask(String cnpName)
	{
		removeObsPropertyByTemplate("task", null, null); 
	}
	
	@OPERATION
	void clearShops(String cnpName)
	{
		removeObsPropertyByTemplate("shops", null, cnpName);
	}
	
	private static Object toItemMap(Object items)
	{
		if (items instanceof Map<?, ?>) return items;
		else
		{
			try 
			{
				ListTerm terms = ASSyntax.createList();
				
				for (Object item : (Object[]) items)
				{
					terms.add(ASSyntax.parseLiteral((String) item));
				}
				
				return Translator.termToObject(terms);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
