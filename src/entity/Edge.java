package entity;

public class Edge {
	// The left Choice of the Edge
	private Choice leftChoice;
	// The right Choice of the Edge
	private Choice rightChoice;
	// The height of the Edge
	private int height;
	
	/**
	 * Default constructor
	 */
	public Edge()
	{
	}
	
	/**
	 * This constructor is used when initializing the leftChoice, rightChoice and height
	 * 
	 * @param Choice leftChoice - the left Choice of the Edge
	 * @param Choice rightChoice - the right Choice of the Edge
	 * @param int height - the height of Edge
	 */
	public Edge(Choice leftChoice, Choice rigthChoice, int height)
	{
		this.leftChoice = leftChoice;
		this.rightChoice = rigthChoice;
		this.height = height;
	}
	
	/**
	 * This method is to set the private attribute leftChoice and rightChoice of Edge
	 * 
	 * @param Choice leftChoice - the left choice of the Edge
	 * @param Choice rightChoice - the right choice of the Edge
	 */
	public void setChoices(Choice leftChoice, Choice rightChoice)
	{
		this.leftChoice = leftChoice;
		this.rightChoice = rightChoice;
	}
	
	/**
	 * This method is to get the private attribute leftChoice of Edge
	 * 
	 * @return the leftChoice of Edge
	 */
	public Choice getLeftChoice()
	{
		return this.leftChoice;
	}
	
	/**
	 * This method is to get the private attribute rightChoice of Edge
	 * 
	 * @return the rightChoice of Edge
	 */
	public Choice getRightChoice()
	{
		return this.rightChoice;
	}
	
	/**
	 * This method is to set the private attribute height of Edge
	 * 
	 * @param int height - the height of the Edge
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 * This method is to get the private attribute height of Edge
	 * 
	 * @return the height of Edge
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * This method is to check whether this Edge has the choice of a certain order
	 * 
	 * @return True if yes, false if no
	 */
	public boolean hasChoice(int order)
	{
		if(this.leftChoice.getOrder() == order || this.rightChoice.getOrder() == order)
		{
			return true;
		}
		return false;
	}
}
