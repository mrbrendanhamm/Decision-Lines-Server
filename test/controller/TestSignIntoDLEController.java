package controller;

/**
 * Many aspects of this adapted from Professor Heineman's TestCase examples discussed during class
 */
import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import entity.*;

import xml.Message;
import junit.framework.TestCase;

public class TestSignIntoDLEController extends TestCase {
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
		//a sample, fully formed SignInRequest message XML string
		Model myModel = Model.getInstance();
		DecisionLineEvent loadedEvent;
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();
		
		String testMessageFailure = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='azafty' password='bad_password' />" +
				"  </signInRequest>" +
				"</request>";
		Message msg = new Message(testMessageFailure);
		Message retVal = myHandler.process(client1, msg);
		assertTrue(retVal != null);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("false"));
		
		
		String testMessageSuccess = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='azafty' password='' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		retVal = myHandler.process(client1, msg);
		
		testMessageSuccess = "<request version='1.0' id='" + client2.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='abra' password='andrew' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, msg);
		assert(retVal != null);
		loadedEvent = myModel.getDecisionLineEvents().get(0);
		assertTrue(loadedEvent.getUsers().contains(new User("abra", "", -1)));
				
		testMessageSuccess = "<request version='1.0' id='" + client3.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='supra' password='' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, msg);
		assert(retVal != null);
		loadedEvent = myModel.getDecisionLineEvents().get(0);
		assertTrue(loadedEvent.getUsers().contains(new User("supra", "", -1)));
	}
}
