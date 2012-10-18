package entity;

public class Choice {

private String name;
private int order;

	public Choice()
	{
		
	}
	
	public Choice(String name, int order)
	{
		this.name = name;
		this.order = order;
	}
	public int getOrder()
	{
		return this.order;
	}
	public void setOrder(int order)
	{
		this.order = order;
	}
	public String getName()
	{
		return this.name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public boolean equal(Object o)
	{
		if(this.name.equals(((Choice) o).getName()) && this.order == ((Choice) o).getOrder())
		{
			return true;
		}
		return false;		
	}
}
