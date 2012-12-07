package controller;

import java.util.ArrayList;
import java.util.Date;
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

/**
 *  This controller handles all <createRequest> requests coming from the client applications.
 */
public class CreateDLEController implements IProtocolHandler {
	// local variable storage.
	ArrayList<Choice> myChoices; 
	EventType myType;
	Behavior myBehavior;
	String myQuestion;
	int numOfChoices;
	int numOfRounds;
	String moderator;
	String moderatorPassword;
	String myEventId;
	String clientIdToServer;
	DecisionLineEvent createdDLE;
	Date dleDate;
	
	/**
	 * Default constructor
	 */
	public CreateDLEController() {
		myChoices = new ArrayList<Choice>();
	}
	
	/**
	 * This method is the calling entry point for this controller.  It is assumed that the message type is appropriate
	 * for this controller.
	 * 
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Read in the Request
		if (!parseMessage(request)) {
			//return failure
			return null;
		}
		
		//Check for missing or invalid parameters
		if (myQuestion.equals("")) //missing question
			return writeFailureResponse("No question has been provided");
		if (myType == EventType.ERROR) 
			return writeFailureResponse("Unrecognized event type");
		if (myBehavior == Behavior.ERROR) 
			return writeFailureResponse("Unrecognized event behavior");
		if (numOfRounds < 0)
			return writeFailureResponse("Set at least 0 rounds of edge selection");
		//if (numOfChoices < 1)
		//	return writeFailureResponse("Ensure that there can be at least 1 choice");
		//if (myType == EventType.CLOSED && myChoices.size() != numOfChoices) 
		//	return writeFailureResponse("Moderator must set every choice in a closed event prior to creating event");
		if (moderator == null) 
			return writeFailureResponse("A Moderator must be included in the create request");
		
		//I generate the event Id and return it to the client.  probably something better than the massive UUID string
		myEventId = UUID.randomUUID().toString();
		dleDate = new Date();
		
		//instantiate the DLE
		createdDLE = new DecisionLineEvent(myEventId, myQuestion, numOfChoices, numOfRounds, myType, myBehavior);
		createdDLE.setModerator(moderator);
		createdDLE.setDate(dleDate);
		User newModerator = new User(moderator, moderatorPassword, 0, numOfRounds);
		createdDLE.setCurrentTurn(newModerator);
		
		//add the appropriate choices
		for (int i = 0; i < myChoices.size(); i++)
			createdDLE.getChoices().add(myChoices.get(i));
		
		//set the moderator also as a user
		createdDLE.getUsers().add(newModerator);
		
		//Update the model appropriately
		Model.getInstance().getDecisionLineEvents().add(createdDLE);
		
		//Write to the database
		DatabaseSubsystem.writeDecisionLineEvent(createdDLE);
		
		/*
		 * Register client so that future requests can use the clientIdServer to tie back to this user and determine when
		 * there are no longer any users connected to a DLE 
		 */
		createdDLE.addClientConnection(newModerator.getUser(), state.id());
		
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
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());

		Node child = request.contents.getFirstChild();
		
		if (child.getAttributes().getNamedItem("type").getNodeValue().equals("open"))
			myType = EventType.OPEN;
		else if (child.getAttributes().getNamedItem("type").getNodeValue().equals("closed"))
			myType = EventType.CLOSED;
		else
			myType = EventType.ERROR;
			
		if (child.getAttributes().getNamedItem("behavior").getNodeValue().equals("roundRobin"))
			myBehavior = Behavior.ROUNDROBIN;
		else if (child.getAttributes().getNamedItem("behavior").getNodeValue().equals("asynchronous"))
			myBehavior = Behavior.ASYNCHRONOUS;
		else myBehavior = Behavior.ERROR;
			
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
				
				Choice newChoice = new Choice(choiceName, indexOf, -1);
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
	 * This method creates the appropriate success response.  
	 *  
	 * @return A properly formatted Success response, or null if a message cannot be properly formed
	 */
	Message writeSuccessResponse() {
		String xmlString = Message.responseHeader(clientIdToServer) +
				"<createResponse id='" + myEventId + "'/>" +
				"</response>";
		Message myMsg = new Message(xmlString);
		System.out.println("Responding: " + myMsg);
		
		return myMsg;
	}

	/**
	 * This method creates the appropriate failure response.  
	 * 
	 * @param reason - the reason for the failure
	 * @return a properly formatted XML response
	 */
	Message writeFailureResponse(String reason) {
		String xmlString = Message.responseHeader(clientIdToServer, reason) +
				"<createResponse id='" + myEventId + "'/>" +
				"</response>";
		Message myMsg = new Message(xmlString);
		System.out.println("Failure: " + myMsg);
		
		return myMsg;
	}
}

