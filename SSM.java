import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class SSM {
	private static Logger log = Logger.getLogger(SSM.class);
	private static SSM instance = null;

	private Server me = null;
	private int myId;
	private ArrayList<Server> physicalNodes = null;
	private int[] serverMap = null;

	private static final int MAX_VIRTUAL = 256;

	public static SSM getInstance() {
		if (instance == null) {
			synchronized (SSM.class) {
				if (instance == null) {
					instance = new SSM();
				}
			}
		}
		return instance;
	}

	public void initSystem() {
		physicalNodes = new ArrayList<Server>();
		addMe();
		BackendDB.getInstance().initNew(MAX_VIRTUAL);
	}

	public void initFromServer(String IP, int port) {
		String responseText = Util.doGet(new Server(IP, port), "/serverlist");
		physicalNodes = Util.g.fromJson(responseText, JSONServerList.class);
		int[] oldMap = generateMap();
		addMe();
		BackendDB bdb = BackendDB.getInstance();
		for (int j = 0; j < MAX_VIRTUAL; j++) {
			if (serverMap[j] == myId) {
				responseText = Util.doGet(physicalNodes.get(oldMap[j]),
						"/getvirtual?node=" + j);
				bdb.putNode(j,
						Util.g.fromJson(responseText, BackendVirtualDB.class));
			}
		}
		for (Server s : physicalNodes) {
			if (s != me) {
				Util.doGet(s, "/addserver?ip=" + me.getIP() + "&port=" + me.getPort());
			}
		}
		log.info("Network join successful");
	}
	
	public void initFrontend(String IP, int port) {
		String responseText = Util.doGet(new Server(IP, port), "/serverlist");
		physicalNodes = Util.g.fromJson(responseText, JSONServerList.class);
		serverMap = generateMap();
		log.info("Network join successful");
	}

	private void addMe() {
		me = new Server(WebServer.MyIP, WebServer.MyPort);
		physicalNodes.add(me);
		myId = physicalNodes.size() - 1;
		serverMap = generateMap();
		log.info(Arrays.toString(serverMap));
	}
	
	public void addServer(String ip, int port) {
		physicalNodes.add(new Server(ip, port));
		serverMap = generateMap();
		log.info(Arrays.toString(serverMap));
	}

	private int[] generateMap() {
		int[] retval = new int[MAX_VIRTUAL];;
		int numNodes = physicalNodes.size();

		int counts[] = new int[numNodes];
		for (int i = 0; i < numNodes; i++) {
			counts[i] = 0;
		}

		for (int j = 0; j < MAX_VIRTUAL; j++) {
			retval[j] = 0;
			counts[0]++;
		}

		int max = maxArrayIndex(counts);
		boolean take;
		for (int i = 1; i < numNodes; i++) {
			take = false;
			for (int j = 0; j < MAX_VIRTUAL; j++) {
				if (retval[j] == max) {
					if (take) {
						counts[retval[j]]--;
						retval[j] = i;
						counts[retval[j]]++;
						take = false;
					} else {
						take = true;
					}
				}
			}
			max = maxArrayIndex(counts);
		}
		return retval;
	}

	private static int maxArrayIndex(int a[]) {
		int maxCount = 0;
		int maxIndex = 0;
		for (int i = 0; i < a.length; i++) {
			if (maxCount < a[i]) {
				maxCount = a[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public String getMyIP() {
		return me.getIP();
	}

	public int getMyPort() {
		return me.getPort();
	}

	public Server getServer(String key) {
		Server retval = null;

		retval = physicalNodes.get(serverMap[getVirtualNode(key)]);

		return retval;
	}

	public String getServerList() {
		return Util.g.toJson(physicalNodes, JSONServerList.class);
	}

	public static int getVirtualNode(String key) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		digest.reset();
		byte[] hashBytes = digest.digest(key.getBytes());

		int hash = (int) hashBytes[0] & 0x000000FF;
		log.info("Translated " + key + " to " + hash);
		return hash;
	}
}
