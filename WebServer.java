import java.io.IOException;

import org.apache.log4j.Logger;

public class WebServer {
	private static Logger log = Logger.getLogger(WebServer.class);

	public static String MyIP;
	public static int MyPort;

	public static boolean frontend = false;

	public static void main(String[] args) {
		if (args[0].equals("-f")) {
			MyPort = Integer.parseInt(args[1]);
			log.info("Starting frontend...initializing from " + args[2] + ":" + args[3]);
			
			SSM ssm = SSM.getInstance();
			
			ssm.initFrontend(args[2], Integer.parseInt(args[3]));
			
			frontend = true;
		} else {
			MyIP = args[0];
			MyPort = Integer.parseInt(args[1]);

			log.info("Starting physical server " + MyIP + ":" + MyPort);

			SSM ssm = SSM.getInstance();

			if (args.length > 2) {
				log.info("Joining network, connecting to " + args[2] + ":"
						+ args[3]);
				ssm.initFromServer(args[2], Integer.parseInt(args[3]));
			} else {
				log.info("First server, Initializing system");
				ssm.initSystem();
			}
		}
		Thread t = null;
		try {
			t = new HttpCoreRequestListener(MyPort);
		} catch (IOException e) {
		}
		t.setDaemon(false);
		t.start();
	}

}
