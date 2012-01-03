import java.util.ArrayList;

public class Row implements Comparable<Row> {
		ArrayList<String> tweets;
		int clock;
		
		public Row (ArrayList<String> t, int c) {
			tweets = t;
			clock = c;
		}
		
		public int getClock () {
			return clock;
		}
		
		public ArrayList<String> getTweets () {
			return tweets;
		}
		
		public int compareTo (Row r) {
			if (clock < r.getClock()) {
				return 1;
			} else if (clock > r.getClock()) {
				return -1;
			}
			return 0;
		}
	}