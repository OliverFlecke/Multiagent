package cnp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.ArtifactId;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class TaskArtifact extends Artifact {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());

	
	private static TaskArtifact instance;
	private static int 			cnpId;
	
	void init()
	{
		instance = this;
	}
	
	public static void invoke(String method, Object... args)
	{
		instance.execInternalOp(method, args);
	}
	
	public static Bid delegateJob(String taskId, Map<String, Integer> items, String facility)
	{
		OpFeedbackParam<Bid> bid = new OpFeedbackParam<>();
		
		instance.execInternalOp("announceWithResult", "assembleRequest", bid, taskId, items, facility);
		
		return bid.get();
	}

	public static Bid delegateItems(String shop, Map<String, Integer> items, String workshop, String agent) 
	{
		OpFeedbackParam<Bid> bid = new OpFeedbackParam<>();
		
		instance.execInternalOp("announceWithResult", "retrieveRequest", bid, shop, items, workshop, agent);
		
		return bid.get();
	}
	
	public static Bid announceWithResult(String property, Object... args)
	{
		OpFeedbackParam<Bid> bid = new OpFeedbackParam<>();
		
		instance.execInternalOp("announceWithResult", property, bid, args);
		
		return bid.get();
	}
	
	@OPERATION
	private void announceWithResult(String property, OpFeedbackParam<Bid> bid, Object... args)
	{
		try 
		{
			ArtifactId id = instance.announce(property, args);		
			
			execLinkedOp(id, "getBid", bid);			
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announceWithResult: " + e.getMessage(), e);
		}
	}

	@OPERATION
	private ArtifactId announce(String property, Object... args)
	{
		try 
		{
			String cnpName = "CNPArtifact" + (++cnpId);
			
			ArtifactId id = makeArtifact(cnpName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			List<Object> properties = new LinkedList<Object>(Arrays.asList(args));
			
			properties.add(id);
			
			defineObsProperty(property, properties.toArray());
			
			return id;
		}
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
			return null;
		}
	}
	
	@OPERATION
	private void clear(String property, int argc, Object cnpId) 
	{
		Object[] args = new Object[argc];
		
		args[args.length - 1] = cnpId;
		
		clear(property, args);
	}
	
	@OPERATION
	private void clear(String property, Object... args)
	{
		try 
		{
			ArtifactId cnpId = (ArtifactId) args[args.length - 1];
			
			execLinkedOp(cnpId, "disposeArtifact");
			
			removeObsPropertyByTemplate(property, args);			
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in clear: " + e.getMessage(), e);
		}
	}
}
