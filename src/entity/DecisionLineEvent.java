package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DecisionLineEvent {
	public static enum EventType { OPEN, CLOSED, FINISHED, ERROR }; 
	public static enum Behavior { ASYNCHRONOUS, ROUNDROBIN, ERROR }; 
	
	private String uniqueId;
	private String question;
	private int numberOfChoice;
	private int numberOfEdge;
	//private HashMap<User,ArrayList<Edge>> usersAndEdges = null;
	private ArrayList<Edge> edges = null;
	private ArrayList<User> users = null;
	private User currentTurn;
	private EventType myType;
	private Behavior myBehavior;
	private ArrayList<Choice> choices = null;;
	private String moderator;
	private Choice decision; //decisions should be an array list
	
	public DecisionLineEvent()
	{
		this.edges = new ArrayList<Edge>();
		this.users = new ArrayList<User>();
		this.choices = new ArrayList<Choice>();
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
	public void setChoices(Choice choice)
	{
		this.choices.add(choice);
	}
	
	//decisions have to be changed to be an ordering 
	public void setDecision(Choice decision)
	{
		this.decision = decision;
	}
	public void setModerator(String moderator)
	{
		this.moderator = moderator;
	}
	public void setCurrentTurn(User currentTurn)
	{
		this.currentTurn = currentTurn;
	}
	public void setType(EventType type){
		this.myType = type;
	}
	public String getUniqueId()
	{
		return this.uniqueId;
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
		
	public String getQuestion()
	{
		return this.question;
	}
	public int getNumberOfChoice()
	{
		return this.numberOfChoice;
	}
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
	
	public ArrayList<Edge> getEdges()
	{
		if (this.edges == null) 
			this.edges = new ArrayList<Edge>();

		return this.edges;
	}
	public ArrayList<User> getUsers()
	{
		if (this.users == null) 
			this.users = new ArrayList<User>();

		return this.users;
	}
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
	
	public ArrayList<Choice> getChoices()
	{
		if (choices == null)
			this.choices = new ArrayList<Choice>();
		
		return this.choices;
	}
	public String getModerator()
	{
		return this.moderator;
	}
	
	public boolean canAddChoice()
	{
		return (this.choices.size() <  this.numberOfChoice);
	}
	public boolean addEdge(Edge edge)
	{
		if(canAddEdge(edge))
		{
			this.edges.add(edge);
			return true;
		}
		return false;
	}
	public boolean addChoice(entity.Choice choice)
	{
		if(this.canAddChoice())
		{
			this.choices.add(choice);
			return true;
		}
		return false;
	}
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
	private boolean canAddEdge(Edge edge)
	{
		int height = edge.getHeight();
		for(Edge edgeT: this.edges)
		{
			if(Math.abs(edgeT.getHeight() - height) < 7)	
			{
				return false;
			}
		}
		return true;
	}
	private int getClosestEdge(int order, int height, ArrayList<Edge> edges)
	{
		int min = Integer.MAX_VALUE;
		int result = order;
		for(Edge edge : edges)
		{
			if(edge.hasChoice(order) && edge.getHeight() > height)
			{
				int diff = edge.getHeight() - height;
				if(diff < min)
				{
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
	public void getFinalOrder()
	{
		int preOrder = -1;
		int curOrder = -1;
		int curHeight = 0;
		for(Choice choice : this.choices)
		{
			preOrder = choice.getOrder();
			curOrder = this.getClosestEdge(preOrder, curHeight, this.edges);
			while(preOrder != curOrder)
			{
				preOrder = curOrder;
				curOrder = this.getClosestEdge(preOrder, curHeight, this.edges);
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
