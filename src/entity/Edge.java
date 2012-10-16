package entity;

public class Edge {
	private Choice choice;
	private int height;
	private User playedBy;
	
	public Edge()
	{
		
	}
	public Edge(Choice choice, int height, User playedBy)
	{
		this.choice = choice;
		this.height = height;
		this.playedBy = playedBy;
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
	public void setPlayedBy(User playedBy)
	{
		this.playedBy = playedBy;
	}
	public User getPlayedBy()
	{
		return this.playedBy;
	}
}
