package controller;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;
import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;
import junit.framework.TestCase;

public class TestCompleteGame extends TestCase {
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
	
	public void testFullRoundRobin() {
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();

		// Create open DLE
		String testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>" +
				"  <createRequest type='open' question='Test Question' numChoices='3' numRounds='2' behavior='roundRobin'>" +
				"    <choice value='Choice1' index='0'/>" +
				"    <user name='User1' />" +
				"  </createRequest>" +
				"</request>";
		Message myMessage = new Message(testMessageSuccess);
		Message retVal = myHandler.process(client1,  myMessage);
		String dleId = retVal.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
		
		// Log in client 2
		// Log in client 3
		// Create Choice 2
		// Create Choice 3
		
		// Begin Game
		// Check for proper turnResponse
		// Client 1 Play Edge 1
		// verify edge received by all players
		// verify proper turnReponse
		
		// Client 2 Play Edge 1
		// Client 3 Play Edge 1
		// Client 1 Play Edge 2
		// Client 2 Play Edge 2
		// Client 3 Play Edge 2
		
	}

}
