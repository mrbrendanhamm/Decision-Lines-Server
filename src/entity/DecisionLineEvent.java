package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DecisionLineEvent {
	public static enum EventType { OPEN, CLOSED, FINISHED }; 
	public static enum Behavior { ASYNCHRONOUS, ROUNDROBIN }; 
	
	private String uniqueId;
	private String question;
	private int numberOfChoice;
	private int numberOfEdge;
	private HashMap<User,ArrayList<Edge>> usersAndEdges = null;
	private User currentTurn;
	private EventType myType;
	private Behavior myBehavior;
	private ArrayList<Choice> choices = null;;
	private String moderator;
	private Choice decision; //decisions should be an array list
	ArrayList<String> connectedClientIds = null;
	
	public DecisionLineEvent()
	{
		this.usersAndEdges = new HashMap<User,ArrayList<Edge>>();
		this.choices = new ArrayList<Choice>();
	}
	
	/**
	 * This constructor is only used when attempting to locate a DecisionLineEvent within an ArrayList
	 * @param uniqueId
	 */
	public DecisionLineEvent(String uniqueId) {
		this.uniqueId = uniqueId;
		this.usersAndEdges = new HashMap<User,ArrayList<Edge>>();
		this.choices = new ArrayList<Choice>();
	}
	
	public DecisionLineEvent(String uniqueId,String question,int numberOfChoice, int numberOfEdge, EventType newType, Behavior newBehavior)
	{
		this.usersAndEdges = new HashMap<User,ArrayList<Edge>>();
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

	public void setBehavior(Behavior newBehavior) { myBehavior = newBehavior; }
	public Behavior getBehavior() { return myBehavior; }
		
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
	public ArrayList<String> getConnectedClients() {
		if (connectedClientIds == null)
			connectedClientIds = new ArrayList<String>();
		
		return connectedClientIds;
	}
	
	public HashMap<User,ArrayList<Edge>> getUsersAndEdges()
	{
		if (usersAndEdges == null) 
			usersAndEdges = new HashMap<User,ArrayList<Edge>>();

		return this.usersAndEdges;
	}
	public User getCurrentTurn()
	{
		return this.currentTurn;
	}
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
	public boolean getIsClosed()
	{
		if (myType == EventType.CLOSED || myType == EventType.FINISHED)
			return true;
		else
			return false;
	}
	public Choice getDecision()
	{
		return this.decision;
	}
	public boolean getIsFinished()
	{
		if (myType == EventType.FINISHED) 
			return true;
		else 
			return false;
	}
	public boolean canAddChoice()
	{
		return (this.choices.size() <  this.numberOfChoice);
	}
	private ArrayList<Edge> getEdgesList()
	{
		ArrayList<Edge> edges = new ArrayList<Edge>();
		Iterator<Entry<User, ArrayList<Edge>>> it = this.usersAndEdges.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<User, ArrayList<Edge>> entry = (Entry<User, ArrayList<Edge>>) it.next();
			ArrayList<Edge> edgestmp = entry.getValue();
			edges.addAll(edgestmp);
		}
		return edges;
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
		ArrayList<Edge> edges = this.getEdgesList();
		for(Choice choice : this.choices)
		{
			preOrder = choice.getOrder();
			curOrder = this.getClosestEdge(preOrder, curHeight, edges);
			while(preOrder != curOrder)
			{
				preOrder = curOrder;
				curOrder = this.getClosestEdge(preOrder, curHeight, edges);
			}
			choice.setFinalDecisionOrder(curOrder);
		}
	}
	/**
	 * This is required for this object to exist in an ArrayList.  Essentially I am stating
	 * that the uniqueId field the way to uniquely identify this object.  
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
