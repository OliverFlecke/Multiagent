package cnp;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;

public class TaskArtifact extends Artifact {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());
	
	private static int cnpId;
	
	private static TaskArtifact instance;
	
	void init()
	{
		instance = this;
	}
	
	public static void announce(Object... args)
	{
		instance.execInternalOp("announce", args);
	}
	
	/**
	 * Announces a task by creating a CNPArtifact and defining it as an observable property.
	 * This will commence the bidding between agents.
	 * @param duration - Duration of the task.
	 * @param id - Name of the created CNPArtifact as feedback parameter.
	 */
	@INTERNAL_OPERATION
	void announce(String taskId)
	{
		try {
			String artifactName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("task", taskId, artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	@OPERATION
	void announce(Map<String, Integer> items, String deliveryLocation)
	{
		try {
			String artifactName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("task", items, deliveryLocation, artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
}
