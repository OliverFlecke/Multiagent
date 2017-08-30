package mas;

import jason.infra.centralised.RunCentralisedMAS;
import massim.Server;

public class Runner {

	public static void main(String[] args) throws Exception 
	{
		new Thread(new Runnable() {
			public void run() {
				Server.main(new String[] { "-conf", "conf/SampleConfigOld.json", "--monitor" });
			}
		}).start();
				
		RunCentralisedMAS.main(new String[] { "src/mas/multiagent_jason.mas2j" });
	}
}
