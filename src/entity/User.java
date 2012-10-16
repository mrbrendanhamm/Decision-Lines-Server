package entity;

public class User {
	private String userid;
	private String password;
	
	public User()
	{
		
	}
	public User(String userid, String password)
	{
		this.userid = userid;
		this.password = password;
	}
	public String getUser()
	{
		return this.userid;
	}
	public void setUser(String userid)
	{
		this.userid = userid;
	}
	public String getPassword()
	{
		return this.password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
}
