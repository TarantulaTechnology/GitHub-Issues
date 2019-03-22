package mm.issues.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import mm.issues.provider.GitHubIssueProvider;
import mm.issues.provider.IssueResponse;

/**
 * 
 * AyncConcurrentSearch constructs a list of futures to search github.
 * 
 * @author markmorris
 * @version 1.0
 *
 */
public class AsyncConcurrentSearch {
		
	/**
	 * getIssues executes a list of CompletableFuture objects. Each searches github.
	 * @param repoArgs is an Array of String. Each is the name of a github repository in the form owner/repo.
	 * @return List of IssueResponse which contains the JsonObject list of issues returned.
	 */
	public static List<IssueResponse> getIssues(String[] repoArgs){
		
		List<CompletableFuture<IssueResponse>> githubSearchRepoFutures = Arrays.asList(repoArgs).stream()
				.map(repo -> CompletableFuture.supplyAsync(() -> {
					try {
						GitHubIssueProvider gitHubIssueProvider = new GitHubIssueProvider();
						IssueResponse githubRepoIssues = gitHubIssueProvider.searchForIssues(repo);
						return githubRepoIssues;
					} catch (Exception e1) {
						System.err.printf("ApiException when calling searchRepoForIssues for repo: %s", repo); 
						e1.printStackTrace();
					}
					return null;
				})).collect(Collectors.<CompletableFuture<IssueResponse>>toList());

		// transform for allof
		CompletableFuture<List<IssueResponse>> allRepoIssues = sequence(githubSearchRepoFutures);

		// Async concurrently get the search results from n repositories
		List<IssueResponse> listOfRepoIssues = null;
		try {
			listOfRepoIssues = allRepoIssues.get();
		} catch (InterruptedException e1) {
			System.err.println("InterruptedException when calling allRepoIssues.get()"); // TODO: move to constants
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			System.err.println("Exception when calling allRepoIssues.get()"); // TODO: move to constants
			e1.printStackTrace();
		}

		return listOfRepoIssues;
	}
	
	
	/**
	 * sequence is a class operation for transforming a List of CompletableFuture T
	 * to T CompletableFuture List of T
	 * @param <T> is a future
	 * @param futures is a future
	 * @return future
	 */
	private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture
				.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
	}


}
