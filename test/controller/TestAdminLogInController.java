package controller;

import org.w3c.dom.Node;

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
	
	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
	}
	/** test the Process message for the AdminLogInController
	 *  Takes client1 and signs him in as the admin.  AdminLoginController should
	 *	response should contain the key used for further transmissions
	 */
	public void testProcessSuccess(){
		Model myModel = Model.getInstance();
		AdminLogInController myController = new AdminLogInController();

		//A valid adminRequest message
		String testMessageSuccess = new String(
				"<request id='c1'>" +
					"<adminRequest>"+
						"<user name='andrew' password='andrew'/>" +
				  	"</adminRequest>" +
				"</request>");
		//make sure xsd is configured
		if (!Message.configure("draw2choose.xsd")) { 
			fail();
		}
		
		Message msg = new Message(testMessageSuccess);
		Message retVal = myController.process(client1, msg);
		assert(retVal != null);
		Node child = retVal.contents.getFirstChild();
		String keyString=child.getAttributes().getNamedItem("key").getNodeValue();
		assert(keyString.equals(myModel.getKey()));
	}
	
	
	/** This method tests for a failure given invalid credentials;
	 * 
	 */

	public void testProcessInvalidCredentials(){
		Model.getInstance();
		AdminLogInController myController = new AdminLogInController();

		//A valid adminRequest message with bad credentials
		String testMessageSuccess = new String(
				"<request id='c1'>" +
					"<adminRequest>"+
						"<user name='notAdmin' password='wrongPW'/>" +
				  	"</adminRequest>" +
				"</request>");
		//make sure xsd is configured
		if (!Message.configure("draw2choose.xsd")) { 
			fail();
		}
		Message msg = new Message(testMessageSuccess);
		Message retVal = myController.process(client1, msg);
		assert(retVal != null);
		
		//check that key is not handed back
		Node child = retVal.contents.getFirstChild();
		String keyString=child.getAttributes().getNamedItem("key").getNodeValue();
		assert(keyString.equals(""));
	}


}
