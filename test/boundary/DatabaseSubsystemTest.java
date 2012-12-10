package boundary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;

import boundary.DatabaseSubsystem;

import junit.framework.TestCase;

import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

public class DatabaseSubsystemTest extends TestCase {
	static String parentDLEId;

	protected void setUp () {
		if (!DatabaseSubsystem.connect()) {
			System.out.println("Error, cannot connect to the database");
			System.exit(0);
		}
		
		if (parentDLEId == null) 
			parentDLEId = UUID.randomUUID().toString();
	}
	
	public void testConnection() {
		DatabaseSubsystem.disconnect();
		try {
			assert(DatabaseSubsystem.con.isClosed());
		} catch (Exception e) {
			fail("Error while disconnecting");
		}
		DatabaseSubsystem.connect();
		try {
			assert(DatabaseSubsystem.con.isClosed() == false);
		} catch (Exception e) {
			fail("Error while connecting");
		}
		java.sql.Connection con = DatabaseSubsystem.getConnection();
		assert(con != null);
		assert(DatabaseSubsystem.isConnected());
	}
	
	public void testWriteDecisionLineEvent() {
		System.out.println("Testing write decisionlineevent");
		int numOfChoices = 4;
		int numOfEdges = 3;
		
		DatabaseSubsystem.configurationProductionDBAccess();
		DatabaseSubsystem.configurationTestDBAccess();
		
		DecisionLineEvent myEvent = new DecisionLineEvent(parentDLEId, "my test question", numOfChoices, numOfEdges, EventType.CLOSED, Behavior.ROUNDROBIN);
		myEvent.setDate(new java.util.Date());
		User newUser1 = new User("andrew1", "", 0, numOfEdges);
		User newUser2 = new User("andrew2", "", 1, numOfEdges);
		myEvent.getUsers().add(newUser1);
		myEvent.getUsers().add(newUser2);
		myEvent.setModerator(newUser1.getUser());
		
		Choice newChoice1 = new Choice("Choice 1", 0, -1);
		Choice newChoice2 = new Choice("Choice 2", 1, -1);
		Choice newChoice3 = new Choice("Choice 3", 2, -1);
		Choice newChoice4 = new Choice("Choice 4", 3, -1);
		myEvent.getChoices().add(newChoice1);
		myEvent.getChoices().add(newChoice2);
		myEvent.getChoices().add(newChoice3);
		
		Edge newEdge1 = new Edge(newChoice1, newChoice2, 1);
		Edge newEdge2 = new Edge(newChoice2, newChoice3, 1);
		Edge newEdge3 = new Edge(newChoice3, newChoice4, 1);
		Edge newEdge4 = new Edge(newChoice1, newChoice2, 2);
		Edge newEdge5 = new Edge(newChoice2, newChoice3, 2);
		myEvent.getEdges().add(newEdge1);
		myEvent.getEdges().add(newEdge2);
		myEvent.getEdges().add(newEdge3);
		myEvent.getEdges().add(newEdge4);
		myEvent.getEdges().add(newEdge5);
		
		int retval = DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		assertTrue(retval > 0);
	}

	public void testWriteEdge() {
		System.out.println("Testing write edges");
		Choice myLeftChoice = new Choice("Choice 1", 0, -1);
		Choice myRightChoice = new Choice("Choice 2", 1, -1);
		int height = 16;
		Edge myEdge = new Edge(myLeftChoice, myRightChoice, height);
		
		int retval = DatabaseSubsystem.writeEdge(myEdge, parentDLEId);
		
		assertTrue(retval > 0);
	}
	
	public void testWriteChoice() {
		System.out.println("Testing write choices");
		Choice myChoice = new Choice("Choice 3", 3, -1);
		
		int retval = DatabaseSubsystem.writeChoice(myChoice, parentDLEId);
		
		assertTrue(retval > 0);
	}

	public void testWriteUser() {
		System.out.println("Testing write user");
		User myUser = new User("azafty2", "", 0, 1);
		int retval = DatabaseSubsystem.writeUser(myUser, parentDLEId);
		assertTrue(retval > 0);
	}
	
	public void testReadUsers() {
		//minor change
		System.out.println("Testing read users");
		DecisionLineEvent myDLE = new DecisionLineEvent(parentDLEId);
		
		boolean retval = DatabaseSubsystem.readUsers(myDLE, 1);
		
		assertTrue(retval);
	}
	
	public void testReadEdges() {
		System.out.println("Testing read edges");
		DecisionLineEvent myDLE = DatabaseSubsystem.readDecisionLineEvent(parentDLEId);

		boolean retval = DatabaseSubsystem.readEdges(myDLE);
		
		assertTrue(retval);
	}
	
	public void testReadChoices() {
		System.out.println("Testing read choices");
		DecisionLineEvent myDLE = new DecisionLineEvent(parentDLEId);
		
		boolean retval = DatabaseSubsystem.readChoices(myDLE);
		
		assertTrue(retval);
	}
	
	public void testReadDecisionLineEvent() {
		//TODO how about sending it a badly formed finish event that needs to have the final order set?
		//dont' forget to reset the unfinished status and unordered values when done
		System.out.println("Testing read decisionlineevent");
		DecisionLineEvent retval = DatabaseSubsystem.readDecisionLineEvent(parentDLEId);
		assertTrue(retval != null);
		
		retval = DatabaseSubsystem.readDecisionLineEvent(parentDLEId + "abc");
		assertTrue(retval == null);
		
		//and while I'm at it, test delete an event
		int deleteResult = DatabaseSubsystem.deleteEventById(parentDLEId);
		assertTrue(deleteResult == 1);
	}
	

	public void testDeleteEventByDate() {
		System.out.println("Testing the delete by date function");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		java.util.Date convertedDate =  new java.util.Date();
		try {
			convertedDate = dateFormat.parse("2012-10-03");
		} catch (Exception e) {
			fail("error while converting date");
		}
    
		int retval = DatabaseSubsystem.deleteEventsByAge(convertedDate, true);
		assert(retval != -1);
		
		retval = DatabaseSubsystem.deleteEventsByAge(convertedDate, false);
		assert(retval != -1);
	}
	
	public void testFinishEventByDate() {
		System.out.println("Testing the close by date function");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		java.util.Date convertedDate =  new java.util.Date();
		try {
			convertedDate = dateFormat.parse("2012-09-03");
		} catch (Exception e) {
			fail("error while converting date");
		}
    
		int retval = DatabaseSubsystem.finishDLEBasedOnDate(convertedDate);
		assert(retval != -1);
	}
	
	public void testProduceReport() {
		System.out.println("Testing produce report");
		String outString = "";
		ArrayList<String> myAL = DatabaseSubsystem.produceReport(EventType.CLOSED);
		assert(myAL.size() > 0);
		for (int i = 0; i < myAL.size(); i++) {
			outString += ";" + myAL.get(i);
			
			if ((i % 7) == 6)  {
				System.out.println(outString);
				outString = "";
			}
		}
		
		outString = "";
		myAL = DatabaseSubsystem.produceReport(EventType.OPEN);
		assert(myAL.size() >= 0);
		for (int i = 0; i < myAL.size(); i++) {
			outString += ";" + myAL.get(i);
			
			if ((i % 7) == 6)  {
				System.out.println(outString);
				outString = "";
			}
		}
		
		outString = "";
		myAL = DatabaseSubsystem.produceReport(EventType.FINISHED);
		assert(myAL.size() >= 0);
		for (int i = 0; i < myAL.size(); i++) {
			outString += ";" + myAL.get(i);
			
			if ((i % 7) == 6)  {
				System.out.println(outString);
				outString = "";
			}
		}
		
	}
	
	public void testVerifyAdminCredentials() {
		System.out.println("Testing verify admin credentials");
		String adminId = new String("andrew");
		String credentials = new String("andrew");
		
		boolean retval = DatabaseSubsystem.verifyAdminCredentials(adminId, credentials);
		
		assert(retval);
		
		retval = DatabaseSubsystem.verifyAdminCredentials("andrew", "badpassword");
		assert(!retval);
	}
	
	public void testFinishedButUnorderedDLE() {
		int numOfChoices = 2;
		int numOfEdges = 1;
		String newDLEId = UUID.randomUUID().toString();
		
		DecisionLineEvent myEvent = new DecisionLineEvent(newDLEId, "my test question", numOfChoices, numOfEdges, EventType.FINISHED, Behavior.ROUNDROBIN);
		myEvent.setDate(new java.util.Date());
		User newUser1 = new User("andrew1", "", 0, numOfEdges);
		User newUser2 = new User("andrew2", "", 1, numOfEdges);
		myEvent.getUsers().add(newUser1);
		myEvent.getUsers().add(newUser2);
		myEvent.setModerator(newUser1.getUser());
		
		Choice newChoice1 = new Choice("Choice 1", 0, -1);
		Choice newChoice2 = new Choice("Choice 2", 1, -1);
		myEvent.getChoices().add(newChoice1);
		myEvent.getChoices().add(newChoice2);
		
		Edge newEdge1 = new Edge(newChoice1, newChoice2, 1);
		Edge newEdge2 = new Edge(newChoice1, newChoice2, 10);
		myEvent.getEdges().add(newEdge1);
		myEvent.getEdges().add(newEdge2);
		
		DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		DecisionLineEvent response = DatabaseSubsystem.readDecisionLineEvent(newDLEId);
		assertTrue(response.getChoice(0).getFinalDecisionOrder() != -1);
		DatabaseSubsystem.deleteEventById(newDLEId);
		
	}
}
