package mm.issues.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * TopDay ended up not being used. Will be deprecated.
 * 
 * @author markmorris
 * @version 1.0
 * @deprecated
 *
 */
public class TopDay {

	/**
	 * top day - day with highest count of issues
	 */
	String day = null;
	
	/**
	 * contains a map of repositories and counts
	 * for the top day
	 */
	public static List<Map<String, Integer>> occurances = 
			new ArrayList<Map<String, Integer>>();
	
	Map<String, Integer> dayCounts = new HashMap<String, Integer>();
	
}
