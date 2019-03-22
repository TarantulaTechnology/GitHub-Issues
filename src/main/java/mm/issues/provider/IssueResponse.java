package mm.issues.provider;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

/**
 * 
 * IssueResponse contains a list of JsonObject 
 * 
 * @author markmorris
 * @version 1.0
 *
 */
public class IssueResponse {

	/**
	 * jsonObjects is a list of JsonObject and is used to store 
	 * the retrieved JsonObjects created from the response string.
	 */
	public List<JsonObject> jsonObjects = new ArrayList<JsonObject>();

	/**
	 * @return the jsonObjects
	 */
	public List<JsonObject> getJsonObjects() {
		return jsonObjects;
	}

	/**
	 * @param jsonObjects the jsonObjects to set
	 */
	public void setJsonObjects(List<JsonObject> jsonObjects) {
		this.jsonObjects = jsonObjects;
	}
	
	/**
	 * @param jsonObjects the jsonObjects to set
	 */
	public void addJsonObjects(List<JsonObject> jsonObjects) {
		this.jsonObjects.addAll(jsonObjects);
	}

	
}
