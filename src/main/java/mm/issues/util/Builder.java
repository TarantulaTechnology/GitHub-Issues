package mm.issues.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import mm.issues.provider.IssueResponse;

/**
 *
 * Buider builds the output from the Json objects returned from the GitHub search.
 *
 * @author markmorris
 * @version 1.0
 *
 */
public class Builder {

	/**
	 *
	 * buildIssues constructs a list of Json objects from a list of IssueResponses. This list is used to 
	 * process the Json objects which represent issues from a repository.
	 * 
	 * @param listOfIssueResponses containing the JsonObject list. The JsonObject contains the issues for a repo.
	 * @return List of json objects created from all of the JsonObjects in IssueResponse.
	 */
	public static List<JsonObject> buildIssuesJsonList(List<IssueResponse> listOfIssueResponses) {

		int count = 0;

		List<JsonObject> issueList = new ArrayList<>();

		if (listOfIssueResponses != null && !listOfIssueResponses.isEmpty()) {

			// for each repo
			for (IssueResponse issueResonse : listOfIssueResponses) {
				
				count = 0;

				List<JsonObject> listOfRepoIssues = issueResonse.getJsonObjects();

				if(listOfRepoIssues != null) {

					String repository = "";
					// for each issue
					for (JsonObject jsonobj : listOfRepoIssues) {

						count++; // for diagnostics

						// id
						JsonValue id = jsonobj.get("id");

						// state
						JsonValue state = jsonobj.get("state");

						// title
						JsonValue title = jsonobj.get("title");

						// repository
						//TODO: refactor -- add the repo name to the issueResponse.
						repository = jsonobj.getString("repository_url");
						String[] parts = repository.split("\\/");
						repository = "" + parts[4] + "/" + parts[5];


						// create_at
						JsonValue created_at = jsonobj.get("created_at");

						// create new issue json object
						JsonObject issueJson = Json.createObjectBuilder()
						.add("id", id)
						.add("state", state)
						.add("title", title)
						.add("repository", repository)
						.add("created_at", created_at)
						.build()
						;

						// add to new issue list
						issueList.add(issueJson);
						
						Util.accumulateDayCounts(created_at.toString());

					} //for

					Util.addMessageToLog("Builder.buildIssues: Count for " + repository + " issues: " + String.valueOf(count));

				} //if

			}//for

			Util.addMessageToLog("Builder.buildIssues: Count for all issues collected: " + issueList.size());

		} else {
			System.out.println("NO ISSUES FOUND");
		}
		
		return issueList;

	}

	/**
	 * buildIssuesJsonString builds the json string for the report.
	 * @param listOfIssues is the list of issues as JsonObjects
	 * @param maxDayRepos is the Map of repositories that share the top day for issues 
	 * @param topDay is the day "YYYY-MM-DD" that had the most issues
	 * @return String is the report as a String of Json
	 */
	public static String buildIssuesJsonString(List<JsonObject> listOfIssues, Map<String, Integer> maxDayRepos, String topDay) {

		String json = null;
		StringBuilder allrepoissues = new StringBuilder("");

		if (listOfIssues != null && !listOfIssues.isEmpty()) {

			// for each repo
			for (JsonObject issue : listOfIssues) {

						allrepoissues.append(issue.toString() + ",");
			}

			Util.addMessageToLog("Builder.buildIssuesList: Count for issues: " + String.valueOf(listOfIssues.size()));

			if(allrepoissues != null && allrepoissues.length() > 0) {
				allrepoissues.insert(0, "{\"issues\":[");
				allrepoissues.deleteCharAt(allrepoissues.length()-1);
				allrepoissues.append("],");
				
				// now add top day
				String topDayStringStart = 
						String.format("\"top_day\": {\"day\": \"%s\",\"occurrences\": {", topDay);
				String topDayStringEnd = "}}}";
				
				StringBuilder sb = new StringBuilder();
				String occurrences = null;
				
				final Map<String, Integer> sortedByCount = maxDayRepos.entrySet()
		                .stream()
		                .sorted( (Map.Entry.<String, Integer>comparingByValue().reversed()))
		                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				
				for( Entry<String, Integer> entry : sortedByCount.entrySet() ) {
					
					String repo = entry.getKey();
					String count = String.valueOf(entry.getValue());
					occurrences = String.format("\"%s:\": %s,", repo, count);
					sb.append(occurrences);
					
				}
				
				sb.deleteCharAt(sb.length()-1);
				String repoString = sb.toString();
				
				String topday = topDayStringStart + repoString + topDayStringEnd;
				
				String s = allrepoissues.toString();
				
				json = s + topday;
				
			}


		} else {
			Util.addMessageToLog("Builder.buildIssuesList: NO ISSUES FOUND");
		}

		return json;

	}

}
