package entity;

import java.util.ArrayList;
import java.util.Date;


public class DecisionLineEvent {
	// EventType enum class
	public static enum EventType { OPEN, CLOSED, FINISHED, ERROR }; 
	// DLE Behavior enum class
	public static enum Behavior { ASYNCHRONOUS, ROUNDROBIN, ERROR }; 
	// The uniqueID of the DLE
	private String uniqueId;
	// The question of the DLE
	private String question;
	// The number of Choices of the DLE.  Also corresponds to the maximum number of users that can be connected
	private int numberOfChoice;
	// The number of Edges of the DLE
	private int numberOfEdge;
	// The ArrayList of all exist edges of the DLE
	private ArrayList<Edge> edges = null;
	// The ArrayList of all exist Users of the DLE
	private ArrayList<User> users = null;
	// The current Turn of the DLE
	private User currentTurn;
	// The Event Type of the DLE
	private EventType myType;
	// The Behavior of the DLE
	private Behavior myBehavior;
	// The ArrayList of all choices of the DLE
	private ArrayList<Choice> choices = null;;
	// The Unique_ID of the moderator of the DLE
	private String moderator;
	// The Date of the DLE created
	private Date createDate;
	
	/**
	 * Default constructor
	 */
	public DecisionLineEvent()
	{
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
		createDate = new Date();
		myType = EventType.ERROR;
		myBehavior = Behavior.ERROR;
	}
	
	/**
	 * This constructor is only used when attempting to locate a DecisionLineEvent within an ArrayList
	 * @param uniqueId - the unique identifier of the DecisionLineEvent
	 */
	public DecisionLineEvent(String uniqueId) {
		this.uniqueId = uniqueId;
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
		myType = EventType.ERROR;
		myBehavior = Behavior.ERROR;
	}
	
	/**
	 * This constructor is used when initializing the uniqueId, question, numberOfChoice, numberOfEdge, EventType and Behavior
	 * 
	 * @param String uniqueId - the uniqueId of the DLE
	 * @param String question - the question of the DLE
	 * @param int numberOfChoice - the number of the Choices of the DLE
	 * @param int numberofEdge - the number of the Edges of the DLE
	 * @param EventType newType - the EventType of the DLE
	 * @param Behavior newBehavior - the Behavior of the DLE
	 */
	public DecisionLineEvent(String uniqueId,String question,int numberOfChoice, int numberOfEdge, EventType newType, Behavior newBehavior)
	{
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
		this.question = question;
		this.uniqueId = uniqueId;
		this.numberOfChoice = numberOfChoice;
		this.numberOfEdge = numberOfEdge;
		this.myType = newType;
		this.myBehavior = newBehavior;
	}
	
	/**
	 * This method is to set the private attribute moderator of the DLE
	 * 
	 * @param String moderator - the User_ID of moderator added into the DLE
	 */
	public void setModerator(String moderator)
	{
		this.moderator = moderator;
	}
	
	/**
	 * This method is to set the private attribute currentTurn of the DLE
	 * 
	 * @param User currentTurn - the current Turn of the DLE
	 */
	public void setCurrentTurn(User currentTurn)
	{
		this.currentTurn = currentTurn;
	}
	
	/**
	 * This method is to set the private attribute MyType of the DLE
	 * 
	 * @param EventType type - the Type of the DLE
	 */
	public void setType(EventType type){
		this.myType = type;
	}
	
	/**
	 * This method is to get the private attribute uniqueId of the DLE
	 * 
	 * @return the uniqueId of the DLE
	 */
	public String getUniqueId()
	{
		return this.uniqueId;
	}
	
	/**
	 * This method is to get the private attribute createDate of the DLE
	 * 
	 * @return the createDate of the DLE
	 */
	public Date getDate() {
		return createDate;
	}
	
	/**
	 * This method is to set the private attribute createDate of the DLE
	 * 
	 * @param Date newDate - the created Date of the DLE
	 */
	public void setDate(Date newDate) {
		createDate = newDate;
	}
	/**
	 * This method is to add a new User into the ArrayList of Users of the DLE
	 * 
	 * @param User user - the User added into the DLE
	 */
	public void addUser(User user)
	{
		this.users.add(user);
	}
	/**
	 * This method sets the behavior of the Decision Line Event based on the Behavior enumeration.  
	 * 
	 * @param newBehavior - the new behavior type, either ROUNDROBIN or ASYNCHRONOUS
	 */
	public void setBehavior(Behavior newBehavior) { 
		myBehavior = newBehavior; 
	}
	
	/**
	 * This methods returns the current Behavior of the DLE.
	 * 
	 * @return Behavior.ROUNDROBIN if this is a round robin DLE, or Behavior.ASYNCHRONOUS if this is an asynchronous DLE
	 */
	public Behavior getBehavior() { 
		return myBehavior; 
	}
		
	/**
	 * This method is to get the private attribute question of DLE
	 * 
	 * @return the question of DLE
	 */
	public String getQuestion()
	{
		return this.question;
	}
	
	/**
	 * This method is to get the private attribute numberOfChoice of the DLE
	 * 
	 * @return the number of Choices of the DLE
	 */
	public int getNumberOfChoice()
	{
		return this.numberOfChoice;
	}
	
	/**
	 * This method is to get the private attribute numberOfEdge of DLE
	 * 
	 * @return the number of Edges of the DLE
	 */
	public int getNumberOfEdge()
	{
		return this.numberOfEdge;
	}
	/**
	 * This method returns the total number of users that have a client application connected to the DLE.  This is used
	 * to determine when a DLE should be removed from memory (aka when no clients are connected).  Other uses to be determined...
	 * 
	 * @return 0 - the count of users which have a client connected.
	 */
	public int connectedClientCount() {
		int count = 0;
		
		for (int i = 0; i < getUsers().size(); i++) {
			if (!getUsers().get(i).getClientStateId().equals(""))
				count++;
		}
		return count;
	}
	
	/**
	 * This removes a client connection from an association with the User.  
	 * 
	 * @param clientId - the ClientState Id to be removed
	 * @return - always true
	 */
	public boolean disconnectClientId(String clientId) {
		for (int i = 0; i < getUsers().size(); i++) 
			if (getUsers().get(i).getClientStateId().equals(clientId)) 
				getUsers().get(i).setClientStateId("");
		
		return true;
	}
	
	/**
	 * This method associates a user with a ClientState Id.  
	 * 
	 * @param userName - the UserName that will be associated with the ClientState Id
	 * @param clientId - The ClientState Id being associated
	 * @return - true if successfully associated, false otherwose
	 */
	public boolean addClientConnection(String userName, String clientId) {
		for (int i = 0; i < getUsers().size(); i++) 
			if (getUsers().get(i).getUser().equals(userName)) {
				getUsers().get(i).setClientStateId(clientId);
				return true;
			}

		return false;
	}
	
	/**
	 * This method retrieves a User from the ClientState Id provided
	 * 
	 * @param clientId - The ClientState Id being searched for.
	 * @return - The User with the associated ClientState Id, or null otherwise
	 */
	public User getUserFromClientId(String clientId) {
		for (int i = 0; i < getUsers().size(); i++) 
			if (getUsers().get(i).getClientStateId().equals(clientId)) 
				return getUsers().get(i);
		
		return null;
	}
	
	/**
	 * This method is to get the private attribute edges of DLE
	 * 
	 * @return the ArrayList of Edges of the DLE
	 */
	public ArrayList<Edge> getEdges()
	{
		if (this.edges == null) 
			this.edges = new ArrayList<Edge>();

		return this.edges;
	}
	
	/**
	 * This method is to get the private attribute users of DLE
	 * 
	 * @return the ArrayList of User of the DLE
	 */
	public ArrayList<User> getUsers()
	{
		if (this.users == null) 
			this.users = new ArrayList<User>();

		return this.users;
	}
	
	/**
	 * This method is to get the private attribute currentTurn of DLE
	 * 
	 * @return the current Turn of the DLE
	 */
	public User getCurrentTurn()
	{
		return this.currentTurn;
	}
	
	/**
	 * This method returns the current status of the event
	 * 
	 * @return 	EventType.OPEN - the event is in the choice setting phase
	 * 			EventType.CLOSED - the event has all choices selected and is ready to play edges
	 * 			EventType.FINISHED - Edge playing has concluded and a final decision has been reached.
	 */
	public EventType getEventType() {
		return myType;
	}
	
	/**
	 * This method is to get the private attribute choices of DLE
	 * 
	 * @return the ArrayList of Choices of the DLE
	 */
	public ArrayList<Choice> getChoices()
	{
		if (choices == null)
			this.choices = new ArrayList<Choice>();
		
		return this.choices;
	}
	
	/**
	 * This method is to get the private attribute moderator of DLE
	 * 
	 * @return the User_ID of the Modetator of the DLE
	 */
	public String getModerator()
	{
		return this.moderator;
	}
	
	/**
	 * This method is to check whether new Edge can be added
	 * 
	 * @return 
	 * 1 - Valid Edge
	 * 2 - The DLE is in finished status
	 * 3 - The height of new Edge is invalid
	 * 4 - The left Choice is not really to the left of the right Choice
	 */
	private int canAddEdge(Edge edge)
	{
		int height = edge.getHeight();
		//check the status
		if(this.myType.equals(EventType.FINISHED))
		{
			return 2;
		}
		//check the height
		for(Edge edgeT: this.edges)
		{
			if(Math.abs(edgeT.getHeight() - height) < 7)	
			{
				return 3;
			}
		}
		//check the position of right Choice
		for(int i = 0; i < this.choices.size(); i++)
		{
			if(this.choices.get(i).equals(edge.getLeftChoice()))
			{
				if(!this.choices.get(i+1).equals(edge.getRightChoice()))
				{
					return 4;
				}
				break;
			}
		}
		return 1;
	}
	
	/**
	 * This method is to check whether the choice with certain order exists in this DLE
	 * 
	 * @param int order - the Order of the Choice
	 * @return True if the choice doesn't exist, false otherwise
	 */
	private boolean NotChoiceWithOrder(int order)
	{
		for(Choice choice : this.choices)
		{
			if(choice.getOrder() == order)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method is to check whether new choice can be added
	 * 
	 * @param int order - the Order of the Choice
	 * @return True if new choice can be added, false otherwise
	 */
	boolean canAddChoice(int order)
	{
		return ((this.choices.size() <  this.numberOfChoice) && this.myType.equals(EventType.OPEN) && NotChoiceWithOrder(order));
	}
	
	/**
	 * This method is to add a new Edge into the ArrayList of Edge of the DLE
	 * 
	 * @param Edge edge - the Edge added into the DLE
	 * @return 
	 * 1 - Success
	 * 2 - The DLE is in finished status
	 * 3 - The height of new Edge is invalid
	 * 4 - The left Choice is not really to the left of the right Choice
	 */
	public int addEdge(Edge edge)
	{
		int re = canAddEdge(edge);
		if(re == 1)
		{
			this.edges.add(edge);
			
		}
		return re;
	}

	
	/**
	 * This method is to add a new Choice into the ArrayList of Choice of the DLE
	 * 
	 * @param Choice choice - the choice added into the DLE
	 */
	public boolean addChoice(entity.Choice choice)
	{
		if(this.canAddChoice(choice.getOrder()))
		{
			this.choices.add(choice);
			return true;
		}
		return false;
	}
	
	/**
	 * This method is to get the Choice by the order
	 * 
	 * @param int order - the order of the searching Choice
	 * @return the Choice with the order
	 */
	public Choice getChoice(int order)
	{
		for(Choice choice : this.choices)
		{
			if(choice.getOrder() == order)
			{
				return choice;
			}
		}
		return null;
	}
	
	/**
	 * This method is to get the closest Edge related to the certain Choice and below the certain height
	 * 
	 * @param int order -  the order of the certain Choice
	 * @param int height - the height limit
	 * @return the other side of the closest Edge
	 */
	private int getClosestEdge(int order, int height)
	{
		int min = Integer.MAX_VALUE;
		int result = order;
		// Go through the each Edge
		for(Edge edge : this.edges)
		{
			// Check whethe the Edge is related to the Choice and below the height
			if(edge.hasChoice(order) && edge.getHeight() > height)
			{
				// Caulate the diff
				int diff = edge.getHeight() - height;
				if(diff < min)
				{
					// Update the min diff and return order of the Choice
					if(edge.getLeftChoice().getOrder() != order)
					{
						result = edge.getLeftChoice().getOrder();
					}else
					{
						result = edge.getRightChoice().getOrder();
					}
					min = diff;					
				}
			}
		}
		return result;
	}
	
	/**
	 * This method is to get final order for each Choice.
	 * Update the FinalDecisionOrder attribute of each Choice.
	 * 
	 */
	public void getFinalOrder()
	{
		int preOrder = -1;
		int curOrder = -1;
		int curHeight = 0;
		// Calculate each Choice
		for(Choice choice : this.choices)
		{
			preOrder = choice.getOrder();
			curOrder = this.getClosestEdge(preOrder, curHeight);
			// Go through the path
			while(preOrder != curOrder)
			{
				preOrder = curOrder;
				curOrder = this.getClosestEdge(preOrder, curHeight);
			}
			choice.setFinalDecisionOrder(curOrder);
		}
	}
	
	/**
	 * This is required for this object to exist in an ArrayList.  Essentially I am stating
	 * that the uniqueId field the way to uniquely identify this object.  
	 * 
	 * @param o - the object being compared
	 * @return True if the objects are the same, false otherwise 
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		
		if (!(o instanceof DecisionLineEvent))
			return false;

		DecisionLineEvent tmp = (DecisionLineEvent) o;
		
		if (tmp.getUniqueId().equals(this.uniqueId)) 
			return true;
		
		return false;
	}
}
