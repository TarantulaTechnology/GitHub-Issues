package mm.issues.model;

import java.util.Date;



/**
 * 
 * Issue is a POJO and created from the github json returned
 * from a search for repo issues.<br> 
 * <br>
 * An Issue is created while iterating over the json result. Each json
 * object is used to set the fields in our Issue object. An Issue object
 * is created at runtime and there will be one Issue object for each
 * json object. The json object contains many more fields, but we
 * only need a few for this use case, so we create a new object representing
 * the Issue for our use case.<br>
 * <br>
 * Here are the fields in the class.<br>
 * <br>
 * Issue : "id", "state", "title", "repository", "created_at" fields. <br>
 *  <br>
 * { 
 *          "id": 1
 *      ,"state": "open"
 *      ,"title": "Found a bug"
 * ,"repository": "owner1/repository1"
 * ,"created_at": "2011-04-22T13:33:48Z" 
 * }
 * }
 * 
 * @author markmorris
 * @version 1.0
 * 
 */
public class Issue {
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the repository
	 */
	public String getRespository() {
		return respository;
	}


	/**
	 * @param respository the repository to set
	 */
	public void setRespository(String respository) {
		this.respository = respository;
	}


	/**
	 * @return the created_at
	 */
	public Date getCreated_at() {
		return created_at;
	}


	/**
	 * @param created_at the created_at to set
	 */
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}


	private int id; // 1 - n
	private String state; // "open" || "closed"
	private String title; // ex. "Found a bug"
	private String respository; // "owner/repository" github convention
	private Date created_at; // ex. "2011-04-22T13:33:48Z"


	public Issue(){
		// empty constructor 
		// the fields get set while reading json object
	}

}//end Class3