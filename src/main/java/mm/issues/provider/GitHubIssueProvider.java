package mm.issues.provider;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.RequestURI;
import com.jcabi.http.Response;
import com.jcabi.http.response.JsonResponse;

import mm.issues.util.Util;

/**
 * class {@code GitHubIssueProvider} leverages jcabi to access and use the GitHub version 3 API.<br>
 * <br>
 * jcabi is open source, available from GitHub and takes care of the heavy 
 * lifting for executing the GitHub APIs.<br>
 * <br>
 * Using it will allow this use case to embrace change. Starting simple and
 * later security can be added with additional functionality to create and
 * update and act on issues by both humans and machines.<br>
 * <br>
 * If the need to access additional resources for issues then the pattern
 * implemented supports additional providers by just supplying a new Provider. 
 * For example class {@code CodeCommitProvider}, a potential provider for 
 * CodeCommit is AWS's "git" repository.<br><br>
 * 
 * @author markmorris
 * @version 1.0
 * 
 * 
 */
public class GitHubIssueProvider implements IssueProvider {

	/**
	 * searchForIssues will access GitHub using the third-party library provided by
	 * open source JCABI.<br>
	 * <br>
	 * It builds up the URL and then fetches the results.<br>
	 * <br>
	 * GitHub states it limits the returned issues to 1000 per repository even though the program
	 * is able to fetch more than 1000 for a repository. GitHub returns a
	 * last page indicator in the headers. We use this header to retrieve all
	 * the issue pages.<br>
	 * <br>
	 * This operation is running in it's own thread, concurrently executing with
	 * additional operations retrieving repositories.<br>
	 * <br>
	 * Ideally, the task of downloading the pages can also be performed async
	 * concurrently, but for now we will retrieve them one by one. But each
	 * repository is async concurrently fetched. And within each thread the pages
	 * are fetched. This page fetching could also be concurrent but the rate limit
	 * defeats the benefit.<br>
	 * <br>
	 * If rate limiting was not an restriction then the processing of fetching 
	 * could be distributed across many machines, and aggregating can also be 
	 * spread across a decreasing number of machines until
	 * one assembles the final two to merge.<br>
	 * <br>
	 * TODO: the exception handling needs more treatment to capture and understand
	 * what may go wrong in the GitHub response. Using a new library and 
	 * third-party data source API does bring benefit and risk, so trapping 
	 * all errors is critical. Currently, we trap exceptions, but a finer 
	 * granularity is needed for production.<br>
	 * <br>
	 * @param repoName String representing the owner and repository as
	 *                owner/repo_name
	 * @return IssueResponse of JsonObjects each representing an issue for the
	 *         repository
	 * 
	 */
	@Override
	public IssueResponse searchForIssues(String repoName) {

		IssueResponse issueResponse = new IssueResponse();
		Map<String, List<String>> headers = null;
		List<String> link = new ArrayList<String>();
		List<String> rateLimit = new ArrayList<String>();
		List<String> githubApiVersion = new ArrayList<String>();
		String linkurl = null;
		Github github = null;
		boolean firstTime = true;
		int currentPage = 1;
		String pageNumber = "1";
		String lastPage = "34";
		Response response = null;
		
		Map<String, String> env = System.getenv();
		String username = env.get("GITHUB_ISSUES_USERNAME");
		String password = env.get("GITHUB_ISSUES_PASSWORD");
		String pacing = env.get("GITHUB_ISSUES_PACING");
		
		// get api object
		github = getGitHubApi(username, password);

		List<String> rateLimitRemaining = new ArrayList<String>();
		rateLimitRemaining.add("30");
		
		//////// LOOP TO GET ALL PAGES ////////
		
		while( currentPage <= Integer.valueOf(lastPage) ) {
		
			pageNumber = String.valueOf(currentPage);
			// create request
			Request request = createRequest(repoName, github, pageNumber);
	
			Integer rl = new Integer(rateLimitRemaining.get(0));
			
			if( rl < 10 ) {
				if(pacing != null && pacing.toLowerCase().equals("true")) {
					try {
						Util.log("Rate limit is close to max, so sleeping for 30 seconds.");
						Thread.sleep(30 * 1000);
					} catch (InterruptedException e) {
						
						Util.addMessageToLog("Rate limiting sleep interuption.");
					}
				}
				// fetch response
				response = fetch(request);
				
			} else {
				
				// fetch response
				response = fetch(request);
					
			}
			
			// get the response headers
			headers = response.headers();
	
			// get the last page if more than one page
			if(firstTime) {
				firstTime = false;
				lastPage = extractLastPage(headers);
				if(lastPage == null) {
					lastPage = "1";
				}
				Util.addMessageToLog("\nGitHubIssueProvider.searchForIssues: Last page for " + repoName + " is " + String.valueOf(lastPage));
			}
			// contains the API version - we may want this so
			// we can manage change
			// X-GitHub-Media-Type=[github.v3]
			githubApiVersion = headers.get("X-GitHub-Media-Type");
			
			// ratelime 10/min default and 30/min if authenticated
			rateLimit = headers.get("X-RateLimit-Limit");
			
			// ratelimit remaining
			rateLimitRemaining = headers.get("X-RateLimit-Remaining");
			
			if(pacing != null && pacing.toLowerCase().equals("true")) {
				Util.log("ratelimit is " + rateLimit);
				Util.log("rateLimitRemaining is " + rateLimitRemaining);
			}
			
			JsonResponse resp = response.as(JsonResponse.class);
			JsonReader jsonReader = resp.json();
			JsonObject jsonObject = jsonReader.readObject();
	
			issueResponse = processJson(repoName, issueResponse, jsonObject);
			
			currentPage++;
	
		}
		// END LOOP
		/////////////////////////////////////////////

		return issueResponse;

	}

	private IssueResponse processJson(String repoNme, IssueResponse issueResponse, JsonObject jsonObject) {
		
		// Process the jsonObject.
		// Add a lot of null checking because we don't know what to expect.
		// TODO: refactor this to remove the nesting; onion pealing.
		if (jsonObject != null) {

			// get the items - an array
			JsonArray jsonArray = jsonObject.getJsonArray("items");

			// might be null so check
			if (jsonArray != null) {

				// turn into a list
				List<JsonObject> items = jsonArray.getValuesAs(JsonObject.class);

				// make sure it is not null
				if (items != null) {

					// set the items in the issueResponse object
					if(issueResponse.jsonObjects != null && issueResponse.jsonObjects.isEmpty()) {
						issueResponse.setJsonObjects(items);
					} else {
						
						// this is immutable so need to pull it apart
						List<JsonObject> existingObjects = issueResponse.getJsonObjects();												
						Object[] json = existingObjects.toArray();
						
						Object[] itemsToAdd = items.toArray();
						
						// create a new list
						List<JsonObject> list1 = new ArrayList(Arrays.asList(json));
						
						List<JsonObject> list2 = new ArrayList(Arrays.asList(itemsToAdd));
									
						list1.addAll(list2);
						
						issueResponse.setJsonObjects(list1);
					}

					/////////////////////////////////////////////
					// error handling
					/////////////////////////////////////////////

				} else {

					// Null List<JsonObject> items found

					String message = String.format(
							"GitHubIssueProvider.processJson: Null List<JsonObject> found on repo %s --> Message: %s", repoNme,
							jsonObject.getString("message"));
					Util.addMessageToLog(message);
				}

			} else {

				// Null jsonArray found
				String message = String.format("GitHubIssueProvider.processJson: Null jsonArray found on repo %s --> Message: %s",
						repoNme, jsonObject.getString("message"));
				Util.addMessageToLog(message);
			}

		} else {

			// Null jsonObject found
			String message = String.format("GitHubIssueProvider.processJson: Null jsonObject found on repo %s", repoNme);
			Util.addMessageToLog(message);
		}
		
		return issueResponse;
	}

	/**
	 * getGitHubApi creates the object to access GitHub API. If username and password are found in the 
	 * environment then username and password will use them to return an authenticated object for 
	 * accessing GitHub. An authenticated object will increase the GitHub rate limit from 10 to 30.
	 * @param username is your GitHub username if the environment variable GITHUB_ISSUES_USERNAME is set
	 * @param password is your GitHub password if the environment variable GITHUB_ISSUES_PASSWORD is set
	 * @return GitHub the GitHub object to access GitHub API
	 */
	private Github getGitHubApi(String username, String password) {
		Github github;
		// check if we want to authenticate
		if (username != null && username.length() > 0 && password != null && password.length() > 0) {

			// -mm- diag
			Instant start = Instant.now();

			// create authenticated object
			github = new RtGithub(username, password);

			// -mm- diag
			Instant finish = Instant.now();
			String timeElapsed = Duration.between(start, finish).toString();
			Util.addMessageToLog("GitHubIssueProvider.getGitHubApi: Time elapsed executing GtHub authentication: " + timeElapsed);

		} else {

			// create unauthenticated object
			github = new RtGithub();

		}
		return github;
	}

	/**
	 * fetch executes the request 
	 * @param request is the search request for GitHub
	 * @return Response is object returned from the fetch. It encapsulates the HTTP JSON response from GitHub.
	 */
	private Response fetch(Request request) {
		// fetch the response
		Response response = null;
		try {
			response = request.fetch();
				
		} catch (IOException e) {
			Util.addMessageToLog("GitHubIssueProvider.fetch: searchForIssues caught an exception.");
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * extractLastPage operation gets the lastpage indicator from the response headers. 
	 * lastpage indicates how many issues are available for a repository. The program uses
	 * the lastpage variable to loop through and fetch all issues up to and
	 * including the lastpage of issues.
	 * @param headers from the response
	 * @return String representing the last available page of issues for the repository
	 */
	private String extractLastPage(Map<String, List<String>> headers) {
		List<String> link;
		String linkurl;
		String lastpage = null;
		// get the link header - it may not exist if only 1 page
		link = headers.get("Link");
		// get the last page number if the link exists
		// if it does we want it so we can
		// iteratively get the rest of the pages
		if (link != null) {
			linkurl = link.get(0);
			String[] parts = linkurl.split(";");
			parts = parts[1].split("&");
			parts = parts[4].split("=");
			lastpage = parts[1].substring(0, parts[1].length() - 1);
		}
		return lastpage;
	}

	/**
	 * createRequest creates the HTTPs request to search GitHub. It uses an 
	 * incrementing pageNumber to create a request for each available page of
	 * issues.
	 * @param repoName the name of the repository to search for issues
	 * @param github the GitHub access object created from getGitHubApi operation
	 * @param pageNumber the page to fetch
	 * @return Request object that encapsulates the HTTPs request.
	 */
	private Request createRequest(String repoName, Github github, String pageNumber) {
		// create the request object
		Request request = github.entry();
		RequestURI requestURI = request.uri();
		requestURI = requestURI.path("/search/issues");
		requestURI = requestURI.queryParam("q", String.format("repo:%s", repoName));
		requestURI = requestURI.queryParam("state", "all");
		requestURI = requestURI.queryParam("sort", "created");
		requestURI = requestURI.queryParam("order", "asc");
		requestURI = requestURI.queryParam("page", pageNumber);
		request = requestURI.back();
		return request;
	}

}// end GitHub