package controller;

import server.MockClient;
import server.Server;
import xml.Message;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import junit.framework.TestCase;

public class TestCloseOpenDLEController extends TestCase {

	MockClient client1, client2;
	Model model;
	
	protected void setUp() {
		// FIRST thing to do is register the protocol being used.
		if (!Message.configure("distributedEBC.xsd")) {
			fail ("unable to configure protocol");
		}
				
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		

		Server.register("c1", client1);
		Server.register("c2", client2);
		
		// clear the singleton
		ClearModelInstance.clearInstance();
	}
	
	protected void tearDown() {
		Server.unregister("c1");
	}
		
	//This will test whether we get a success closing an open DLE
	public void testProcess(){
		model = Model.getInstance();
		DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		dle.setModerator(client1.id());
		//a sample, fully formed create message XML string
		String xmlString = "<request version='1.0' id='" + dle.getUniqueId() + "'>" +
				"  <closeRequest>" +
				"    <name=" + client1.id() + "/>" +
				"  </closeRequest>" +
				"</request>";
		

		Message request = new Message(xmlString);
		
		Message response = new CloseOpenDLEController().process(client1, request);
		
		assertTrue(response.success());
	}
	
}
