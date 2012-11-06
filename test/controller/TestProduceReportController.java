package controller;

import boundary.DatabaseSubsystem;
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

public class TestProduceReportController extends TestCase {
	MockClient client1;
	Model myModel;
	String xmlString;
	Message request;
	ProduceReportController myController;

	protected void setUp () {
		if (!Message.configure(ApplicationMain.getMessageXSD())) { 
			fail ("unable to configure protocol");
		}
		
		// make server think there are two connected clients...
		client1 = new MockClient("c1");

		Server.register("c1", client1);

		ClearModelInstance.clearInstance();
	}

	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
	}
	/**This test case will pass the valid admin credentials and receive
	 * a report.
	 * 
	 */
	public void testProduceReportControllValidCred(){
		//get singleton
		myModel=Model.getInstance(); 
		
		//generate several dles and send them to the database
		
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
		java.util.Date oldDate = new java.util.Date(currentDate.getTime() - 24*3600*1000*365);
		dleOpen1.setDate(oldDate);
		dleOpen2.setDate(oldDate);
		dleClosed1.setDate(oldDate);
		dleClosed2.setDate(oldDate);
		dleFinish1.setDate(oldDate);
		dleFinish2.setDate(oldDate);
		
		//set moderators
		dleOpen1.setModerator("c1");
		dleOpen2.setModerator("c1");
		dleClosed1.setModerator("c1");
		dleClosed1.setModerator("c1");
		dleFinish1.setModerator("c1");
		dleFinish1.setModerator("c1");
		
		//add to system
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen1);
		DatabaseSubsystem.writeDecisionLineEvent(dleOpen2);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed1);
		DatabaseSubsystem.writeDecisionLineEvent(dleClosed2);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish1);
		DatabaseSubsystem.writeDecisionLineEvent(dleFinish2);		
		
		//create string for message
		xmlString = "<request version='1.0' id='"+client1.id()+"'>"+
						"<reportRequest key='"+myModel.getKey()+"' type='open'/>" +
					"</request>";

		//generate message from string
		request= new Message(xmlString);
		
		//Instantiate controller and send message
		myController = new ProduceReportController();
		Message response = myController.process(client1, request);
		

		
		assert(response!=null);
		
		
		//cleanup
		DatabaseSubsystem.deleteEventById(dleOpen1.getUniqueId());
		DatabaseSubsystem.deleteEventById(dleOpen2.getUniqueId());
		DatabaseSubsystem.deleteEventById(dleClosed1.getUniqueId());
		DatabaseSubsystem.deleteEventById(dleClosed2.getUniqueId());
		DatabaseSubsystem.deleteEventById(dleFinish1.getUniqueId());
		DatabaseSubsystem.deleteEventById(dleFinish2.getUniqueId());
	}
	
	/** This test case will pass invalid admin credentials and should not 
	 * receive a report
	 */
	//public void testProduceReportControllInvalidCred(){
		
	//}

	//TODO : Expand these cases to test closed and finished events

}
