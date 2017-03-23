package RunProgram;

import massim.competition2015.monitor.GraphMonitor;
import massim.server.Server;
import massim.test.InvalidConfigurationException;

public class RunServer  {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				GraphMonitor.main(new String[] { "-rmihost", "localhost", "-rmiport", "1099" });
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					Server.main(new String[] { "--conf", "conf/2016-r-random-conf.xml" });
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		// Run the our program, which then can connect to the server.
		ConnectToServer.main(new String[0]);
	}

}
