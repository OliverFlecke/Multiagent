package cnp;

import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.OPERATION;

public class TaskArtifact extends Artifact {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());

	
	private static TaskArtifact instance;
	private static int 			cnpId;
	
	void init()
	{
		instance = this;
	}
	
	public static void announce	(Object... args) { instance.execInternalOp("announce", 	args); }	
	public static void clear	(Object... args) { instance.execInternalOp("clear", 	args); }
	
	@OPERATION
	void announce(String taskId, String deliveryLocation, Object items, String type)
	{
		try {
			String artifactName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("task", taskId, deliveryLocation, items, type, artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	@OPERATION
	void clear(String artifactName)
	{
		removeObsPropertyByTemplate("task", null, null, null, null, artifactName); 
	}
}
