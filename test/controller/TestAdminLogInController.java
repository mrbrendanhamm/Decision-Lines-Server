package controller;

import java.util.UUID;

import server.MockClient;
import server.Server;
import entity.ClearModelInstance;
import entity.Model;

import xml.Message;
import junit.framework.TestCase;

public class TestAdminLogInController extends TestCase {
	
	MockClient client1, client2;
	Model model;
	
	protected void setUp() {
		// FIRST thing to do is register the protocol being used.
		if (!Message.configure("draw2choose.xsd")) {
			fail ("unable to configure protocol");
		}
				
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		

		Server.register("c1", client1);
		Server.register("c2", client2);
		
		// clear the singleton
		ClearModelInstance.clearInstance();
	}
	
	public void testProcess(){
		AdminLogInController myController = new AdminLogInController();

		//a sample, fully formed SignInRequest message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='" + client1 + "'>" +
					"<adminRequest>" +
				  		"<user name='andrew' password='andrew' />" +
				  	"</adminRequest>" +
				"</request>");
		System.out.println(testMessageSuccess);
		if (!Message.configure("draw2choose.xsd")) { 
			fail();
		}
		
		Message msg = new Message(testMessageSuccess);
		Message retVal = myController.process(client1, msg);
		assert(retVal != null);
		
	}

}
