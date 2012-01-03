import java.util.ArrayList;
import java.util.Collections;

public class ResultSet {
	ArrayList<Row> results;
	
	public ResultSet () {
		results = new ArrayList<Row>();
	}
	
	public void addRow(Row r) {
		results.add(r);
	}

	public String getLatest() {
		if (results.size() > 0) {
			Collections.sort(results);
			Row r = results.get(0);
			return Util.g.toJson(r, Row.class);
		} else {
			return "[\"not found\"]";
		}
	}

}
