package entity;

import java.util.ArrayList;
import java.util.HashMap;

public class DecisionLineEvent {
	private String uniqueId;
	private String question;
	private int numberOfChoice;
	private int numberOfEdge;
	private HashMap<User,ArrayList<Edge>> usersAndEdges;
	private User currentTurn;
	private boolean isAsynchronous;
	private ArrayList<Choice> choices;
	private User moderator;
	private boolean isClosed;
	private Choice decision;
	private boolean isFinished;
	
	public DecisionLineEvent()
	{
		this.usersAndEdges = new HashMap<User,ArrayList<Edge>>();
		this.choices = new ArrayList<Choice>();
	}
	public DecisionLineEvent(String uniqueId,String question,int numberOfChoice, int numberOfEdge, boolean isAsynchronous)
	{
		this.usersAndEdges = new HashMap<User,ArrayList<Edge>>();
		this.choices = new ArrayList<Choice>();
		this.question = question;
		this.isClosed = false;
		this.isFinished = false;
		this.uniqueId = uniqueId;
		this.numberOfChoice = numberOfChoice;
		this.numberOfEdge = numberOfEdge;
		this.isAsynchronous = isAsynchronous;
	}
	public void setChoices(Choice choice)
	{
		this.choices.add(choice);
	}
	public void setDecision(Choice decision)
	{
		this.decision = decision;
	}
	public void setModerator(User moderator)
	{
		this.moderator = moderator;
	}
	public void setCurrentTurn(User currentTurn)
	{
		this.currentTurn = currentTurn;
	}
	public String getUniqueId()
	{
		return this.uniqueId;
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
	public HashMap<User,ArrayList<Edge>> getUsersAndEdges()
	{
		return this.usersAndEdges;
	}
	public User getCurrentTurn()
	{
		return this.currentTurn;
	}
	public boolean getIsAsynchronous()
	{
		return this.isAsynchronous;
	}
	public ArrayList<Choice> getChoices()
	{
		return this.choices;
	}
	public User getModerator()
	{
		return this.moderator;
	}
	public boolean getIsClosed()
	{
		return this.isClosed;
	}
	public Choice getDecision()
	{
		return this.decision;
	}
	public boolean getIsFinished()
	{
		return this.isFinished;
	}
}
