package mm.issues.provider;

/**
 * 
 * Class {@code CodeCommitIssueProvider} is an example 
 * illustrating a simple pattern that supports change. 
 * The data provider is decoupled from the client of the data. 
 * The client always see the same interface, but we are 
 * free to extend the capabilities without breaking the client.
 * 
 * @author markmorris
 * @version 1.0
 */
public class CodeCommitIssueProvider implements IssueProvider {

	/**
	 * method to implement for searching an issue provider and returning 
	 * issues
	 */
	@Override
	public IssueResponse searchForIssues(String repo) {
		// TODO Auto-generated method stub
		return null;
	}

}//end CodeCommit