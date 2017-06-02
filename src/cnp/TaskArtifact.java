package cnp;

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
	
	public static void announceShops(Collection<Shop> shops)
	{
		Object shopNames = shops.stream().map(Shop::getName).toArray(String[]::new);
				
		instance.execInternalOp("announceShops", shopNames);
	}
	
	@OPERATION
	void announceJob(String taskId, String deliveryLocation, Object items, String type)
	{
		try 
		{
			String cnpName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(cnpName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("task", taskId, deliveryLocation, toItemMap(items), type, cnpName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	public static void announceAuction(String taskId, AuctionJob auction) {
		instance.execInternalOp("announceAuction", taskId);		
	}
	
	@OPERATION
	void announceAuction(String taskId)
	{
		try {
			String artifactName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("auction", taskId, artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}	
	}
	

	@OPERATION
	void announceShops(Object shops)
	{
		try 
		{
			String cnpName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(cnpName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("shops", shops, cnpName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	@OPERATION
	void clearTask(String artifactName)
	{
		removeObsPropertyByTemplate("task", null, null, null, null, artifactName); 
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
