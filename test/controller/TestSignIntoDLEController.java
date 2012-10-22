package controller;

import java.util.UUID;

import xml.Message;
import junit.framework.TestCase;

public class TestSignIntoDLEController extends TestCase {
	public void testProcess() {
		SignIntoDLEController myController = new SignIntoDLEController();
		UUID clientIdForServer = UUID.randomUUID();
		
		//a sample, fully formed SignInRequest message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='" + clientIdForServer.toString() + "'>" +
				"  <signInRequest id='12345'>" +
				"    <user name='azafty' />" +
				"  </signInRequest>" +
				"</request>");
		
		if (!Message.configure("draw2choose.xsd")) { 
			fail();
		}
		Message msg = new Message(testMessageSuccess);
		
		Message retVal = myController.process(null, msg);
		
		assert(retVal != null);
	}
}
