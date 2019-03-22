package mm.issues;

import java.util.List;

import javax.json.JsonObject;

import org.junit.BeforeClass;
import org.junit.Test;

import mm.issues.provider.IssueResponse;
import mm.issues.util.AsyncConcurrentSearch;
import mm.issues.util.Builder;
import mm.issues.util.Util;

/**
 * 
 * TestMain
 * 
 * @author markmorris
 * @version 1.0
 *
 */
public class TestMain {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("setUpBeforeClass");
	}

	@Test
	public void testOne() {
		assert(1 != 2);
	}
	
	//TODO: create tests for...
	
	//Util.validateArgument(args);
	
	//Util.checkForAndSetPrettyPrintOption(args); 
	
	//Util.removeFirstElement(args);
	
	//Util.validateInputArguments(repoArgs);
	
	//AsyncConcurrentSearch.getIssues(repoArgs);		
	
	//Builder.buildIssuesJsonList(listOfRepoIssues);
	
	//Util.getMaxDay(Util.dayCounts);
	
	//Util.getReposAndCountsForDay(topDay, listOfIssues);
	
	//Builder.buildIssuesJsonString(listOfIssues, maxDayRepos, topDay);

	//Util.doPrettyPrint(jsonString);
	
	//Util.printMessages();
	
	// test single request
	
	// test multiple request
	
	// test getting all pages
	
	// test failure of request
	
	// test ordering
	
	// test unicode characters
	
	// test multiple top days
	
	// test multiple top day repo ordering
	
	// test for breaking point like 1000 repos
	
	// test async conncurrent performance 
	
	
}
