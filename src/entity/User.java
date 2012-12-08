package entity;

public class User {
	// The User_ID of User
	private String userid;
	// The password 
	private String password;
	// The position of the player.  This value is used to determine the order of turns in RoundRobin
	private int position;
	
	private int edgesRemaining; 
	
	// The id of the ClientState of a client who has connected to the DLE using a specific user name
	private String clientStateId;
	
	/**
	 * Default constructor
	 */
	public User()
	{
		clientStateId = "";
	}
	
	/**
	 * This constructor is used when initializing the userid, password and position
	 * 
	 * @param userid - the User_Id of the User
	 * @param password - the Password of the User
	 * @param position - the Position of the User
	 */
	public User(String userid, String password, int position, int edgesRemaining)
	{
		this.userid = userid;
		this.password = password;
		this.position = position;
		this.edgesRemaining = edgesRemaining;
		clientStateId = "";
	}
	
	/**
	 * This method is to get the private attribute User_ID of User
	 * 
	 * @return the User_ID of the User
	 */
	public String getUser()
	{
		return this.userid;
	}
	
	/**
	 * This method is to set the private attribute User_ID of User
	 * 
	 * @param userid - the User_ID of User
	 */
	public void setUser(String userid)
	{
		this.userid = userid;
	}
	
	/**
	 * This method is to get the private attribute Password of User
	 * 
	 * @return the Password of the User
	 */
	public String getPassword()
	{
		return this.password;
	}
	
	/**
	 * This method is to set the private attribute Password of User
	 * 
	 * @param password - the Password of User
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * This method is to get the private attribute ClientState of User
	 * 
	 * @return the clientStateId of the User
	 */
	public String getClientStateId() { return clientStateId; } 
	
	/**
	 * This method associates a ClientState with a User.  This linking can be later used to determine the user from
	 * the client state, or determine if any clients are connected to a DLE.
	 * 
	 * @param newId - the clientstate id of the connected client associated with this User
	 */
	public void setClientStateId(String newId) { clientStateId = new String(newId); } 
	
	/**
	 * This method is to get the private attribute Position of User
	 * 
	 * @return the Position of the User
	 */
	public int getPosition() { return position; }
	
	/**
	 * This method is to set the private attribute Position of User
	 * 
	 * @param position - the Position of User
	 */
	public void setPosition(int position) { this.position = position; }
	
	
	public boolean canAddEdgeInAsynch() {
		if (edgesRemaining <= 0) 
			return false;
		return true;
	}
	
	/**
	 *  This method is to reduce by 1 the number of edges a User has left to add
	 */
	public void decrementEdgesRemaining() {
		edgesRemaining--;
	}
	/**
	 * This method is used to increase by 1 the number of edges a User has left to add.
	 * This is used only in the event that a user is kicked from a RR event
	 */
	public void incrementEdgesRemaining(){
		edgesRemaining++;
	}
	
	/**
	 * This method returns the number of edges a user has left to add
	 * @return
	 */
	public int getEdgesRemaining(){
		return edgesRemaining;
	}
	
	/**
	 * This method sets the hash code used for hashing function.  May no longer be used as we have since moved away
	 * from a Hashmap storage for users.
	 * 
	 * @return a hash code that serves as a unique identifier for this User.
	 */
	@Override
	public int hashCode() {
		return userid.hashCode() + 31;
	}
	
	/**
	 * This method is to check whether two Users are the same
	 * 
	 * @param o - the object compared to this User
	 * @return True if they are the same User
	 * 		   False otherwise
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null) 
			return false;
		
		if (!(o instanceof User))
			return false;

		User tmp = (User) o;
		if (tmp.getUser().equals(this.userid)) 
			return true;
		
		return false;		
	}
}
