package controller;

import server.MockClient;
import server.Server;
import xml.Message;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import entity.User;
import junit.framework.TestCase;

public class TestCloseOpenDLEController extends TestCase {

	MockClient client1, client2, client3;
	Model myModel;
	User user1, user2, user3;
	
	protected void setUp() {
		// FIRST thing to do is register the protocol being used.
		if (!Message.configure("draw2choose.xsd")) {
			fail ("unable to configure protocol");
		}
				
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		client3 = new MockClient("c3");
		

		Server.register("c1", client1);
		Server.register("c2", client2);
		Server.register("c3", client3);
		
		// clear the singleton
		ClearModelInstance.clearInstance();
	}
	
	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
		
		
	}
		
	//This will test whether we get a success closing an open DLE
	public void testProcessOpen(){
		//create controller
		CloseOpenDLEController myController= new CloseOpenDLEController();
		
		// get the singleton
		myModel = Model.getInstance();
		
		//make new open DLE and add to model
		DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		myModel.getDecisionLineEvents().add(dle);
		dle.setDate(new java.util.Date());
		
		//create users
		user1= new User("c1","pass1",0);
		user2= new User("c2","pass1",1);
		user3= new User("c3","pass1",2);
		
		//set user client state ids
		user1.setClientStateId("c1");
		user2.setClientStateId("c2");
		user3.setClientStateId("c3");
		
		//add users to dle
		dle.addUser(user1);
		dle.addUser(user2);
		dle.addUser(user3);
		
		//set moderator of dle
		dle.setModerator(client1.id());
		
		
		//a sample, fully formed create message XML string
		String xmlString = "<request version='1.0' id='"+client1.id()+"'>"+
				  "<closeRequest id='"+dle.getUniqueId()+"'/>"+
				"</request>";
		Message request = new Message(xmlString);
		
		Message response = myController.process(client1, request);
		assertTrue(!(response.toString()==null));
	}
	
	//This will test whether we get a success closing a closed DLE
		public void testProcessClosed(){
			//create controller
			CloseOpenDLEController myController= new CloseOpenDLEController();
			
			// get the singleton
			myModel = Model.getInstance();
			
			//make new open DLE and add to model
			DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.CLOSED, Behavior.ROUNDROBIN);
			myModel.getDecisionLineEvents().add(dle);
			dle.setDate(new java.util.Date());
			
			//create users
			user1= new User("c1","pass1",0);
			user2= new User("c2","pass1",1);
			user3= new User("c3","pass1",2);
			
			//set user client state ids
			user1.setClientStateId("c1");
			user2.setClientStateId("c2");
			user3.setClientStateId("c3");
			
			//add users to dle
			dle.addUser(user1);
			dle.addUser(user2);
			dle.addUser(user3);
			
			//set moderator of dle
			dle.setModerator(client1.id());
			
			
			//a sample, fully formed create message XML string
			String xmlString = "<request version='1.0' id='"+client1.id()+"'>"+
					  "<closeRequest id='"+dle.getUniqueId()+"'/>"+
					"</request>";
			Message request = new Message(xmlString);
			
			Message response = myController.process(client1, request);
			assertTrue(!(response.toString()==null));
		}
	
		//This will test whether we get a success closing an open DLE
		public void testProcessFinished(){
			//create controller
			CloseOpenDLEController myController= new CloseOpenDLEController();
			
			// get the singleton
			myModel = Model.getInstance();
			
			//make new open DLE and add to model
			DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.FINISHED, Behavior.ROUNDROBIN);
			myModel.getDecisionLineEvents().add(dle);
			dle.setDate(new java.util.Date());
			
			//create users
			user1= new User("c1","pass1",0);
			user2= new User("c2","pass1",1);
			user3= new User("c3","pass1",2);
			
			//set user client state ids
			user1.setClientStateId("c1");
			user2.setClientStateId("c2");
			user3.setClientStateId("c3");
			
			//add users to dle
			dle.addUser(user1);
			dle.addUser(user2);
			dle.addUser(user3);
			
			//set moderator of dle
			dle.setModerator(client1.id());
			
			
			//a sample, fully formed create message XML string
			String xmlString = "<request version='1.0' id='"+client1.id()+"'>"+
					  "<closeRequest id='"+dle.getUniqueId()+"'/>"+
					"</request>";
			Message request = new Message(xmlString);
			
			Message response = myController.process(client1, request);
			assertTrue(!(response.toString()==null));
		}
}
