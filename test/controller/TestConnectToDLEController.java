package controller;

import java.util.UUID;

import xml.Message;
import junit.framework.TestCase;

public class TestConnectToDLEController extends TestCase {
	public void testProcess() {
		ConnectToDLEController myController = new ConnectToDLEController();
		UUID clientIdForServer = UUID.randomUUID();
		
		//a sample, fully formed create message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='" + clientIdForServer.toString() + "'>" +
				"  <connectRequest />" +
				"</request>");
		
		if (!Message.configure("draw2choose.xsd")) { 
			fail();
		}
		Message msg = new Message(testMessageSuccess);
		
		//myController.parseMessage(msg);
		Message retVal = myController.process(null, msg);
		
		assert(retVal != null);
	}
	
}
