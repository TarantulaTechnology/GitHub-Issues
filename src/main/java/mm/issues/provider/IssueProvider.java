package mm.issues.provider;

/**
 * 
 * IssueProvider for multiple issue provider supporting any
 * future growth.
 * 
 * @author markmorris
 * @version 1.0
 */
public interface IssueProvider {
	
	public IssueResponse searchForIssues(String repo);

}//end Class2