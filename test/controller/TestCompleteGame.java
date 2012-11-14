package controller;

import server.ApplicationMain;
import server.MockClient;
import server.Server;
import xml.Message;
import boundary.DatabaseSubsystem;
import boundary.DefaultProtocolHandler;
import entity.ClearModelInstance;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;
import junit.framework.TestCase;

public class TestCompleteGame extends TestCase {
	MockClient client1, client2, client3;
	
	protected void setUp () {
		if (!Message.configure(ApplicationMain.getMessageXSD())) { 
			fail ("unable to configure protocol");
		}
		
		if (!DatabaseSubsystem.connect()) {
			System.out.println("Error, cannot connect to the database");
			System.exit(0);
		}
		
		// make server think there are two connected clients...
		client1 = new MockClient("c1");
		client2 = new MockClient("c2");
		client3 = new MockClient("c3");
		
		Server.register("c1", client1);
		Server.register("c2", client2);
		Server.register("c3", client3);
		
		ClearModelInstance.clearInstance();
	}

	protected void tearDown() {
		Server.unregister("c1");
		Server.unregister("c2");
		Server.unregister("c3");
	}
	
	public void testOpenRoundRobin() {
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();

		// Create open DLE from Client 1
		String testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>" +
				"  <createRequest type='open' question='Test Question' numChoices='3' numRounds='2' behavior='roundRobin'>" +
				"    <choice value='Choice0' index='0'/>" +
				"    <user name='User0' />" +
				"  </createRequest>" +
				"</request>";
		Message myMessage = new Message(testMessageSuccess);
		Message retVal = myHandler.process(client1,  myMessage);
		Message asynch;
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		String dleId = retVal.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
		
		/** Game is Open **/
		// Log in Request from Client 2
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>" +
					"<signInRequest id='" + dleId + "'><user name='User1'/></signInRequest></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted signInReponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));
		
		// Log in client 3
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>" +
				"<signInRequest id='" + dleId + "'><user name='User2'/></signInRequest></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted signInReponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));
		// Broadcasted signInReponse to Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));
		
		// Create Choice 2
		testMessageSuccess =  "<request version='1.0' id='" + client2.id() + "'>"+
				  "<addChoiceRequest id='" + dleId + "' number='1' choice='Choice1'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted choiceResponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addChoiceResponse"));
		// Broadcasted choiceResponse to Client 3
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addChoiceResponse"));
		
		// Create Choice 3
		testMessageSuccess =  "<request version='1.0' id='" + client3.id() + "'>"+
				  "<addChoiceRequest id='" + dleId + "' number='2' choice='Choice2'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted choiceResponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addChoiceResponse"));
		// Broadcasted choiceResponse to Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addChoiceResponse"));
		
		/** Game is Closed **/
		// Check for proper turnResponse
		//retVal = client1.getAndRemoveMessage();
		//assertTrue(retVal.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 1 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='1'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client1, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 3
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 2 receives turnResponse
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 2 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='0' right='1' height='9'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 3
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 3 receives turnResponse
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 3 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='0' right='1' height='17'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 1 receives turnResponse
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 1 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='25'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client1, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 3
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 2 receives turnResponse
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 2 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='0' right='1' height='32'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 3
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 3 receives turnResponse
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		
		// Client 3 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='0' right='1' height='40'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// verify edge received by Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// verify edge received by Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 1 receives turnResponse with completed=true
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(asynch.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		// Verify Client 2 receives turnResponse with completed=true
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(asynch.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		
		// Verify Client 3 receives addEdgeResponse
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		// Verify Client 3 returned value is a turnResponse with completed=true
		assertTrue(retVal.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(retVal.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		
		/** Game is Finished **/
		// do what here?
	}
	
	public void testClosedAsynchronous() {
		DefaultProtocolHandler myHandler = new DefaultProtocolHandler();

		// Create open DLE from Client 1
		String testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>" +
				"  <createRequest type='closed' question='Test Question' numChoices='3' numRounds='2' behavior='asynchronous'>" +
				"    <choice value='Choice0' index='0'/>" +
				"    <choice value='Choice1' index='1'/>" +
				"    <choice value='Choice2' index='2'/>" +
				"    <user name='User0' />" +
				"  </createRequest>" +
				"</request>";
		Message myMessage = new Message(testMessageSuccess);
		Message retVal = myHandler.process(client1,  myMessage);
		Message asynch;
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		String dleId = retVal.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
		
		/** Game is Open **/
		// Log in Request from Client 2
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>" +
					"<signInRequest id='" + dleId + "'><user name='User1'/></signInRequest></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted signInReponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));
		
		// Log in client 3
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>" +
				"<signInRequest id='" + dleId + "'><user name='User2'/></signInRequest></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		// Broadcasted signInReponse to Client 1
		asynch = client1.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));
		// Broadcasted signInReponse to Client 2
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("joinResponse"));

		int edgeHeight = 1;
		
		// Client 2 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		edgeHeight += 8;
		
		// Client 2 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		edgeHeight += 8;
		
		// Client 2 Play Edge 3
		testMessageSuccess = "<request version='1.0' id='" + client2.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client2, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("false"));
		edgeHeight += 8;
		
		// Client 3 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		edgeHeight += 8;
		
		// Client 3 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		edgeHeight += 8;
		
		// Client 3 Play Edge 3
		testMessageSuccess = "<request version='1.0' id='" + client3.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client3, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("false"));
		edgeHeight += 8;
		
		// Client 1 Play Edge 1
		testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client1, myMessage);
		assertTrue(retVal.contents.getAttributes().getNamedItem("success").getNodeValue().equals("true"));
		edgeHeight += 8;
		
		// Client 1 Play Edge 2
		testMessageSuccess = "<request version='1.0' id='" + client1.id() + "'>"+
				"<addEdgeRequest id='" + dleId + "' left='1' right='2' height='" + edgeHeight + "'/></request>";
		myMessage = new Message(testMessageSuccess);
		retVal = myHandler.process(client1, myMessage);;
		assertTrue(retVal.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(retVal.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		
		
		/** Game Now Finished **/
		for (int i = 0; i < 6; i++) {
			asynch = client1.getAndRemoveMessage();
			assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));

			asynch = client2.getAndRemoveMessage();
			assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));

			asynch = client3.getAndRemoveMessage();
			assertTrue(asynch.contents.getFirstChild().getLocalName().equals("addEdgeResponse"));
		}
		
		asynch = client2.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(asynch.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		
		asynch = client3.getAndRemoveMessage();
		assertTrue(asynch.contents.getFirstChild().getLocalName().equals("turnResponse"));
		assertTrue(asynch.contents.getFirstChild().getAttributes().getNamedItem("completed").getNodeValue().equals("true"));
		
	}
}
