package controller;

import org.w3c.dom.Node;

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
		//construct the message
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
					"<removeRequest key='"+myKey+"' id='dleID'>" +
					"</removeRequest>" +
				"</request>";
		Message request = new Message(testMessage);
		RemoveDLEController removeController = new RemoveDLEController();
		
		removeController.process(client1, request);
		
	}
	
	/** This will test the deletion of all completed events 0 days old
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
		
		//message to close finished dles 0 days old
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
				"<removeRequest key='"+myKey+"' completed='true' daysOld='0'>" +
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
		
		//message to close finished dles 0 days old
		String testMessage = "<request version='1.0' id='"+ client1.id() +"'>" +
				"<removeRequest key='"+myKey+"' completed='false' daysOld='0'>" +
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
	}
	
}
