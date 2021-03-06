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
	private int numberOfChoices;
	// The number of Edges of the DLE
	private int numberOfEdges;
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
	DecisionLineEvent()
	{
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
		createDate = new Date();
		myType = EventType.ERROR;
		myBehavior = Behavior.ERROR;
		createDate = new java.util.Date();
		question = "";
		moderator = "undefined";
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
		createDate = new java.util.Date();
		myType = EventType.ERROR;
		myBehavior = Behavior.ERROR;
		question = "";
		moderator = "undefined";
	}
	
	/**
	 * This constructor is used when initializing the uniqueId, question, numberOfChoice, numberOfEdge, EventType and Behavior
	 * 
	 * @param uniqueId - the uniqueId of the DLE
	 * @param question - the question of the DLE
	 * @param numberOfChoice - the number of the Choices of the DLE
	 * @param numberOfEdge - the number of the Edges of the DLE
	 * @param newType - the EventType of the DLE
	 * @param newBehavior - the Behavior of the DLE
	 */
	public DecisionLineEvent(String uniqueId,String question,int numberOfChoices, int numberOfEdges, EventType newType, Behavior newBehavior)
	{
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
		this.question = question;
		this.uniqueId = uniqueId;
		this.numberOfChoices = numberOfChoices;
		this.numberOfEdges = numberOfEdges;
		this.myType = newType;
		this.myBehavior = newBehavior;
		createDate = new java.util.Date();
	}
	
	/**
	 * This method is to set the private attribute moderator of the DLE
	 * 
	 * @param moderator - the User_ID of moderator added into the DLE
	 */
	public void setModerator(String moderator)
	{
		this.moderator = moderator;
	}
	
	/**
	 * This method is to set the private attribute currentTurn of the DLE
	 * 
	 * @param currentTurn - the current Turn of the DLE
	 */
	public void setCurrentTurn(User currentTurn)
	{
		this.currentTurn = currentTurn;
	}
	
	/**
	 * This method is to set the private attribute MyType of the DLE
	 * 
	 * @param type - the Type of the DLE
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
	 * @param newDate - the created Date of the DLE
	 */
	public void setDate(Date newDate) {
		createDate = newDate;
	}
	/**
	 * This method is to add a new User into the ArrayList of Users of the DLE
	 * 
	 * @param user - the User added into the DLE
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
	public int getNumberOfChoices()
	{
		return this.numberOfChoices;
	}
	
	/**
	 * This method is to get the private attribute numberOfEdge of DLE
	 * 
	 * @return the number of Edges of the DLE
	 */
	public int getNumberOfEdges()
	{
		return this.numberOfEdges;
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
		return ((choices.size() < numberOfChoices) && NotChoiceWithOrder(order)); //myType.equals(EventType.OPEN) && 
	}
	
	/**
	 * This method is to add a new Edge into the ArrayList of Edge of the DLE
	 * 
	 * @param edge - the Edge added into the DLE
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

	public void determineCurrentUsersTurn() {
		if (getCurrentTurn() == null && getUsers().size() > 0) {
			int i = getEdges().size() % numberOfChoices;
			User tmpU = getUsers().get(i);
			setCurrentTurn(tmpU);
		}
	}
	
	
	/**
	 * This method is to add a new Choice into the ArrayList of Choice of the DLE.  If all choices are made then the event
	 * is changed from Open to Closed
	 * 
	 * @param choice - the choice added into the DLE
	 * @return true if the choice was added, false otherwise
	 */
	public boolean addChoice(entity.Choice choice)
	{
		if(canAddChoice(choice.getOrder()))
		{
			this.choices.add(choice);

			if (choices.size() == numberOfChoices) {
				// change game from Open to Closed, and set the current player
				myType = EventType.CLOSED;
				for (int i = 0; i < users.size(); i++)
					if (users.get(i).getPosition() == 0)
						setCurrentTurn(users.get(i));
			}
			
			return true;
		}
		return false;
	}
	
	/**
	 * This method is to get the Choice by the order
	 * 
	 * @param order - the order of the searching Choice
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
	 * @param order -  the order of the certain Choice
	 * @param height - the height limit
	 * @return the other side of the closest Edge
	 */
	private int[] getClosestEdge(int order, int height)
	{		
		int min = Integer.MAX_VALUE;
		//result[0] = new Order of Choice result[1] = new Height
		int result[] = new int[2];
		result[0] = -1;
		// Go through the each Edge
		for(Edge edge : this.edges)
		{
			// Check whether the Edge is related to the Choice and below the height
			if(edge.hasChoice(order) && edge.getHeight() > height)
			{
				// Calculate the diff
				int diff = edge.getHeight() - height;
				if(diff < min)
				{
					// Update the min diff and return order of the Choice
					if(edge.getLeftChoice().getOrder() != order)
					{
						result[0] = edge.getLeftChoice().getOrder();
					}else
					{
						result[0] = edge.getRightChoice().getOrder();
					}
					result[1] = edge.getHeight();
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
		int CurandHeight[] = new int[2];
		int curHeight = 0;
		// Calculate each Choice
		for(Choice choice : this.choices)
		{
			preOrder = choice.getOrder();
			CurandHeight = this.getClosestEdge(preOrder, curHeight);
			curOrder = CurandHeight[0];
			curHeight = CurandHeight[1];
			// Go through the path
			while(curOrder > 0)
			{
				preOrder = curOrder;
				CurandHeight = this.getClosestEdge(preOrder, curHeight);
				curOrder = CurandHeight[0];
				curHeight = CurandHeight[1];
			}
			choice.setFinalDecisionOrder(preOrder);
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

	public Boolean removeUser(User user) {
		Boolean retVal = false;
		for (int i=0; i<this.users.size();i++){
			if(this.users.get(i).equals(user)){
				this.users.remove(i);
				retVal=true;
			}
		}
		
		return retVal;
	}
}
