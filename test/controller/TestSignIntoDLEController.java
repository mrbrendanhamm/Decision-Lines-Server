package controller;

import boundary.DefaultProtocolHandler;

import server.MockClient;
import server.Server;
import entity.*;

import xml.Message;
import junit.framework.TestCase;

public class TestSignIntoDLEController extends TestCase {
	MockClient client1, client2, client3;
	
	protected void setUp () {
		if (!Message.configure("draw2choose.xsd")) { 
			fail ("unable to configure protocol");
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
		
		String testMessageSuccess = "<request version='1.0' id='" + client1.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='azafty' password='' />" +
				"  </signInRequest>" +
				"</request>";
		
		Message msg = new Message(testMessageSuccess);
		
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();
		Message retVal = myHandler.process(client1, msg);
		
		/*
		assert(retVal != null);

		loadedEvent = myModel.getDecisionLineEvents().get(0);
		assert(loadedEvent != null);
		
		assertTrue(loadedEvent.getUsersAndEdges().containsKey(new User("azafty", "andrew", -1)));		
		*/
		
		testMessageSuccess = "<request version='1.0' id='" + client2.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='abra' password='andrew' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, msg);
		assert(retVal != null);
		loadedEvent = myModel.getDecisionLineEvents().get(0);
		assertTrue(loadedEvent.getUsersAndEdges().containsKey(new User("abra", "", -1)));
		
		testMessageSuccess = "<request version='1.0' id='" + client3.id().toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='supra' password='' />" +
				"  </signInRequest>" +
				"</request>";
		msg = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, msg);
		assert(retVal != null);
		loadedEvent = myModel.getDecisionLineEvents().get(0);
		assertTrue(loadedEvent.getUsersAndEdges().containsKey(new User("supra", "", -1)));
	}
}
