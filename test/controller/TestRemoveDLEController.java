package controller;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import junit.framework.TestCase;

public class TestRemoveDLEController extends TestCase {
	MockClient client1, client2, client3;

	
	protected void setUp () {
		if (!Message.configure(ApplicationMain.getMessageXSD())) { 
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
	/** This test will hand the RemoveDLEController a single dleID to be deleted
	 * 
	 */
 public void testProcessOneDLE(){
		Model myModel = Model.getInstance();
		//need to have client1 sign in as admin.  So we send request and get the key
		String testAdmin = 	"<request version='1.0' id='c1'>" +
				"<adminRequest>" +
			  		"<user name='andrew' password='andrew'/>" +
			  	"</adminRequest>" +
			"</request>";
		Message adminLogMessage = new Message(testAdmin);
		AdminLogInController myAdminLogIn = new AdminLogInController();
		Message adminLoginResponse = myAdminLogIn.process(client1, adminLogMessage);
		Node adminResponseChild = adminLoginResponse.contents.getFirstChild();
		String myKey = adminResponseChild.getAttributes().getNamedItem("key").getNodeValue();
		
		//Create a new dle
		DecisionLineEvent dle = new DecisionLineEvent("dleID","question",3, 3, EventType.OPEN, Behavior.ROUNDROBIN);
		myModel.getDecisionLineEvents().add(dle);
		java.util.Date currentDate = new java.util.Date();
		long daysOld = 250;
		java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*daysOld);
		dle.setDate(oldDate);

		DatabaseSubsystem.writeDecisionLineEvent(dle);
		
		//construct the message
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
					"<removeRequest key='"+myKey+"' id='dleID'>" +
					"</removeRequest>" +
				"</request>";
		Message request = new Message(testMessage);
		//RemoveDLEController removeController = new RemoveDLEController();
		DefaultProtocolHandler removeController = new DefaultProtocolHandler();
		
		//send the message
		removeController.process(client1, request);
		
		//assert that the dle has been removed.
		assertTrue(myModel.getDecisionLineEvent("dleID")==null);
		assertTrue(DatabaseSubsystem.readDecisionLineEvent("dleID")==null);
	}
	
	/** This will test the deletion of all completed events 200 days old
	 * 
	 */
public void testProcessByCompleted(){
		Model myModel = Model.getInstance();
		//need to have client1 sign in as admin.  So we send request and get the key
		String testAdmin = 	"<request version='1.0' id='c1'>" +
				"<adminRequest>" +
			  		"<user name='andrew' password='andrew'/>" +
			  	"</adminRequest>" +
			"</request>";
		Message adminLogMessage = new Message(testAdmin);
		AdminLogInController myAdminLogIn = new AdminLogInController();
		Message adminLoginResponse = myAdminLogIn.process(client1, adminLogMessage);
		Node adminResponseChild = adminLoginResponse.contents.getFirstChild();
		String myKey = adminResponseChild.getAttributes().getNamedItem("key").getNodeValue();
		
		// Need to create multiple dles
		DecisionLineEvent dleOpen1 = new DecisionLineEvent("dleOpen1","question1",3, 3, EventType.OPEN, Behavior.ROUNDROBIN);
		DecisionLineEvent dleOpen2 = new DecisionLineEvent("dleOpen2","question2",3, 3, EventType.OPEN, Behavior.ASYNCHRONOUS);
		DecisionLineEvent dleClosed1 = new DecisionLineEvent("dleClosed1","question3",3, 3, EventType.CLOSED, Behavior.ROUNDROBIN);
		DecisionLineEvent dleClosed2 = new DecisionLineEvent("dleClosed2","question4",3, 3, EventType.CLOSED, Behavior.ASYNCHRONOUS);
		DecisionLineEvent dleFinish1 = new DecisionLineEvent("dleFinish1","question5",3, 3, EventType.FINISHED, Behavior.ROUNDROBIN);
		DecisionLineEvent dleFinish2 = new DecisionLineEvent("dleFinish2","question6",3, 3, EventType.FINISHED, Behavior.ASYNCHRONOUS);
		
		//and add them
		myModel.getDecisionLineEvents().add(dleOpen1);
		myModel.getDecisionLineEvents().add(dleOpen2);
		myModel.getDecisionLineEvents().add(dleClosed1);
		myModel.getDecisionLineEvents().add(dleClosed2);
		myModel.getDecisionLineEvents().add(dleFinish1);
		myModel.getDecisionLineEvents().add(dleFinish2);
		
		//set dates to be older than 0 days
		java.util.Date currentDate = new java.util.Date();
		long daysOld = 250;
		java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*daysOld);
		dleOpen1.setDate(oldDate);
		dleOpen2.setDate(oldDate);
		dleClosed1.setDate(oldDate);
		dleClosed2.setDate(oldDate);
		dleFinish1.setDate(oldDate);
		dleFinish2.setDate(oldDate);
		
		//add to database
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen1);
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen2);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed1);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed2);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish1);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish2);
		
		
		
		//message to close finished dles 0 days old
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
				"<removeRequest key='"+myKey+"' completed='true' daysOld='200'>" +
				"</removeRequest>" +
			"</request>";
		Message request = new Message(testMessage);
		RemoveDLEController removeController = new RemoveDLEController();
		removeController.process(client1, request);
		assertTrue(myModel.getDecisionLineEvent("dleOpen1")!=null);
		assertTrue(myModel.getDecisionLineEvent("dleOpen2")!=null);
		assertTrue(myModel.getDecisionLineEvent("dleClosed1")!=null);
		assertTrue(myModel.getDecisionLineEvent("dleClosed2")!=null);
		assertTrue(myModel.getDecisionLineEvent("dleFinish1")==null);
		assertTrue(myModel.getDecisionLineEvent("dleFinish2")==null);
		
		//clean up
		myModel.removeDecisionLineEvent(dleOpen1);
		myModel.removeDecisionLineEvent(dleOpen2);
		myModel.removeDecisionLineEvent(dleClosed1);
		myModel.removeDecisionLineEvent(dleClosed2);
		myModel.removeDecisionLineEvent(dleFinish1);
		myModel.removeDecisionLineEvent(dleFinish2);
		
	}
	
	/** This will test that only unFinished events 0 days old are deleted
	 * 
	 */
public void testProcessByNotCompleted(){
		Model myModel = Model.getInstance();
		//need to have client1 sign in as admin.  So we send request and get the key
		String testAdmin = "<request version='1.0' id='c1'>" +
				"<adminRequest>" +
			  		"<user name='andrew' password='andrew'/>" +
			  	"</adminRequest>" +
			"</request>";
		Message adminLogMessage = new Message(testAdmin);
		AdminLogInController myAdminLogIn = new AdminLogInController();
		Message adminLoginResponse = myAdminLogIn.process(client1, adminLogMessage);
		Node adminResponseChild = adminLoginResponse.contents.getFirstChild();
		String myKey = adminResponseChild.getAttributes().getNamedItem("key").getNodeValue();
		
		// Need to create multiple dles
		DecisionLineEvent dleOpen1 = new DecisionLineEvent("dleOpen1","question1",3, 3, EventType.OPEN, Behavior.ROUNDROBIN);
		DecisionLineEvent dleOpen2 = new DecisionLineEvent("dleOpen2","question2",3, 3, EventType.OPEN, Behavior.ASYNCHRONOUS);
		DecisionLineEvent dleClosed1 = new DecisionLineEvent("dleClosed1","question3",3, 3, EventType.CLOSED, Behavior.ROUNDROBIN);
		DecisionLineEvent dleClosed2 = new DecisionLineEvent("dleClosed2","question4",3, 3, EventType.CLOSED, Behavior.ASYNCHRONOUS);
		DecisionLineEvent dleFinish1 = new DecisionLineEvent("dleFinish1","question5",3, 3, EventType.FINISHED, Behavior.ROUNDROBIN);
		DecisionLineEvent dleFinish2 = new DecisionLineEvent("dleFinish2","question6",3, 3, EventType.FINISHED, Behavior.ASYNCHRONOUS);
				
		//and add them
		myModel.getDecisionLineEvents().add(dleOpen1);
		myModel.getDecisionLineEvents().add(dleOpen2);
		myModel.getDecisionLineEvents().add(dleClosed1);
		myModel.getDecisionLineEvents().add(dleClosed2);
		myModel.getDecisionLineEvents().add(dleFinish1);
		myModel.getDecisionLineEvents().add(dleFinish2);
		
		//set dates to be older than 0 days
		java.util.Date currentDate = new java.util.Date();
		long daysOld = 250;
		java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*daysOld);
		dleOpen1.setDate(oldDate);
		dleOpen2.setDate(oldDate);
		dleClosed1.setDate(oldDate);
		dleClosed2.setDate(oldDate);
		dleFinish1.setDate(oldDate);
		dleFinish2.setDate(oldDate);
		
		//add to system
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen1);
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen2);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed1);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed2);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish1);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish2);
		
		
		//message to close finished dles 0 days old
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
				"<removeRequest key='"+myKey+"' completed='false' daysOld='200'>" +
				"</removeRequest>" +
			"</request>";
		Message request = new Message(testMessage);
		RemoveDLEController removeController = new RemoveDLEController();
		removeController.process(client1, request);
		assertTrue(myModel.getDecisionLineEvent("dleOpen1")==null);
		assertTrue(myModel.getDecisionLineEvent("dleOpen2")==null);
		assertTrue(myModel.getDecisionLineEvent("dleClosed1")==null);
		assertTrue(myModel.getDecisionLineEvent("dleClosed2")==null);
		assertTrue(myModel.getDecisionLineEvent("dleFinish1")!=null);
		assertTrue(myModel.getDecisionLineEvent("dleFinish2")!=null);
		
		//clean up
		myModel.removeDecisionLineEvent(dleOpen1);
		myModel.removeDecisionLineEvent(dleOpen2);
		myModel.removeDecisionLineEvent(dleClosed1);
		myModel.removeDecisionLineEvent(dleClosed2);
		myModel.removeDecisionLineEvent(dleFinish1);
		myModel.removeDecisionLineEvent(dleFinish2);
		
	}

public void testProcessOneDLEInvalidKey(){
	Model myModel = Model.getInstance();
	//need to have client1 sign in as admin.  So we send request and get the key
	String testAdmin = 	"<request version='1.0' id='c1'>" +
			"<adminRequest>" +
		  		"<user name='andrew' password='andrew'/>" +
		  	"</adminRequest>" +
		"</request>";
	Message adminLogMessage = new Message(testAdmin);
	AdminLogInController myAdminLogIn = new AdminLogInController();
	myAdminLogIn.process(client1, adminLogMessage);

	//Create a new dle
	DecisionLineEvent dle = new DecisionLineEvent("dleID","question",3, 3, EventType.OPEN, Behavior.ROUNDROBIN);
	myModel.getDecisionLineEvents().add(dle);
	java.util.Date currentDate = new java.util.Date();
	long daysOld = 250;
	java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*daysOld);
	dle.setDate(oldDate);

	DatabaseSubsystem.writeDecisionLineEvent(dle);
	
	//construct the message
	String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
				"<removeRequest key='falseKey' id='dleID'>" +
				"</removeRequest>" +
			"</request>";
	Message request = new Message(testMessage);
	RemoveDLEController removeController = new RemoveDLEController();
	
	//send the message
	removeController.process(client1, request);
	
	//assert that the dle has not been removed.
	assertTrue(myModel.getDecisionLineEvent("dleID")!=null);
	//clean up
	myModel.removeDecisionLineEvent(dle);

	
}
	
}
