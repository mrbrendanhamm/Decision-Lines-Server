package entity;


import entity.DecisionLineEvent.Behavior;
import junit.framework.TestCase;

public class DecisionLineEventTest extends TestCase
{
	private DecisionLineEvent DLE;
	private User user = new User("A","B",1);
	private Choice choice = new Choice("When to Eat", 1);
	private Choice choice2 = new Choice("When to Meet", 2);
	protected void setUp() throws Exception
	{
		DLE = new DecisionLineEvent();
	}

	public void testDecisionLineEventString()
	{
		DLE = new DecisionLineEvent("A");
		assert(DLE.getUniqueId().equals("A"));
	}
	
	public void testDecisionLineEventStringStringIntIntEventTypeBehavior()
	{
		DLE = new DecisionLineEvent("A","When to Meet",4,4,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.getUniqueId().equals("A"));
		assert(DLE.getQuestion().equals("When to Meet"));
		assert(DLE.getNumberOfChoice() == 4);
		assert(DLE.getNumberOfEdge() == 4);
		assert(DLE.getEventType() == DecisionLineEvent.EventType.CLOSED);
		assert(DLE.getBehavior() == Behavior.ASYNCHRONOUS);
	}


	public void testGetBehavior()
	{
		DLE.setBehavior(Behavior.ASYNCHRONOUS);
		assert(DLE.getBehavior() == Behavior.ASYNCHRONOUS);
	}

	public void testgetUserFromClientId()
	{
		DLE.addUser(user);
		DLE.addClientConnection("A", "C");
		assert(DLE.getUserFromClientId("C").getUser().equals("A"));
		assert(DLE.getUserFromClientId("A") == null);
	}

	public void testGetEdges()
	{
		Edge edge = new Edge(choice,choice2,1);
		Edge edge2 = new Edge(choice,choice2,10);
		DLE.addEdge(edge);
		DLE.addEdge(edge2);
		assert(DLE.getEdges().get(0).hasChoice(1));
		assert(DLE.getEdges().get(1).hasChoice(2));
	}

	public void testGetUsers()
	{
		DLE.addUser(user);
		assert(DLE.getUsers().get(0).getUser().equals("A"));
	}

	public void testGetCurrentTurn()
	{
		DLE.setCurrentTurn(user);
		assert(DLE.getCurrentTurn().getUser().equals("A")); 
	}

	public void testGetEventType()
	{
		DLE.setType(DecisionLineEvent.EventType.CLOSED);
		assert(DLE.getEventType() == DecisionLineEvent.EventType.CLOSED);
	}

	public void testGetChoices()
	{
		DLE.addChoice(choice);
		DLE.addChoice(choice2);
		assert(DLE.getChoices().get(0).equals(choice));
		assert(DLE.getChoices().get(1).equals(choice2));
	}

	public void testGetModerator()
	{
		DLE.setModerator("A");
		assert(DLE.getModerator().equals("A"));
	}

	public void testCanAddChoice()
	{
		DLE = new DecisionLineEvent("A","When to Meet",1,1,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.canAddChoice());
		DLE.addChoice(choice);
		assert(!DLE.canAddChoice());
	}
	
	public void testGetQuestion()
	{
		DLE = new DecisionLineEvent("A","When to Meet",4,4,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.getQuestion().equals("When to Meet"));
	}
	
	public void testGetNumberOfChoice()
	{
		DLE = new DecisionLineEvent("A","When to Meet",4,4,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.getNumberOfChoice() == 4);
	}
	
	public void testGetNumberOfEdge()
	{
		DLE = new DecisionLineEvent("A","When to Meet",4,4,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.getNumberOfEdge() == 4);
	}
	
	public void testConnectedClientCount()
	{
		assert(DLE.connectedClientCount() == 0);
		DLE.addUser(user);
		DLE.addClientConnection("A", "C");
		assert(DLE.connectedClientCount() == 1);
	}
	
	public void testDisconnectClientId()
	{
		DLE.addUser(user);
		DLE.addClientConnection("A", "C");
		DLE.disconnectClientId("C");
		assert(DLE.connectedClientCount() == 0);
	}

	public void testGetFinalOrder()
	{
		Choice choice3 = new Choice("When to Face", 3);
		Edge edge = new Edge(choice,choice2,1);
		Edge edge2 = new Edge(choice,choice2,10);
		Edge edge3 = new Edge(choice2,choice3,18);
		DLE.addChoice(choice);
		DLE.addChoice(choice2);
		DLE.addChoice(choice3);
		DLE.addEdge(edge);
		DLE.addEdge(edge2);
		DLE.addEdge(edge3);
		DLE.getFinalOrder();
		assert(DLE.getChoice(0).getFinalDecisionOrder() == 0);
		assert(DLE.getChoice(1).getFinalDecisionOrder() == 2);
		assert(DLE.getChoice(2).getFinalDecisionOrder() == 1);
	}

	public void testEqualsObject()
	{
		DLE = new DecisionLineEvent("A","When to Meet",1,1,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		DecisionLineEvent DLE2 = new DecisionLineEvent("A","When to Meet",1,1,DecisionLineEvent.EventType.CLOSED,Behavior.ASYNCHRONOUS);
		assert(DLE.equals(DLE2));
	}

}
