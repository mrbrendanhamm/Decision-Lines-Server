package controller;

import java.util.UUID;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;
import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.User;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import junit.framework.TestCase;

public class TestClientDisconnect extends TestCase {
	MockClient client1, client2, client3;
	String parentDLEId;

	
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
		
		parentDLEId = UUID.randomUUID().toString();
		int numOfChoices = 4;
		int numOfEdges = 3;
		
		DecisionLineEvent myEvent = new DecisionLineEvent(parentDLEId, "my test question", numOfChoices, numOfEdges, EventType.CLOSED, Behavior.ROUNDROBIN);
		myEvent.setDate(new java.util.Date());
		User newUser1 = new User("andrew1", "", 0, numOfEdges);
		User newUser2 = new User("andrew2", "", 1, numOfEdges);
		User newUser3 = new User("andrew3", "andrew", 2, numOfEdges);
		myEvent.getUsers().add(newUser1);
		myEvent.getUsers().add(newUser2);
		myEvent.getUsers().add(newUser3);
		myEvent.setModerator(newUser1.getUser());

		DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		ClearModelInstance.clearInstance();
	}

	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
		DatabaseSubsystem.deleteEventById(parentDLEId);
	}
	
	public void testDisconnectClient() {
		//a sample, fully formed SignInRequest message XML string
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();
		SignIntoDLEController tmpCont = new SignIntoDLEController();
		
		//use DLEId created above
		
		String testMessageSuccess = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <signInRequest id='" + parentDLEId + "'>" +
				"    <user name='andrew1' password='' />" +
				"  </signInRequest>" +
				"</request>";
		Message msg = new Message(testMessageSuccess);
		tmpCont.process(client1,  msg);
		
		testMessageSuccess = "<request version='1.0' id='" + client2.id().toString() + "'>" +
				"  <signInRequest id='" + parentDLEId + "'>" +
				"    <user name='andrew2' password='' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		myHandler.process(client1, msg);
				
		testMessageSuccess = "<request version='1.0' id='" + client3.id().toString() + "'>" +
				"  <signInRequest id='" + parentDLEId + "'>" +
				"    <user name='andrew3' password='andrew' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		myHandler.process(client1, msg);
		
		myHandler.logout(client1);
		myHandler.logout(client2);
		myHandler.logout(client3);
		
	}
}
