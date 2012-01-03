import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

public class BackendVirtualDB {
	private static Logger log = Logger.getLogger(BackendVirtualDB.class);

	private HashMap<String, VirtualDataNode> index;
	private transient ReentrantReadWriteLock lock;
	private int clock;

	public BackendVirtualDB() {
		index = new HashMap<String, VirtualDataNode>();
		lock = new ReentrantReadWriteLock();
		clock = 0;
	}
	
	public BackendVirtualDB(HashMap<String, VirtualDataNode> i, int c) {
		index = i;
		lock = new ReentrantReadWriteLock();
		clock = c;
	}

	public void addTweet(String hash, String tweet) {
		log.info("Adding tweet '" + tweet + "' to #" + hash);
		lock.writeLock().lock();
		VirtualDataNode tweetList = null;

		if ((tweetList = index.get(hash)) == null) {
			index.put(hash, new VirtualDataNode());
			tweetList = index.get(hash);
		}
		tweetList.addTweet(tweet, clock++);
		lock.writeLock().unlock();
	}

	public ArrayList<String> getTweets(String hash) {
		lock.readLock().lock();
		ArrayList<String> retval = index.get(hash).getTweets();
		lock.readLock().unlock();
		return retval;
	}
	
	public int getMaxClock(String hash) {
		lock.readLock().lock();
		int retval = index.get(hash).getMaxClock();
		lock.readLock().unlock();
		return retval;
	}

	private class VirtualDataNode {
		public ArrayList<Tweet> tweetList;

		public VirtualDataNode() {
			tweetList = new ArrayList<Tweet>();
		}
		
		@SuppressWarnings("unused")
		public VirtualDataNode(ArrayList<Tweet> t) {
			tweetList = t;
		}

		public void addTweet(String tweet, int c) {
			tweetList.add(new Tweet(tweet, c));
		}
		
		public ArrayList<String> getTweets() {
			Collections.sort(tweetList);
			ArrayList<String> retval = new ArrayList<String>();
			for (Tweet t : tweetList) {
				retval.add(t.toString());
			}
			return retval;
		}
		
		public int getMaxClock() {
			Collections.sort(tweetList);
			return tweetList.get(0).clock;
		}
		
		private class Tweet implements Comparable<Tweet> {
			private String tweet;
			private int clock;

			public Tweet(String t, int c) {
				tweet = t;
				clock = c;
			}

			public int getClock() {
				return clock;
			}
			
			public String toString() {
				return tweet + " [" + clock + "]";
			}

			public int compareTo(Tweet t) {
				if (clock < t.getClock())
					return 1;
				else if (clock > t.getClock())
					return -1;
				return 0;
			}

		}
	}
}
