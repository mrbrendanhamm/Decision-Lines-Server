package entity;

import java.util.ArrayList;

public class User {
	private String userid;
	private String password;
	private int position;
	private String clientStateId;
	
	public User()
	{
		clientStateId = "";
	}
	public User(String userid, String password, int position)
	{
		this.userid = userid;
		this.password = password;
		this.position = position;
		clientStateId = "";
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
	
	public String getClientStateId() { return clientStateId; } 
	public void setClientStateId(String newId) { clientStateId = new String(newId); } 
	
	public int getPosition() { return position; }
	public void setPosition(int position) { this.position = position; }
	
	@Override
	public int hashCode() {
		return userid.hashCode() + 31;
	}
	
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
