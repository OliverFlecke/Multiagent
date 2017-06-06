package cnp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.OPERATION;
import data.CUtil;
import env.Translator;
import info.JobArtifact;
import massim.scenario.city.data.AuctionJob;
import massim.scenario.city.data.Job;
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
		Job job = JobArtifact.getJob(taskId);
		
		instance.execInternalOp("announceJob", taskId, job.getStorage().getName(), CUtil.extractItems(job), type); 
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
	void announceJob(String taskId, String deliveryLocation, Object items, String type)
	{
		instance.announce("task", taskId, deliveryLocation, toItemMap(items), type);
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
	
	private void announce(String property, Object... args)
	{
		try 
		{
			String cnpName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(cnpName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty(property, args, cnpName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	@OPERATION
	void clearTask(String cnpName)
	{
		removeObsPropertyByTemplate("task", null, null, null, null, cnpName); 
	}
	
	@OPERATION
	void clearShops(String cnpName)
	{
		removeObsPropertyByTemplate("shops", null, cnpName);
	}
	
	private static Object toItemMap(Object items)
	{
		if (items instanceof Map<?, ?>) return items;
		else return CUtil.toStringMap(Translator.convertASObjectToMap((Object[]) items));
	}
}
