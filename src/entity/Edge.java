package entity;

public class Edge {
	private Choice choice;
	private int height;
	
	public Edge()
	{
	}
	public Edge(Choice choice, int height)
	{
		this.choice = choice;
		this.height = height;
	}
	public void setChoice(Choice choice)
	{
		this.choice = choice;
	}
	public Choice getChoice()
	{
		return this.choice;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public int getHeight()
	{
		return this.height;
	}
}
