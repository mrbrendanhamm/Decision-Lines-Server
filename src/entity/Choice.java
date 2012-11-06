package entity;

public class Choice {

	// The name of Choice
	private String name;
	// The order of Choice
	private int order;
	// The final order when the DLE is finished
	private int finalDecisionOrder;

	/**
	 * Default constructor
	 */
	public Choice()
	{
		
	}
	
	/**
	 * This constructor is used when initializing the name and order
	 * 
	 * @param name - the name of Choice
	 * @param order - the order of Choice
	 * Set finalDesicisonOrder as -1
	 */
	public Choice(String name, int order)
	{
		this.name = name;
		this.order = order;
		this.finalDecisionOrder = -1;
	}
	
	/**
	 * This constructor is used when initializing the name, order and finalDecisionOrder
	 * 
	 * @param name - the name of Choice
	 * @param order - the order of Choice
	 * @param finalDecisionOrder - the final decision order of Choice when the DLE is finished
	 */
	public Choice(String name, int order, int finalDecisionOrder)
	{
		this.name = name;
		this.order = order;
		this.finalDecisionOrder = finalDecisionOrder;
	}
	
	/**
	 * This method is to get the private attribute order of Choice
	 * 
	 * @return the order of Choice
	 */
	public int getOrder()
	{
		return this.order;
	}
	
	/**
	 * This method is to set the private attribute order of Choice
	 * 
	 * @param order - the order of Choice
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}
	
	/**
	 * This method is to get the private attribute name of Choice
	 * 
	 * @return the name of Choice
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * This method is to set the private attribute name of Choice
	 * 
	 * @param name - the name of Choice
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * This method is to get the private attribute finalDecisionOrder of Choice
	 * 
	 * @return the finalDecisionOrder of Choice
	 */
	public int getFinalDecisionOrder() {
		return finalDecisionOrder;
	}
	
	/**
	 * This method is to set the private attribute finalDecisionOrder of Choice
	 * 
	 * @param finalDecisionOrder - the finalDecisionOrder of Choice
	 */
	public void setFinalDecisionOrder(int finalDecisionOrder) {
		this.finalDecisionOrder = finalDecisionOrder;
	}
	
	/**
	 * This method is to check whether two Choices are the same
	 * 
	 * @param o - the object compared to this Choice
	 * @return True if they are the same Choice
	 * 		   False otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		
		if (!(o instanceof Choice))
			return false;

		Choice tmp = (Choice) o;
		
		if (tmp.getOrder() == this.order) 
			return true;
		
		return false;
	}
}
