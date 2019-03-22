package mm.issues;
/**
 * 
 * Main entry calls {@code CommandLineProcessor} which sequences the processing 
 * from start to finish employing async concurrent GitHub search requests using
 * {@code CompletableFutures}.
 * <br><br>
 * 
 * GitHub Issues <br>
 *<br>
 * Generates a report about the the Issues belonging to a
 * list of github repositories ordered by creation time, and information about
 * the day when most Issues were created.<br>
 *<br>
 * Input: List of 1 to n Strings with Github repositories references with
 * the format "owner/repository"<br>
 *<br> 
 *
 * Output: String representation of a Json dictionary with the following
 * content:<br>
 *<br>
 * - "issues": List containing all the Issues related to all the repositories
 * provided. The list is ordered by the Issue "created_at" field (From
 * oldest to newest) Each entry of the list is a dictionary with basic
 * Issue information: "id", "state", "title", "repository" and "created_at"
 * fields. Issue entry example: { "id": 1, "state": "open", "title": "Found a
 * bug", "repository": "owner1/repository1", "created_at":
 * "2011-04-22T13:33:48Z" }
 *<br><br>
 * - "top_day": Dictionary with the information of the day when most Issues were
 * created. Contains the day and the number of Issues that were created
 * on each repository this day. If there are more than one "top_day", the latest
 * one is used. example: { "day": "2011-04-22", "occurrences": {
 * "owner1/repository1": 8, "owner2/repository2": 0, "owner3/repository3": 2 } }
 *<br><br>
 *
 * Output example: <br>
 *
 * {<br>
 * "issues": [ { "id": 38, "state": "open", "title": "Found a bug",<br>
 * "repository": "owner1/repository1", "created_at": "2011-04-22T13:33:48Z" }, {<br>
 * "id": 23, "state": "open", "title": "Found a bug 2", "repository":<br>
 * "owner1/repository1", "created_at": "2011-04-22T18:24:32Z" }, { "id": 24,<br>
 * "state": "closed", "title": "Feature request", "repository":<br>
 * "owner2/repository2", "created_at": "2011-05-08T09:15:20Z" } ], "top_day": {<br>
 * "day": "2011-04-22", "occurrences": { "owner1/repository1": 2,<br>
 * "owner2/repository2": 0 } } }<br>
 *<br>
 * <br>
 * 
 * @author markmorris
 * @version 1.0
 * 
 */
public class Main {

    /**
     * 
     * main calls CommandlinProcessor to run the searches async concurrently
     * using {@code CompletableFutures}.
     * 
     * @param args String array with Github repositories with the format
     * "owner/repository"
     *
     */
    public static void main(String[] args) {
        //System.out.println("Let's code!");
    	// Party time, i mean command line party -- long live the prompt :)
		CommandLineProcessor cp = new CommandLineProcessor();
		try {
			cp.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

    }

}
