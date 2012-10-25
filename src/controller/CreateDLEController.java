package controller;

import java.util.ArrayList;
import java.util.UUID;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import boundary.DatabaseSubsystem;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;
import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

public class CreateDLEController implements IProtocolHandler {
	ArrayList<Choice> myChoices; 
	EventType myType;
	Behavior myBehavior;
	String myQuestion;
	int numOfChoices;
	int numOfRounds;
	String moderator;
	String moderatorPassword;
	String myEventId;
	String myVersion;
	String clientIdToServer;
	DecisionLineEvent createdDLE;
	
	public CreateDLEController() {
		myChoices = new ArrayList<Choice>();
		myQuestion = new String();
	}
	
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Read in the Request
		if (!parseMessage(request)) {
			//return failure
			return null;
		}
		
		//Check for missing parameters
		if (myQuestion.equals("")) /* test all parameters here */ { 
			//debug message for a malformed message
			return writeFailureResponse("No question has been provided");
		}
		
		//I generate the event Id and return it to the client.  probably something better than this massive string however
		myEventId = UUID.randomUUID().toString();
		
		createdDLE = new DecisionLineEvent(myEventId, myQuestion, numOfChoices, numOfRounds, myType, myBehavior);
		createdDLE.setModerator(moderator);
		User newModerator = new User(moderator, moderatorPassword, 0);
		for (int i = 0; i < myChoices.size(); i++)
			createdDLE.getChoices().add(myChoices.get(i));
		createdDLE.getUsersAndEdges().put(newModerator, new ArrayList<Edge>());
		
		//Update the model appropriately
		Model.getInstance().getDecisionLineEvents().add(createdDLE);
		
		//Write to the database
		DatabaseSubsystem.writeDecisionLineEvent(createdDLE);
		
		//Register client
		createdDLE.getConnectedClients().add(clientIdToServer);
		
		return writeSuccessResponse(); //this specific message is sent back to the requesting client
	}
		
	
	/**
	 * This method performs the XML manipulation to read the message and separate out the data.
	 * In general there are two types of data: Nodes and Attributes.  Attributes exist only on Nodes
	 * Nodes can hold other Nodes.  So parsing really just means traversing the node list and knowing
	 * when to query for attributes.  
	 *  
	 * @param request - XML Message of type createRequest that needs to be parsed for inputs
	 * @return true if successfully parsed
	 */
	boolean parseMessage(Message request) {
		myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());

		Node child = request.contents.getFirstChild();
		child = child.getNextSibling();
		
		if (child.getAttributes().getNamedItem("type").getNodeValue().equals("open"))
			myType = EventType.OPEN;
		else
			myType = EventType.CLOSED;
			
		if (child.getAttributes().getNamedItem("behavior").getNodeValue().equals("roundRobin"))
			myBehavior = Behavior.ROUNDROBIN;
		else
			myBehavior = Behavior.ASYNCHRONOUS;
			
		myQuestion = new String(child.getAttributes().getNamedItem("question").getNodeValue());
		numOfChoices = Integer.parseInt(child.getAttributes().getNamedItem("numChoices").getNodeValue());
		numOfRounds = Integer.parseInt(child.getAttributes().getNamedItem("numRounds").getNodeValue());
		
		//one layer deep are the nodes that hold information for the Choices and the Moderator.
		//unfortunately children nodes are accessed through a separate entity called a 'NodeList'
		NodeList myList = child.getChildNodes();

		// Parse through each child.  Each of these children also have attributes
		for (int i = 0; i < myList.getLength(); i++) { 
			if (myList.item(i).getNodeName().equals("choice")) {  //A Choice has been found
				String choiceName = new String("");
				int indexOf = -1;
				
				choiceName = myList.item(i).getAttributes().getNamedItem("value").getNodeValue();
				indexOf = Integer.parseInt(myList.item(i).getAttributes().getNamedItem("index").getNodeValue());
				
				Choice newChoice = new Choice(choiceName, indexOf);
				myChoices.add(newChoice);
			}
			else if (myList.item(i).getNodeName().equals("user")) {  //A Moderator has been found
				moderator = myList.item(i).getAttributes().getNamedItem("name").getNodeValue();
				
				if (myList.item(i).getAttributes().getNamedItem("password") != null)
					moderatorPassword = myList.item(i).getAttributes().getNamedItem("password").getNodeValue();
				else
					moderatorPassword = new String("");
			}
		}
		
		//Theoretically, the message has been fully parsed at this point
		return true;
	}

	/**
	 * This method creates the appropriate response.  Might need to be split into two methods, one for 
	 * Open and one for Closed DLEs
	 *  
	 * @return A properly formatted Success method, or null if a message cannot be properly formed
	 */
	Message writeSuccessResponse() {
		String xmlString = Message.responseHeader(clientIdToServer) +
				"<createResponse id='error'/>" +
				"</response>";
		System.out.println(xmlString);
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
	
	Message writeFailureResponse(String reason) {
		String xmlString = Message.responseHeader(clientIdToServer, reason) +
				"<createResponse id='" + myEventId + "'/>" +
				"</response>";
		System.out.println(xmlString);
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
}

