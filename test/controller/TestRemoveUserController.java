package controller;

import entity.ClearModelInstance;
import server.MockClient;
import server.Server;
import xml.Message;
import junit.framework.TestCase;

//This test case will have to wait is there is no removeUser request/response in xsd

public class TestRemoveUserController extends TestCase {
	MockClient client1, client2;
	
	protected void setUp () {
		if (!Message.configure("draw2choose.xsd")) { 
			fail ("unable to configure protocol");
		}
		
		// make server think there are two connected clients...
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		
		Server.register("c1", client1);
		Server.register("c2", client2);
		
		ClearModelInstance.clearInstance();
	}
	
	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
	}
	
	public void TestProcess(){
		
	}

}
