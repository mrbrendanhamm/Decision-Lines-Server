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

public class TestClientDisconnect extends TestCase {
	MockClient client1, client2, client3;
	//String dleId;

	
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
		
		//set DLE Id through creation of new DLE
		
		ClearModelInstance.clearInstance();
	}

	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
	}
	
	public void testDisconnectClient() {
		//a sample, fully formed SignInRequest message XML string
		Model myModel = Model.getInstance();
		DecisionLineEvent loadedEvent;
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();
		SignIntoDLEController tmpCont = new SignIntoDLEController();
		
		//use DLEId created above
		
		String testMessageSuccess = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='azafty' password='' />" +
				"  </signInRequest>" +
				"</request>";
		Message msg = new Message(testMessageSuccess);
		//myHandler.process(client1, msg);
		tmpCont.process(client1,  msg);
		
		testMessageSuccess = "<request version='1.0' id='" + client2.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='abra' password='andrew' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		myHandler.process(client1, msg);
				
		testMessageSuccess = "<request version='1.0' id='" + client3.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='supra' password='' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		myHandler.process(client1, msg);
		
		loadedEvent = myModel.getDecisionLineEvent("12345");
		assertTrue(loadedEvent != null);
		//TODO verify that the users are connected with the expected client states
		
		//delete DLEId
		myHandler.logout(client1);
		myHandler.logout(client2);
		myHandler.logout(client3);
		
	}
}
