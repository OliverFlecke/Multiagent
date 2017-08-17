package mapc2017.env;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logger extends PrintStream {
	
	private static String OUTPUT = "output/output_" + System.currentTimeMillis() + ".txt";
	
	private static Logger instance;

	public Logger() throws FileNotFoundException {
		super(new BufferedOutputStream(new FileOutputStream(OUTPUT)), true);
	}
	
	public static void init() {
		try {
			instance = new Logger();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Logger get() {
		return instance;
	}
	

}
