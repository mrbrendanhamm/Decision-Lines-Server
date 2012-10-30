package entity;

public class User {
	private String userid;
	private String password;
	int position;
	
	public User()
	{
		
	}
	public User(String userid, String password, int position)
	{
		this.userid = userid;
		this.password = password;
		this.position = position;
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
