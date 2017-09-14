package mapc2017.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logger extends PrintStream {
	
	private static Logger instance;

	public Logger(String fileName) throws FileNotFoundException 
	{
		super(new BufferedOutputStream(new FileOutputStream(fileName)), true);
	}
	
	public Logger() throws FileNotFoundException 
	{
		this(getDirectory() + getFileName());
	}
	
	public static void reset() {
		try {
			if (instance != null) 
				instance.close();
			instance = new Logger();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Logger get() {
		if (instance == null) reset();
		return instance;
	}
	
	public static String getDirectory()
	{
		File path = new File("output/");
		
		if (!path.isDirectory())
			path.mkdir();

		return path.getPath() + "/";
	}
	
	public static String getFileName() {
		return String.format("output_%d.txt", System.currentTimeMillis());
	}
	

}
