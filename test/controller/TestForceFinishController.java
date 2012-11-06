package controller;

import server.MockClient;
import server.Server;
import xml.Message;
import entity.Choice;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Edge;
import entity.Model;
import entity.User;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import junit.framework.TestCase;

public class TestForceFinishController extends TestCase
{
	private ForceFinishController ffc;
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
	private Edge edge1;
	private Edge edge2;
	private Edge edge3;
	protected void setUp() throws Exception
	{
		if (!Message.configure("draw2choose.xsd")) {
			fail ("unable to configure protocol");
		}
		java.util.Date currentDate = new java.util.Date();
		java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*4);
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		client3 = new MockClient("c3");
		Server.register("c1", client1);
		Server.register("c2", client2);
		Server.register("c3", client3);
		ClearModelInstance.clearInstance();
		ffc = new ForceFinishController();
		model = Model.getInstance();
		dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ASYNCHRONOUS);
		user1 = new User("A","B",1);
		user1.setClientStateId("c1");
		user2 = new User("C","D",2);
		user2.setClientStateId("c2");
		user3 = new User("E","F",3);
		user3.setClientStateId("c3");
		dle.addUser(user1);
		dle.addUser(user2);
		dle.addUser(user3);
		choice1 = new Choice("When to meet",1);
		choice2 = new Choice("When to eat",2);
		choice3 = new Choice("When to play",3);
		edge1 = new Edge(choice1,choice2,1);
		edge2 = new Edge(choice2,choice3,10);
		edge3 = new Edge(choice1,choice2,20);
		dle.addChoice(choice1);
		dle.addChoice(choice2);
		dle.addChoice(choice3);
		dle.setCurrentTurn(user1);
		dle.addEdge(edge1);
		dle.addEdge(edge2);
		dle.addEdge(edge3);
		dle.setDate(oldDate);
		model.getDecisionLineEvents().add(dle);
	}

	public void testProcess()
	{
		// a sample failure XML message since wrong key
		String xmlString = "<request version='1.0' id='"
				+ client2.id().toString() + "'>" + "<forceRequest key='0' id='testID'/>"
				+ "</request>";
		System.out.println(xmlString);
		Message request = new Message(xmlString);
		Message response = ffc.process(client1, request);
		System.out.println(response.toString());
		
		// a sample failure XML message since DLE doesn't exist
		xmlString = "<request version='1.0' id='"
				+ client2.id().toString() + "'>" + "<forceRequest key='"
				+ model.getKey() + "' id='test'/>"
				+ "</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = ffc.process(client1, request);
		System.out.println(response.toString());
		
		// a sample success XML message
		xmlString = "<request version='1.0' id='"
				+ client2.id().toString() + "'>" + "<forceRequest key='"
				+ model.getKey() + "' id='testID'/>"
				+ "</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = ffc.process(client1, request);
		System.out.println(response.toString());
		
		model.getDecisionLineEvents().add(dle);
		// a sample success XML message
		xmlString = "<request version='1.0' id='"
				+ client2.id().toString() + "'>" + "<forceRequest key='"
				+ model.getKey() + "' daysOld='10'/>"
				+ "</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = ffc.process(client1, request);
		System.out.println(response.toString());
	}

}
