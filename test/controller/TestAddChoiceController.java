package controller;

import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import entity.User;
import server.MockClient;
import server.Server;
import xml.Message;
import junit.framework.TestCase;

public class TestAddChoiceController extends TestCase
{
	private AddChoiceController acc;
	private User user;
	private DecisionLineEvent dle;
	private Model model;
	private MockClient client1;
	protected void setUp() throws Exception
	{
		if (!Message.configure("draw2choose.xsd")) {
			fail ("unable to configure protocol");
		}
		client1 = new MockClient("c1");
		Server.register("c1", client1);
		ClearModelInstance.clearInstance();
		acc = new AddChoiceController();
		model = Model.getInstance();
		dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		user = new User("A","B",1,3);
		user.setClientStateId("c1");
		dle.addUser(user);
		model.getDecisionLineEvents().add(dle);
	}

	public void testProcess()
	{	
		// a sample success XML message
		String xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addChoiceRequest id='"+dle.getUniqueId()+"' number='1' choice='When to meet'/>"+
				"</request>";
		System.out.println(xmlString);
		Message request = new Message(xmlString);
		Message response = acc.process(client1, request);
		System.out.println(response.toString());
		
		// a sample failure XML message since wrong number
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addChoiceRequest id='"+dle.getUniqueId()+"' number='2' choice='When to meet'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = acc.process(client1, request);
		System.out.println(response.toString());
		
		// a sample failure XML message since choice with the same order exists 
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addChoiceRequest id='"+dle.getUniqueId()+"' number='1' choice='When to meet'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = acc.process(client1, request);
		System.out.println(response.toString());
		
		// a sample failure XML message since DLE is closed
		dle.setType(EventType.CLOSED);
		xmlString = "<request version='1.0' id='"+client1.id().toString()+"'>"+
				  "<addChoiceRequest id='"+dle.getUniqueId()+"' number='1' choice='When to meet'/>"+
				"</request>";
		System.out.println(xmlString);
		request = new Message(xmlString);
		response = acc.process(client1, request);
		System.out.println(response.toString());
		assertTrue(!(response.toString()==null));
	}

}
