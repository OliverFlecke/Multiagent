package mapc2017.env;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logger extends PrintStream {
	
	private static Logger instance;

	public Logger() throws FileNotFoundException {
		super(new BufferedOutputStream(new FileOutputStream(getFileName())), true);
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
	
	private static String getFileName() {
		return String.format("output/output_%d.txt", System.currentTimeMillis());
	}
	

}
