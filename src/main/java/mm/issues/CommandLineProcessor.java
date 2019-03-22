package mm.issues;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import mm.issues.provider.IssueResponse;
import mm.issues.util.AsyncConcurrentSearch;
import mm.issues.util.Builder;
import mm.issues.util.Util;

/**
 * class CommandLineProcessor takes the input arguments, a string list of GitHub
 * Repository names, and searches each one for issues both "open" and "closed".
 * It employs several helper classes to partition the tasks required to generate
 * a JSON string meeting the defined requirements found in {@code Main.java}
 * <br><br>
 * Same repo entered multiple times. The program allows this, but the output will
 * obviously be off.
 * <br><br>
 * If a valid repo name is entered but the repo does not exist in GitHub
 * the message from GitHub is printed in the output but is not included in
 * the report.
 * <br><br>
 * Running from the runnable jar while convenient is much slower than
 * running with an expanded jar and classpath. There is a file in output-jar 
 * called setclasspath that you can edit and source prior to execution to set
 * the classpath and the other environment settings.
 * <br><br>
 * Execute the program without any arguments to get some help on
 * the environment variables that can be set, which may be helpful.
 * <br><br>
 * TODO: add better notification to alert user the repo name was 
 * not validated in GitHub, meaning it was not found. For now the user can
 * turn on message printing to see the repos that GitHub did not find.
 * Message printing is enabled by setting an environment variable.<br><br>
 * Here are the environment variable that can be set:<br> 
 * GITHUB_ISSUES_USERNAME=username<br>
 * GITHUB_ISSUES_PASSWORD=password<br>
 * GITHUB_ISSUES_PRINT_MESSAGES=true | false<br>
 * GITHUB_ISSUES_PACING=true | false<br>
 * 
 * @author markmorris
 * @version 1.0
 *
 */
public class CommandLineProcessor {

	/**
	 * run is the main line of execution. It calls helpers to execute 
	 * the task of fetching issues from a provider. The class {@code GitHubIssueProvider}
	 * is used to execute async concurrent requests for issues.
	 * 
	 * @param args a list of GitHub repository names owner/repo
	 * @throws IllegalArgumentException error in input arguments 
	 */
	public void run(final String[] args) throws IllegalArgumentException {
		
		String[] repoArgs = args;
		String timeElapsed = null;
		
		Instant startCommandLineProcessor = Instant.now();
		
		// will exit if invalid
		Util.validateArgument(args);

		// check for pretty print
		// Optional pretty print feature - 1st arg must be a lowercase p
		Util.checkForAndSetPrettyPrintOption(args); // TODO: move to constants
		
		if (Util.prettyPrint) {
			repoArgs = Util.removeFirstElement(args);
		}

		Util.validateInputArguments(repoArgs);
		
		Instant startAsyncConcurrentSearch = Instant.now();
		
		// Execute search
		List<IssueResponse> listOfRepoIssues = AsyncConcurrentSearch.getIssues(repoArgs);		
		
		timeElapsed = Duration.between(startAsyncConcurrentSearch, Instant.now()).toString();		
		Util.addMessageToLog("\nCommandLineProcessor.run: Time executing AsyncConcurrentSearch " + timeElapsed + "\n");
		
		// Transform the raw search results into our business 
		// requirement domain model Issue.java.
		List<JsonObject> listOfIssues = Builder.buildIssuesJsonList(listOfRepoIssues);
		
		if(listOfIssues != null && !listOfIssues.isEmpty()) {
			

			// get top day
			String topDay = Util.getMaxDay(Util.dayCounts);
			
			// get the repos and counts for the max day
			Map<String, Integer> maxDayRepos = Util.getReposAndCountsForDay(topDay, listOfIssues);
			
	
			String jsonString = Builder.buildIssuesJsonString(listOfIssues, maxDayRepos, topDay);
			// Print the report string to the console
			// as defined in the requirements.
			if (!Util.prettyPrint) {
				Util.log(jsonString);
			} else {
				Util.doPrettyPrint(jsonString);
			}		
			Util.addMessageToLog( "\nJSON Report Length " + String.valueOf( jsonString.length()) + "\n");	
			
			timeElapsed = Duration.between(startCommandLineProcessor, Instant.now()).toString();		
			Util.addMessageToLog("Time executing CommandLineProcessor " + timeElapsed + "\n");
			
		} else {
			//No Issues found
			Util.log("*** No issues found. ***");
			timeElapsed = Duration.between(startCommandLineProcessor, Instant.now()).toString();		
			Util.addMessageToLog("Time executing CommandLineProcessor " + timeElapsed + "\n");
			
		}
			
		Util.printMessages();
		

		
	}// run

}
