package cnp;

import java.util.Comparator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.Artifact;
import cartago.ArtifactConfig;
import cartago.OPERATION;
import data.CUtil;
import env.Translator;
import info.JobArtifact;
import massim.scenario.city.data.Job;

public class TaskArtifact extends Artifact implements Comparator<Job> {

	private static final Logger logger = Logger.getLogger(TaskArtifact.class.getName());

	
	private static TaskArtifact instance;
	private static int 			cnpId;
	
	void init()
	{
		instance = this;
	}
	
	public static void announce(String taskId, String type) 
	{
		Job job = JobArtifact.getJob(taskId);
		
		instance.execInternalOp("announce", taskId, job.getStorage().getName(), CUtil.extractItems(job), type); 
	}
	
	public static void clear	(Object... args) { instance.execInternalOp("clear", 	args); }
	
	@OPERATION
	void announce(String taskId, String deliveryLocation, Object items, String type)
	{
		try {
			String artifactName = "CNPArtifact" + (++cnpId);
			
			makeArtifact(artifactName, "cnp.CNPArtifact", ArtifactConfig.DEFAULT_CONFIG);
			
			defineObsProperty("task", taskId, deliveryLocation, toItemMap(items), type, artifactName);
		} 
		catch (Throwable e) 
		{
			logger.log(Level.SEVERE, "Failure in announce: " + e.getMessage(), e);
		}		
	}
	
	private Object toItemMap(Object items)
	{
		if (items instanceof Map<?, ?>) return items;
		else return CUtil.toStringMap(Translator.convertASObjectToMap((Object[]) items));
	}
	
	@OPERATION
	void clear(String artifactName)
	{
		removeObsPropertyByTemplate("task", null, null, null, null, artifactName); 
	}
	
	private int evaluateJob(Job job)
	{
		int value = 0;
		
		
		
		return value;
	}

	@Override
	public int compare(Job job1, Job job2) {
		return evaluateJob(job1) - evaluateJob(job2);
	}
}
