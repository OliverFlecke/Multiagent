package cnp;

import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import jason.asSyntax.Literal;

public class TaskArtifact extends Artifact {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());
	
	private static int taskId;
	
	private static TaskArtifact instance;
	
	void init()
	{
		instance = this;
	}
	
	public static TaskArtifact getInstance() {
		return instance;
	}
	
	/**
	 * Announces a task by creating a CNPArtifact and defining it as an observable property.
	 * This will commence the bidding between agents.
	 * @param duration - Duration of the task.
	 * @param id - Name of the created CNPArtifact as feedback parameter.
	 */
	public void announce(String task, int duration)
	{
		try {
			String artifactName = "CNPArtifact" + (++taskId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", new ArtifactConfig(duration));
			
			defineObsProperty("task", Literal.parseLiteral(task), artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announceTask: " + e.getMessage(), e);
		}		
	}
	
	/**
	 * Removes a previously announced task from the Artifact.
	 * @param artifactName - Name of the CNPArtifact
	 */
	@OPERATION
	void clear(String artifactName)
	{
		removeObsPropertyByTemplate("task", artifactName);
	}
}
