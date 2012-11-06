package controller;

import java.util.UUID;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;
import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;
import entity.Choice;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Edge;
import entity.Model;
import entity.User;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import junit.framework.TestCase;

public class TestForceFinishController extends TestCase {
	MockClient client1, client2, client3;
	
	protected void setUp () {
		if (!Message.configure(ApplicationMain.getMessageXSD())) { 
			fail ("unable to configure protocol");
		}
		
		if (!DatabaseSubsystem.connect()) {
			System.out.println("Error, cannot connect to the database");
			System.exit(0);
		}
		
		// make server think there are two connected clients...
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		client3 = new MockClient("c3");
		
		Server.register("c1", client1);
		Server.register("c2", client2);
		Server.register("c3", client3);
		
		ClearModelInstance.clearInstance();
	}

	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
	}
	
	

	public void testProcess() {
		// Initialize testing environment
		Model myModel = Model.getInstance();
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();

		String uniqueId = UUID.randomUUID().toString();
		int numOfChoices = 1;
		int numOfEdges = 1;
		DecisionLineEvent loadedEvent = new DecisionLineEvent(uniqueId, "my test question", numOfChoices, numOfEdges, EventType.CLOSED, Behavior.ROUNDROBIN);
		loadedEvent.setDate(new java.util.Date());
		User newUser1 = new User("andrew1", "", 0);
		loadedEvent.getUsers().add(newUser1);
		loadedEvent.setModerator(newUser1.getUser());
		Choice newChoice1 = new Choice("Choice 1", 1, -1);
		loadedEvent.getChoices().add(newChoice1);
		myModel.getDecisionLineEvents().add(loadedEvent);
		DatabaseSubsystem.writeDecisionLineEvent(loadedEvent);
		
		//Build the request string
		String testMessageSuccess = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <forceRequest key='" + myModel.getKey() + "' id='" + loadedEvent.getUniqueId() + "'/>" +
				"</request>";
		Message msg = new Message(testMessageSuccess);
		Message retVal = myHandler.process(client1, msg);
		assertTrue(retVal != null);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		assertTrue(loadedEvent.getEventType() == EventType.FINISHED);

	}

}
