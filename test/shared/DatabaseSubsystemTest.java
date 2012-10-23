package shared;

import java.util.ArrayList;
import java.util.UUID;

import junit.framework.TestCase;

import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

public class DatabaseSubsystemTest extends TestCase {
	public void testReadEdges() {
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		myDLE.getChoices().add(new Choice("Choice 1", 1));
		myDLE.getUsersAndEdges().put(new User("azafty",  ""), new ArrayList<Edge>());

		boolean retval = DatabaseSubsystem.readEdges(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteEdge() {
		User myUser = new User("azafty", "");
		Choice myChoice = new Choice("Choice 1", 1);
		int height = 16;
		Edge myEdge = new Edge(myChoice, height);
		String myDLEId = new String("12345");
		
		int retval = DatabaseSubsystem.writeEdge(myEdge, myDLEId, myUser);
		
		assertTrue(retval > 0);
	}
	
	public void testReadChoices() {
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		
		boolean retval = DatabaseSubsystem.readChoices(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteChoice() {
		Choice myChoice = new Choice("Choice 3", 3);
		String myDLEId = new String("12345");
		
		int retval = DatabaseSubsystem.writeChoice(myChoice, myDLEId);
		
		assertTrue(retval > 0);
	}
	
	public void testReadUsers() {
		DecisionLineEvent myDLE = new DecisionLineEvent("12345");
		
		boolean retval = DatabaseSubsystem.readUsers(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteUser() {
		User myUser = new User("azafty2", "andrew");
		String myDLEId = new String("12345");
		int retval = DatabaseSubsystem.writeUser(myUser, myDLEId);
		assertTrue(retval > 0);
	}
	
	public void testReadDecisionLineEvent() {
		String myDLEId = new String("12345");
		DecisionLineEvent retval = DatabaseSubsystem.readDecisionLineEvent(myDLEId);
		assertTrue(retval != null);
	}
	
	public void testWriteDecisionLineEvent() {
		String uniqueId = UUID.randomUUID().toString();
		int numOfChoices = 4;
		int numOfEdges = 3;
		
		DecisionLineEvent myEvent = new DecisionLineEvent(uniqueId, "my test question", numOfChoices, numOfEdges, EventType.CLOSED, Behavior.ROUNDROBIN);
		User newUser1 = new User("andrew1", "");
		User newUser2 = new User("andrew2", "");
		myEvent.getUsersAndEdges().put(newUser1, new ArrayList<Edge>());
		myEvent.getUsersAndEdges().put(newUser2, new ArrayList<Edge>());
		myEvent.setModerator(newUser1.getUser());
		
		Choice newChoice1 = new Choice("Choice 1", 1);
		Choice newChoice2 = new Choice("Choice 2", 2);
		Choice newChoice3 = new Choice("Choice 3", 3);
		myEvent.getChoices().add(newChoice1);
		myEvent.getChoices().add(newChoice2);
		myEvent.getChoices().add(newChoice3);
		
		Edge newEdge1 = new Edge(newChoice1, 1);
		Edge newEdge2 = new Edge(newChoice2, 1);
		Edge newEdge3 = new Edge(newChoice3, 1);
		Edge newEdge4 = new Edge(newChoice1, 2);
		Edge newEdge5 = new Edge(newChoice2, 2);
		myEvent.getUsersAndEdges().get(newUser1).add(newEdge1);
		myEvent.getUsersAndEdges().get(newUser1).add(newEdge2);
		myEvent.getUsersAndEdges().get(newUser1).add(newEdge3);
		myEvent.getUsersAndEdges().get(newUser2).add(newEdge4);
		myEvent.getUsersAndEdges().get(newUser2).add(newEdge5);
		
		int retval = DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		assertTrue(retval > 0);
	}
	
	public void testVerifyAdminCredentials() {
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
