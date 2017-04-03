package mas;
import jason.JasonException;
import jason.infra.centralised.RunCentralisedMAS;
import massim.Server;

public class Runner {

	public static void main(String[] args) throws JasonException {
//		new Thread(new Runnable() {
//			public void run() {
//				GraphMonitor.main(new String[] { "-rmihost", "localhost", "-rmiport", "1099" });
//			}
//		}).start();

		new Thread(new Runnable() {
			public void run() {
//				Server.main(new String[] { "--conf", "conf/2016-r-random-conf.xml" });
				Server.main(new String[] { "-conf", "conf/SampleConfig.json" });
			}
		}).start();
		
		RunCentralisedMAS.main(new String[] { "src/mas/multiagent_jason.mas2j" });
	}
}
