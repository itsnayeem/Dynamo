import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class BackendDB {
	private static Logger log = Logger.getLogger(BackendDB.class);

	private static BackendDB instance = null;

	private ConcurrentHashMap<Integer, BackendVirtualDB> database = null;
	
	public static BackendDB getInstance() {
		if (instance == null) {
			synchronized (BackendDB.class) {
				if (instance == null) {
					instance = new BackendDB();
				}
			}
		}
		return instance;
	}
	
	private BackendDB()  {
		database = new ConcurrentHashMap<Integer, BackendVirtualDB>();
	}
	
	public void initNew (int numVirtual) {
		log.info("Initializing system database");
		for (int i = 0; i < numVirtual; i++) {
			database.put(i, new BackendVirtualDB());
		}
	}
	
	public void putNode(int VirtualNode, BackendVirtualDB vdb) {
		database.put(VirtualNode, vdb);
	}
	
	public String getNode(int VirtualNode) {
		BackendVirtualDB vdb = database.get(VirtualNode);
		
		return Util.g.toJson(vdb, BackendVirtualDB.class);
	}
	
	public void addTweet(int VirtualNode, String hash, String tweet) {
		BackendVirtualDB vdb = database.get(VirtualNode);
		
		vdb.addTweet(hash, tweet);
	}
	
	public Row getTweets(int VirtualNode, String hash) {
		BackendVirtualDB vdb = database.get(VirtualNode);
		
		Row retval = new Row(vdb.getTweets(hash), vdb.getMaxClock(hash));
		
		return retval;
	}
	
	public static void main (String[] args) {
		BackendDB bdb = BackendDB.getInstance();
		bdb.initNew(1);
		
		bdb.addTweet(0, "apple", "I have 1 #apple");
		bdb.addTweet(0, "apple", "I have two apples");
		
		Row r = bdb.getTweets(0, "apple");
		System.out.println(Util.g.toJson(r, Row.class));
	}
	
}
