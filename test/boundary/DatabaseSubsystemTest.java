package boundary;

import java.util.UUID;

import boundary.DatabaseSubsystem;

import junit.framework.TestCase;

import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

public class DatabaseSubsystemTest extends TestCase {
	public void testReadEdges() {
		System.out.println("Testing read edges");
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		myDLE.getChoices().add(new Choice("Choice 1", 1, -1));
		myDLE.getUsers().add(new User("azafty",  "", 0));

		boolean retval = DatabaseSubsystem.readEdges(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteEdge() {
		System.out.println("Testing write edges");
		Choice myLeftChoice = new Choice("Choice 1", 0, -1);
		Choice myRightChoice = new Choice("Choice 2", 1, -1);
		int height = 16;
		Edge myEdge = new Edge(myLeftChoice, myRightChoice, height);
		String myDLEId = new String("12345");
		
		int retval = DatabaseSubsystem.writeEdge(myEdge, myDLEId);
		
		assertTrue(retval > 0);
	}
	
	public void testReadChoices() {
		System.out.println("Testing read choices");
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		
		boolean retval = DatabaseSubsystem.readChoices(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteChoice() {
		System.out.println("Testing write choices");
		Choice myChoice = new Choice("Choice 3", 3, -1);
		String myDLEId = new String("12345");
		
		int retval = DatabaseSubsystem.writeChoice(myChoice, myDLEId);
		
		assertTrue(retval > 0);
	}
	
	public void testReadUsers() {
		System.out.println("Testing read users");
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		
		boolean retval = DatabaseSubsystem.readUsers(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteUser() {
		System.out.println("Testing write user");
		User myUser = new User("azafty2", "", 0);
		String myDLEId = new String("12345");
		int retval = DatabaseSubsystem.writeUser(myUser, myDLEId);
		assertTrue(retval > 0);
	}
	
	public void testReadDecisionLineEvent() {
		System.out.println("Testing read decisionlineevent");
		String myDLEId = new String("12345");
		DecisionLineEvent retval = DatabaseSubsystem.readDecisionLineEvent(myDLEId);
		assertTrue(retval != null);
	}
	
	public void testWriteDecisionLineEvent() {
		System.out.println("Testing write decisionlineevent");
		String uniqueId = UUID.randomUUID().toString();
		int numOfChoices = 4;
		int numOfEdges = 3;
		
		DecisionLineEvent myEvent = new DecisionLineEvent(uniqueId, "my test question", numOfChoices, numOfEdges, EventType.CLOSED, Behavior.ROUNDROBIN);
		User newUser1 = new User("andrew1", "", 0);
		User newUser2 = new User("andrew2", "", 1);
		myEvent.getUsers().add(newUser1);
		myEvent.getUsers().add(newUser2);
		myEvent.setModerator(newUser1.getUser());
		
		Choice newChoice1 = new Choice("Choice 1", 1, -1);
		Choice newChoice2 = new Choice("Choice 2", 2, -1);
		Choice newChoice3 = new Choice("Choice 3", 3, -1);
		Choice newChoice4 = new Choice("Choice 4", 4, -1);
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
	
	public void testVerifyAdminCredentials() {
		System.out.println("Testing verify admin credentials");
		String adminId = new String("andrew");
		String credentials = new String("andrew");
		
		try {
			boolean retval = DatabaseSubsystem.verifyAdminCredentials(adminId, credentials);
		
			assertTrue(retval);
		} catch (IllegalArgumentException e) {
			fail("invalid login");
		}
	}
}
