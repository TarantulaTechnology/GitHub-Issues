package mm.issues.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class my be deprecated and is not currently used.
 * RepoList class planned to encapsulate the "command" 
 * to search on demand using it's repos list.
 * 
 * This will allow multiple command objects to execute
 * concurrently.
 * 
 * @author markmorris
 * @version 1.0
 * @deprecated
 * 
 */
public class RepoList {
	
	private String[] args;
	List<String> repos = new ArrayList<String>();
	
	/**
	 * getRepos returns the input args as a list to be used for processing.
	 * @return List of String containing the repository names.
	 */
	public List<String> getRepos() {
			return repos;
	}

	/**
	 * Constructor is initialized with any arguments from the input.
	 * @param _args from the input
	 */
	public RepoList(String[] _args) {
		this.args = _args;
		createtReposList();
	}
	
	/**
	 * size of repo list
	 * @return the size of the repo list -- number of entries.
	 */
	public int size() {
		if(repos == null) {
			return 0;
		} else {
			return repos.size();
		}
	}
	/**
	 * createtReposList - process the input args and create 
	 * a list for processing.
	 * 
	 * TODO: Add validation logic and domain error checking.
	 * Currently assumes good input and adds it to the list
	 * of repos to search.
	 * 
	 */
	private void createtReposList() {
		
		List<String> repos = new ArrayList<String>();
		
		try {			
			for( String arg : args) {
				
				// validate the arg
				// check for correct format
				// bad characters, etc.
				// skip if bad
				// TODO: use a regex expression to perform check.
				// if we get funny business then log it.
				
				// ok passed the checks, so lets add it.
				repos.add(arg);
			}			
			this.repos = repos;
		} catch (Exception e) {
			// things can and do go wrong 
			// with arrays and input :)
			System.out.println("Caught exception on createReposList"); // TODO: move to message constants.
			e.printStackTrace();
			// we will continue though, set empty list
			this.repos = new ArrayList<String>();
			
		}
		
	}

}
