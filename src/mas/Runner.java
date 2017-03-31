package mas;
import jason.JasonException;
import jason.infra.centralised.RunCentralisedMAS;
import massim.competition2015.monitor.GraphMonitor;
import massim.server.Server;
import massim.test.InvalidConfigurationException;

public class Runner {

	public static void main(String[] args) throws JasonException {
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
		
		RunCentralisedMAS.main(new String[] { "src/mas/multiagent_jason.mas2j" });
	}
}
