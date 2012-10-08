package shared;

import junit.framework.TestCase;

public class DatabaseSubsystemTest extends TestCase {
	public void testReadEdges() {
		DecisionLineEvent myDLE = new DecisionLineEvent();

		boolean retval = DatabaseSubsystem.readEdges(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteEdge() {
		Edge myEdge = new Edge();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeEdge(myEdge, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadChoices() {
		DecisionLineEvent myDLE = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.readChoices(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteChoice() {
		Choice myChoice = new Choice();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeChoice(myChoice, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadUsers() {
		DecisionLineEvent myDLE = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.readUsers(myDLE);
		
		assertTrue(retval);
	}
	
	public void testWriteUser() {
		User myUser = new User();
		String myDLEId = new String();
		
		boolean retval = DatabaseSubsystem.writeUser(myUser, myDLEId);
		
		assertTrue(retval);
	}
	
	public void testReadDecisionLineEvent() {
		String myDLEId = new String();
		
		DecisionLineEvent retval = DatabaseSubsystem.readDecisionLineEvent(myDLEId);
		
		assertTrue(retval != null);
	}
	
	public void testWriteDecisionLineEvent() {
		DecisionLineEvent myEvent = new DecisionLineEvent();
		
		boolean retval = DatabaseSubsystem.writeDecisionLineEvent(myEvent);
		
		assertTrue(retval);
	}
	
	public void testVerifyAdminCredentials() {
		String adminId = new String();
		String credentials = new String();
		
		boolean retval = DatabaseSubsystem.verifyAdminCredentials(adminId, credentials);
		
		assertTrue(retval);
	}
}
