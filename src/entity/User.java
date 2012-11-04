package entity;

public class User {
	// The User_ID of User
	private String userid;
	// The password 
	private String password;
	// The position 
	private int position;
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
	 * @param String userid - the User_Id of the User
	 * @param String password - the Password of the User
	 * @param int position - the Position of the User
	 */
	public User(String userid, String password, int position)
	{
		this.userid = userid;
		this.password = password;
		this.position = position;
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
	 * @param String User_ID - the User_ID of User
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
	 * @param String password - the Password of User
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * This method is to get the private attribute clinetStateId of User
	 * 
	 * @return the clientStateId of the User
	 */
	public String getClientStateId() { return clientStateId; } 
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
	 * @param int position - the Position of User
	 */
	public void setPosition(int position) { this.position = position; }
	
	@Override
	public int hashCode() {
		return userid.hashCode() + 31;
	}
	
	/**
	 * This method is to check whether two Users are the same
	 * 
	 * @param Object o - the object compared to this User
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
