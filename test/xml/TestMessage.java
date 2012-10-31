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
		Message tmpMessage = new Message(response);
		
		System.out.println(testMessageSuccess);
		System.out.println(response);
		System.out.println(tmpMessage);
		
		assert(true);
	}
}
