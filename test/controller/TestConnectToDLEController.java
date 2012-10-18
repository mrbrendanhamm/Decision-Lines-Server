package controller;

import xml.Message;
import junit.framework.TestCase;

public class TestConnectToDLEController extends TestCase {
	public void testProcess() {
		ConnectToDLEController myController = new ConnectToDLEController();
		
		//a sample, fully formed create message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='12345'>" +
				"  <connectRequest />" +
				"</request>");
		
		if (!Message.configure("draw2choose.xsd")) { 
			System.exit(0);
		}
		Message msg = new Message(testMessageSuccess);
		
		//myController.parseMessage(msg);
		myController.process(null, msg);
		
		//TODO: implement the testing procedure for process(clientState, Message);
		assert(myController.process(null, msg) == null);
	}
	
}
