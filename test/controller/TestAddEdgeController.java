package controller;

import server.MockClient;
import server.Server;
import xml.Message;
import entity.Choice;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import junit.framework.TestCase;

public class TestAddEdgeController extends TestCase
{
	private AddEdgeController aec;
	private User user1;
	private User user2;
	private User user3;
	private DecisionLineEvent dle;
	private Model model;
	private MockClient client1;
	private MockClient client2;
	private MockClient client3;
	private Choice choice1;
	private Choice choice2;
	private Choice choice3;
	protected void setUp() throws Exception
	{
		if (!Message.configure("draw2choose.xsd")) {
			fail ("unable to configure protocol");
		}
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		client3 = new MockClient("c3");
		Server.register("c1", client1);
		Server.register("c2", client2);
		Server.register("c3", client3);
		ClearModelInstance.clearInstance();
		aec = new AddEdgeController();
		model = Model.getInstance();
		dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		user1 = new User("A","B",1,3);
		user1.setClientStateId("c1");
		user2 = new User("C","D",2,3);
		user2.setClientStateId("c2");
		user3 = new User("E","F",3,3);
		user3.setClientStateId("c3");
		dle.addUser(user1);
		dle.addUser(user2);
		dle.addUser(user3);
		choice1 = new Choice("When to meet",1);
		choice2 = new Choice("When to eat",2);
		choice3 = new Choice("When to play",3);
		dle.addChoice(choice1);
		dle.addChoice(choice2);
		dle.addChoice(choice3);
		dle.setCurrentTurn(user1);
		model.getDecisionLineEvents().add(dle);
	}

	public void testProcess()
	{
		// a sample failure XML message since it is not this user's turn
		String xmlString = "<request version='1.0' id='"+client2.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='2' height='1'/>"+
				"</request>";
		System.out.println(xmlString);
		Message request = new Message(xmlString);
		Message response = aec.process(client1, request);
		System.out.println(response.toString());
		
		// a sample success XML message
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='2' height='1'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = aec.process(client1, request);
		System.out.println(response.toString());
		
		// a sample success XML message after changing the current turn
		xmlString = "<request version='1.0' id='"+client2.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='2' height='10'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = aec.process(client1, request);
		System.out.println(response.toString());
		
		dle.setBehavior(Behavior.ASYNCHRONOUS);
		// a sample failure XML message since invalid height
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='2' height='2'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = aec.process(client1, request);
		System.out.println(response.toString());
		
		// a sample failure XML message since invalid edge
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='3' height='20'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = aec.process(client1, request);
		System.out.println(response.toString());
		
		dle.setType(EventType.FINISHED);
		// a sample failure XML message since DLE is finished
		xmlString = "<request version='1.0' id='"+client2.id().toString()+"'>"+
				  "<addEdgeRequest id='"+dle.getUniqueId()+"' left='1' right='2' height='20'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = aec.process(client1, request);
		System.out.println(response.toString());
		assertTrue(!(response.toString()==null));
	}

}
