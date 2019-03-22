package mm.issues.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Util class is the workhorse and contains many helper operations that execute the task
 * of searching multiple GitHub repositories. This class captures the core functions and 
 * can be used to refactor the program into a more object oriented design based on the known
 * functional and non-functional characteristics of accessing GitHub. Many aspects of access are 
 * captured and can be refined and augmented with granular unit tests. 
 * 
 * @author markmorris
 * @version 1.0
 *
 */
public class Util {
	
	/**
	 * dayCounts is a map containing the days and their counts. This is used to 
	 * calulate the top day report section.
	 */
	public static Map<String, Integer> dayCounts = new HashMap<String, Integer>();

	
	/**
	 * holds program logged diagnostic messages
	 */
	private static  List<String> messageLog = new ArrayList<String>();

	/**
	 * pretty print json
	 */
	public static boolean prettyPrint = false;


	private static String[] RepoList;
	
	/**
	 * checkForAndSetPrettyPrintOption checks if the 1st command line argument is a lowercase letter p, which
	 * indicates to output Json in pretty print format. Will also set a flag, so the program
	 * knows to output json in pretty print format.
	 * 
	 * @param args input arguments from command line
	 */
	public static void checkForAndSetPrettyPrintOption(final String[] args) {
		Util.prettyPrint = args[0].equals("p") ? true : false;
	}

	/**
	 * 
	 * validateInputArguments is the operation that validates the command line input arguments.
	 * It checks for valid repo names, and if, invalid throws an exception and stops program execution. <br>
	 * <br>
	 * TODO: handle how to proceed on errors. Possibilities are create an exception report 
	 * for the invalid repositories, and keep processing any valid repositories found. <br>
	 * Or prompt the user for a correction or replacement. <br>
	 * Discussions with the end users are required to determine
	 * the best way of handling errors when they occur.
	 * 
	 * @param repoArgs String[] of repo name input arguments
	 */
	public static void validateInputArguments(String[] repoArgs) {
		// validate repo name input args - exit on error and output a help message
		// upper or lower alpha, numbers, dashs, underscore, and a single forward slash
		// valid: owner/repository or owner/repo-name
		int numberOfArguments = 0;
		for (String repoString : repoArgs) {
			
			numberOfArguments = repoArgs.length;
			
			final Pattern pattern = Pattern.compile("([A-Za-z0-9-_])+\\/([A-Za-z0-9-_])+"); // TODO: move to constants
			
			if (!pattern.matcher(repoString).matches()) {
				String errMessage = String.format(
						"Invalid Input String Error: %s -- Input must be a pattern like owner1/repo1 owner2/repo2 ...",
						repoString); // TODO: move to constants
				throw new IllegalArgumentException(errMessage);
			}
			
		}
		Util.addMessageToLog("\nUtil.validateInputArguments: "
					+ "" + String.valueOf(numberOfArguments)
					+ " valid repositories.");
		
		// Store repo list for later use to calc the top day
		// have the list will save time iterating on the issues
		Util.RepoList = repoArgs;

	}

	/**
	 * 
	 * removeFirstElement is a utility that performs an array unshift operation.
	 * @param args String[] the array to unshift
	 * @return String[] the resulting array without first element
	 */
	public static String[] removeFirstElement(final String[] args) {
		String[] repoArgs;
		repoArgs = Arrays.copyOfRange(args, 1, args.length);
		return repoArgs;
	}

	/**
	 * log is a shortcut for System.out.println
	 * @param str to print to console
	 */
	public static void log(String str) {
		System.out.println(str);
	}
	
	/**
	 * error message part for validate operations
	 */
	static String errMsgPart = "getIssues version 1.0\nUsage: java -jar getIssues.jar [p] owner1/repo1 owner2/repo-2 owner3/repo_3 ...\n"
			+ "The optional first argument p instructs the code to return the JSON string in pretty format.\n"
			+ "NOTE: GitHub enforces a rate limit of 10 per minute and you will experience it when you enter\n"
			+ "many owner/repo entries or you continously execute too fast.\n"
			+ "You can login to GitHub by entering: your_username and your_password\n"
			+ "to improve your rate limit. The rate limit is increased to 30.\n"
			+ "Set the following environment variables to login to github.\n"
			+ "GITHUB_ISSUES_USERNAME=username\n"
			+ "GITHUB_ISSUES_PASSWORD=password\n"
			+ "Set this environment variable if you want to print job stats at the end.\n"
			+ "GITHUB_ISSUES_PRINT_MESSAGES=true\n"
			+ "Set this environment variable if you want to ensure you get all\n"
			+ "issues available and avoid the rate limit.\n"
			+ "This will cause the program to sleep for 30 seconds when it\n"
			+ "detects a potential rate limit violation approaching.\n"
			+ "GITHUB_ISSUES_PACING=true\n"
			;
	
	/**
	 * validateArgument checks the command line input arguments for errors.
	 * 
	 * @param args is the command line input arguments
	 */
	public static void validateArgument(final String[] args) {
		
    	if(args == null || args.length == 0) {
    		String help = "***** NO INPUT ARGUMENTS FOUND. APPLICATION REQUIRES INPUT ARGUMENT *****\n" + errMsgPart;
    		Util.log(help);
    		System.exit(0);
    	}
    	
    	// p alone is an error too

    	if( args[0].equals("p") && args.length < 2 ) {
    		String help = 
    				"***** NO INPUT ARGUMENTS FOUND AFTER OPTIONAL PRETTY PRINT OPTION. *****\n" + errMsgPart;
    		Util.log(help);
    		System.exit(0);
    	}
	}

	/**
	 * addMessageToLog is used to capture runtime messages from 
	 * the program, so they can be printed on completion and viewed.
	 * Used for analysis and troubleshooting.
	 * 
	 * @param message to be stored and printed on completion of program
	 */
	public static synchronized void addMessageToLog(String message) {
		
		Util.messageLog.add(message);
	}

	/**
	 * printMessage is used to print the program messages captured during execution.
	 */
	public static void printMessages() {
		
		Map<String, String> env = System.getenv();
		String printMessages = env.get("GITHUB_ISSUES_PRINT_MESSAGES");

		if( printMessages != null && printMessages.toLowerCase().equals("true") ) {
			
			for (String string : messageLog) {
				Util.log(string);				
			}
		}
		
	}

	/**
	 * doPrettyPrint is a utility operation for printing json in pretty print format.
	 *  
	 * @param report is the raw json string
	 */
	public static void doPrettyPrint(String report) {
		if(report != null && report.length() > 0) {
			JsonReader reader = Json.createReader(new StringReader(report));
			JsonObject reportObject = null;
			if(reader != null ) {
				try {
				reportObject = reader.readObject();
				reader.close();		
				}catch (Exception e) {
					e.printStackTrace();
				}
				String prettyJson = Printer.prettyPrint(reportObject);
				Util.log(prettyJson);	
			}
		}
	}

	/**
	 * accumulateDayCounts is the operation that creates the list of days, their issue count, and 
	 * stores them for calculating the top day report segment. The operation strips off the date from
	 * the response date (2011-05-08T09:15:20Z) like so, 2011-05-08, and accumulates counts for the day.
	 * It does this for all issues, creating effectively, a set, as a map of day:count. This helps calculating
	 * the top day and eliminates iteration over all issues for the top day calculation.
	 * 
	 * @param _day is a date string from the response formatted as 2011-05-08T09:15:20Z.
	 */
	public static void accumulateDayCounts(String _day) {
		
		String[] parts = _day.split("T");
		String day = parts[0].substring(1);
		
		if( Util.dayCounts.containsKey(day) ) {
			// increment
			Integer count = Util.dayCounts.get(day);
			count++;
			Util.dayCounts.put(day, count);
			
		} else {
			// add 
			Util.dayCounts.put(day, new Integer(1));
		}
		
		
	}

	/**
	 * getMaxDay is a utility operation for determining the top day. 
	 * It uses a Map created by accumulateDayCounts.
	 * 
	 * @param dayCounts a map of day:count for all of the issues found
	 * @return String of the max day ex. "2010-05-12".
	 */
	public static String getMaxDay(Map<String, Integer> dayCounts) {		
		
		List<String> maxDays = new ArrayList<String>();
		
		int maxValue = (Collections.max(dayCounts.values()));
		
		for( Entry<String, Integer> entry : dayCounts.entrySet() ) {
			
			if( entry.getValue()==maxValue ) {
				
				maxDays.add(entry.getKey());
			}
			
		}
		return (Collections.max(maxDays));
	}

	/**
	 * getReposAndCountsForDay finds the repositories that had issues on the top day.
	 *
	 * @param topDay is the day with the most issues
	 * @param listOfIssues is a list of Json objects from the response that contain issues for a repository 
	 * @return Map of repositories that had issues on the top day with their issue count on the top day
	 */
	public static Map<String, Integer> getReposAndCountsForDay(String topDay, List<JsonObject> listOfIssues) {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (JsonObject jsonObject : listOfIssues) {
			
			String date = jsonObject.getString("created_at");
			String repository = jsonObject.getString("repository");
			
			if(date.contains(topDay)) {				
				// this issue did occur on top day 
				// see if it is already in the list
				if(map.containsKey(repository)){
					// update count
					Integer count = map.get(repository);
					count++;
					map.put(repository, count);
				} else {
					// add it 
					map.put(repository, new Integer(1));
				}
			}
		}
		
		// now set all repos to zero and then set the max repo list
		for (String repo : Util.RepoList) {
			if(!map.containsKey(repo)) {
				// add it
				map.put(repo, new Integer(0));
			}
		} 
		
		// now we have list of the repos that had issues on top day
		// the rest of the repos will be set to zero in the report

		return map;
	}
		
}

















