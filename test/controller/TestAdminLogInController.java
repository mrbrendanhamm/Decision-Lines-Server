package controller;

import java.util.UUID;

import xml.Message;
import junit.framework.TestCase;

public class TestAdminLogInController extends TestCase {
	public void testProcess(){
		AdminLogInController myController = new AdminLogInController();
		UUID clientIdForServer = UUID.randomUUID();

		//a sample, fully formed SignInRequest message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='" + clientIdForServer.toString() + "'>" +
				"  <adminRequest>" +
				"    <user name='admin' password='password' />" +
				"  </adminRequest>" +
				"</request>");
		
		if (!Message.configure("draw2choose.xsd")) { 
			System.exit(0);
		}
		
		Message msg = new Message(testMessageSuccess);
		Message retVal = myController.process(null, msg);
		assert(retVal != null);
		
	}

}
