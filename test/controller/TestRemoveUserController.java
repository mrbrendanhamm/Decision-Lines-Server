package controller;

import java.util.ArrayList;

import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import server.MockClient;
import server.Server;
import xml.Message;
import junit.framework.TestCase;

//This test case will have to wait is there is no removeUser request/response in xsd

public class TestRemoveUserController extends TestCase {
	MockClient client1, client2, client3;
	Model myModel;
	User user1, user2, user3;
	ArrayList<User> userList = new ArrayList<User>();
	DecisionLineEvent dle;
	
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
	
	public void testRemoveUserRR(){
		
		myModel = Model.getInstance();
		RemoveUserController myController = new RemoveUserController();
		
		//make new open DLE and add to model
		dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		myModel.getDecisionLineEvents().add(dle);
		dle.setDate(new java.util.Date());

		//create users
		user1= new User("c1","pass1",0,3);
		user2= new User("c2","pass1",1,3);
		user3= new User("c3","pass1",2,3);
				
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
		
		
		
		String removeUser2 = "<request version='1.0' id='"+client1.id()+"'>"+
				  "<kickRequest id='"+dle.getUniqueId()+"' user='"+user2.getUser()+"'/>"+
				"</request>";
		Message request = new Message(removeUser2);
		
		myController.process(client1, request);
		
		userList=dle.getUsers();
		
		for (User user:userList){
		assertTrue( !(user.getUser().equals(client2.id()) ) );
		}
		myModel.removeDecisionLineEvent(dle);		
		
	}
	
	public void testRemoveUserAsynchronous(){
		myModel = Model.getInstance();
		RemoveUserController myController = new RemoveUserController();
		Boolean inList=false;
		
		//make new open DLE and add to model
		DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ASYNCHRONOUS);
		myModel.getDecisionLineEvents().add(dle);
		dle.setDate(new java.util.Date());

		//create users
		user1= new User("c1","pass1",0,3);
		user2= new User("c2","pass1",1,3);
		user3= new User("c3","pass1",2,3);
				
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
		
		
		
		String removeUser2 = "<request version='1.0' id='"+client1.id()+"'>"+
				  "<kickRequest id='"+dle.getUniqueId()+"' user='"+user2.getUser()+"'/>"+
				"</request>";
		Message request = new Message(removeUser2);
		
		myController.process(client1, request);
		
		userList=dle.getUsers();
		
		for (User user:userList){
			if (user.getUser().equals(client2.id()) )
				inList=true;
		}
		
		assertTrue(inList);
		myModel.removeDecisionLineEvent(dle);
		
	}
	
	public void testRemoveUserNotInDLE(){
		myModel = Model.getInstance();
		RemoveUserController myController = new RemoveUserController();
		Boolean inList=false;
		
		//make new open DLE and add to model
		DecisionLineEvent dle= new DecisionLineEvent("testID","testQuestion",3,3, EventType.OPEN, Behavior.ROUNDROBIN);
		myModel.getDecisionLineEvents().add(dle);
		dle.setDate(new java.util.Date());

		//create users
		user1= new User("c1","pass1",0,3);
		user2= new User("c2","pass1",1,3);
		user3= new User("c3","pass1",2,3);
				
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
		
		
		
		String removeUser2 = "<request version='1.0' id='"+client1.id()+"'>"+
				  "<kickRequest id='"+dle.getUniqueId()+"' user='user0'/>"+
				"</request>";
		Message request = new Message(removeUser2);
		
		Message response = myController.process(client1, request);
		
		String isSuccess = response.contents.getAttributes().getNamedItem("success").getNodeValue();
		
		assertTrue(isSuccess.equals("false"));
		myModel.removeDecisionLineEvent(dle);
		
	}

}

