package controller;

/**
 * Many aspects of this adapted from Professor Heineman's TestCase examples discussed during class
 */

import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;

import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;

import server.ApplicationMain;
import server.MockClient;
import server.Server;

import xml.Message;
import junit.framework.TestCase;

public class TestCreateDLEController extends TestCase {
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
		//a sample, fully formed create message XML string
		String testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>" +
				"  <createRequest type='closed' question='Test Question' numChoices='3' numRounds='3' behavior='roundRobin'>" +
				"    <choice value='Choice1' index='0'/>" +
				"    <choice value='Choice2' index='1'/>" +
				"    <choice value='Choice3' index='2'/>" +
				"    <user name='User1' />" +
				"  </createRequest>" +
				"</request>";
		
		Message msg = new Message(testMessageSuccess);
		Message retVal = new DefaultProtocolHandler().process(client1, msg);
		assert(retVal != null);
		String dleId = retVal.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();

		DecisionLineEvent loadedEvent = Model.getInstance().getDecisionLineEvent(dleId);
		assert(loadedEvent != null);
		assertTrue(loadedEvent.getUniqueId().equals(dleId));

	}
}
