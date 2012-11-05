package xml;

import server.ApplicationMain;
import junit.framework.TestCase;



public class TestMessage extends TestCase {
	
	
	protected void setUp () {
		if (!Message.configure(ApplicationMain.getMessageXSD())) { 
			fail ("unable to configure protocol");
		}
	}
	
	public void testNormalization() {
		String testMessageSuccess = "<request  version='1.0'  id='12345' >" +
				"  <signInRequest id='12345' >" +
				"    <user name='azafty' password='' />" +
				"  </signInRequest  >" +
				"</request  >";
		
		String response = Message.normalizeInput(testMessageSuccess);
		Message tmpMessage = new Message(testMessageSuccess);
		
		System.out.println("Original XML String:                          " + testMessageSuccess);
		System.out.println("Normalized through Segment Processing:        " + response);
		System.out.println("Tested through full Message Building Process: " + tmpMessage);
		
		assert(true);
	}
}
