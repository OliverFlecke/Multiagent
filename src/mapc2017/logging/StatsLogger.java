package mapc2017.logging;

import java.io.File;
import java.io.FileNotFoundException;

import mapc2017.data.JobStatistics;
import mapc2017.env.info.DynamicInfo;

public class StatsLogger extends Logger 
{
	private static StatsLogger instance;
	
	public StatsLogger() throws FileNotFoundException 
	{
		super(getDirectory() + getFileName());
	}
	
	public static void reset() 
	{
		try 
		{
			if (instance != null) 
				instance.close();
			instance = new StatsLogger();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static StatsLogger get() 
	{
		if (instance == null) reset();
		return instance;
	}
	
	public static String getDirectory()
	{
		File path = new File("output/statistics");
		
		if (!path.isDirectory())
			path.mkdir();

		return path.getPath() + "/";
	}

	public static String getFileName()
	{
		return String.format("stats_%d.txt", System.currentTimeMillis());
	}
	
	public static void printStats() 
	{
		Logger logger = StatsLogger.get();
		
		logger.println("--- Statistics --- Step "	+ DynamicInfo.get().getStep());
		logger.println("Total jobs:             "  	+ JobStatistics.getTotalJobs());
		logger.println("Total jobs started:     "	+ JobStatistics.getTotalJobsStarted());
		logger.println("Total jobs completed:   " 	+ JobStatistics.getTotalJobsCompleted());
		logger.println("Currently active jobs:  "	+ JobStatistics.getActiveJobs());

		logger.println("Total auctions:			"	+ JobStatistics.getTotalAuctions());
		logger.println("Bids:					"	+ JobStatistics.getAuctionsBidOn());
		logger.println("Auctions won:			"	+ JobStatistics.getAuctionsWon());
		logger.println("Auctions completed:		"	+ JobStatistics.getAuctionsCompleted());
		logger.println("Auctions failed:		"	+ JobStatistics.getAuctionsFailed());
		logger.println("Auctions started:		"	+ JobStatistics.getAuctionsStarted());

		logger.println("Total missions:			"	+ JobStatistics.getTotalMissions());
		logger.println("Missions started:		"	+ JobStatistics.getMissionsStarted());
		logger.println("Missions completed:		"	+ JobStatistics.getMissionsCompleted());
		logger.println("Missions failed:		"	+ JobStatistics.getMissionsFailed());
		
		printSeparator();
		logger.println();
	}

	private static void printSeparator()
	{
		StatsLogger.get().println("--------------------------");
	}
}
