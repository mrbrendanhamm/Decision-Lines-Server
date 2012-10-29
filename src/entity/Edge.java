package entity;

public class Edge {
	private Choice leftChoice;
	private Choice rightChoice;
	private int height;
	
	public Edge()
	{
	}
	public Edge(Choice leftChoice, Choice rigthChoice, int height)
	{
		this.leftChoice = leftChoice;
		this.rightChoice = rigthChoice;
		this.height = height;
	}
	public void setChoices(Choice leftChoice, Choice rightChoice)
	{
		this.leftChoice = leftChoice;
		this.rightChoice = rightChoice;
	}
	public Choice getLeftChoice()
	{
		return this.leftChoice;
	}
	public Choice getRightChoice()
	{
		return this.rightChoice;
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
