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
	
	/*
	 * Jun, I commented out your prior code because you cast the object and test the name before
	 * you've confirmed that the object is of type Choice.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		
		if (!(o instanceof Choice))
			return false;

		Choice tmp = (Choice) o;
		
		if (tmp.order == this.order) 
			return true;
		
		return false;
	}
	/*
	public boolean equal(Object o)
	{
		if(this.name.equals(((Choice) o).getName()) && this.order == ((Choice) o).getOrder())
		{
			return true;
		}
		return false;		
	}
	*/
}
