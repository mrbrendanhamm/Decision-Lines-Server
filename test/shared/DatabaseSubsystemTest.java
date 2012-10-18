package shared;

import junit.framework.TestCase;

import entity.*;

public class DatabaseSubsystemTest extends TestCase {
	public void testReadEdges() {
		//TODO implement
		DecisionLineEvent myDLE = new DecisionLineEvent();

		boolean retval = DatabaseSubsystem.readEdges(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteEdge() {
		//TODO implement
		Edge myEdge = new Edge();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeEdge(myEdge, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadChoices() {
		//TODO implement
		DecisionLineEvent myDLE = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.readChoices(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteChoice() {
		//TODO implement
		Choice myChoice = new Choice();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeChoice(myChoice, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadUsers() {
		//TODO implement
		DecisionLineEvent myDLE = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.readUsers(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteUser() {
		//TODO implement
		User myUser = new User();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeUser(myUser, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadDecisionLineEvent() {
		// This test case only works immediately after running the dls_up.sql code as it is as of 10/16
		String myDLEId = new String("12345");
		
		DecisionLineEvent retval = DatabaseSubsystem.readDecisionLineEvent(myDLEId);
		
		assertTrue(retval != null);
	}
	
	public void testWriteDecisionLineEvent() {
		//TODO implement
		DecisionLineEvent myEvent = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		assertTrue(retval);
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
