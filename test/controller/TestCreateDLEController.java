package controller;

import xml.Message;
import junit.framework.TestCase;

public class TestCreateDLEController extends TestCase {
	public void testProcess() {
		/* I've done a little bit too broad of a test case here as I'm testing the functionality
		 * of the whole class.  What I should have done was test the individual methods in the
		 * controller first before I get this far (parse message and the like).  because I'm testing 
		 * such a broad scope I need to include the message loader and XSD file below.
		 */
		
		CreateDLEController myController = new CreateDLEController();
		
		//a sample, fully formed create message XML string
		String testMessageSuccess = new String("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request version='1.0' id='abcdef'>" +
				"  <createRequest type='closed' question='Test Question' numChoices='3' numRounds='3' behavior='roundRobin'>" +
				"    <choice value='Choice1' index='0'/>" +
				"    <choice value='Choice2' index='1'/>" +
				"    <choice value='Choice3' index='2'/>" +
				"    <user name='User1' password=''/>" +
				"  </createRequest>" +
				"</request>");
		
		//configure the message handling system. This ensures the supplied message is valid according to the schema 
		if (!Message.configure("draw2choose.xsd")) { 
			System.exit(0);
		}
		Message msg = new Message(testMessageSuccess);
		
		//I'm using this is as a debugging method.  Basically I call this function here, so that when
		// I run the code in debug mode, I can step into this procedure.  It's a good way to review
		// what data is being sent when and why XML is being parsed in a specific way.
		myController.parseMessage(msg);
		
		
		/* somewhat of a bad example here because as of right now all paths return null
		 * eventually the controller will properly handle the message and therefore this
		 * function should return an intelligent evaluation of the function.  In the meantime
		 * I've built the general testing stub and marked it with a todo tag to let me know that
		 * i'll need to come back and finish it
		 */
		//TODO: implement the testing procedure for process(clientState, Message);
		assert(myController.process(null, msg) == null);
	}
}
