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
}
