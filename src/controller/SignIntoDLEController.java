package controller;

import java.util.ArrayList;

import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import entity.User;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import boundary.DatabaseSubsystem;
import xml.Message;

/**
 * This controller handles all <signInRequest> messages coming from the client.
 */
public class SignIntoDLEController implements IProtocolHandler {
	String myEventId;
	String clientIdToServer;
	//String myVersion;
	String userName;
	String userPassword;
	DecisionLineEvent myDLE;
	User newUser;
	
	/**
	 * The default constructor
	 */
	public SignIntoDLEController() {
		myEventId = new String("");
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
		// Initialize local variables
		Model myModel = Model.getInstance();
		
		// Read in the Request
		if (!parseMessage(request)) {
			//do error message
			return null;
		}
		
		//is it already in the model?
		int indexOf = myModel.getDecisionLineEvents().indexOf(new DecisionLineEvent(myEventId));
		if (indexOf < 0) { //doesn't exist in the model yet.  Read from DB
			myDLE = DatabaseSubsystem.readDecisionLineEvent(myEventId);
			
			if (myDLE == null) //not found in DB, return failure
				return writeFailureResponse("Event:" + myEventId + " does not exist in database.");
			
			myModel.getDecisionLineEvents().add(myDLE);
		}
		else
			myDLE = Model.getInstance().getDecisionLineEvents().get(indexOf);
		

		newUser = new User(userName, userPassword, -1, myDLE.getNumberOfEdges());

		boolean userAlreadyExists = false;
		ArrayList<User> userList = myDLE.getUsers();
		//Locate if user is already associated with DLE
		for (int i = 0; i < userList.size(); i++) {
			if (newUser.getUser().equals(userList.get(i).getUser())) {
				//User id already connected to DLE.  Check password
				if (newUser.getPassword().equals(userList.get(i).getPassword())) {
					newUser = userList.get(i);
					userAlreadyExists = true;
				}
				else //error, incorrect password
					return writeFailureResponse("Invalid password for user: " + newUser.getUser());
			}
		}
		
		
		if (!userAlreadyExists) { //Create the user if they are new
			if (userList.size() >= myDLE.getNumberOfChoices()) {
				return writeFailureResponse("Error, maximum number of users exceeded!");
			}
			
			newUser.setPosition(userList.size());
			userList.add(newUser);
			DatabaseSubsystem.writeUser(newUser,  myDLE.getUniqueId());
		}
		
		//associate ClientState with this event id
		myDLE.addClientConnection(newUser.getUser(), clientIdToServer);
		
		//TODO what else do I have to verify?  Just that the user count isn't exceeded and the User/Password match?
		
		//notify all other connected clients that a new client is on board
		for(int i = 0; i < userList.size(); i++) {
			String processing = userList.get(i).getClientStateId();
			if (!processing.equals(clientIdToServer) && !processing.equals("")) {
				Server.getState(processing).sendMessage(writeJoinNotification(processing));
			}
		}

		//TODO This will result in the turn announcement arriving prior to the sign into success notice.  This is a reoccuring problem
		// Let TurnAnnouncementController handle the turn responses 
		//new TurnAnnouncementController(myDLE).userSignIn(newUser);
		
		return writeSuccessResponse();
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
		//myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());
		
		Node child = request.contents.getFirstChild();
		
		myEventId = child.getAttributes().getNamedItem("id").getNodeValue();
		
		child = child.getFirstChild();
		userName = child.getAttributes().getNamedItem("name").getNodeValue();
		
		if (child.getAttributes().getNamedItem("password") != null)
			userPassword = child.getAttributes().getNamedItem("password").getNodeValue();
		else
			userPassword = new String("");
		
		return true;
	}	

	/**
	 * This method creates a JoinResponse message that will be sent to notify other connected clients that a new 
	 * client has logged into the same DecisionLineEvent
	 * 
	 * @param localClientId - the ClientState Id used by the connected client
	 * @return - A properly formatted joinResponse message or null if one cannot be formed
	 */
	Message writeJoinNotification(String localClientId) {
		String xmlString = Message.responseHeader(localClientId) +
				"<joinResponse id='" + localClientId + "'>" +
				"  <user name='" + newUser.getUser() + "' />" +
				"</joinResponse></response>";
		
		Message newMsg = new Message(xmlString);
		System.out.println("Broadcast: " + newMsg);
		
		return newMsg;
	}

	/**
	 * This method creates the appropriate success response.  
	 *  
	 * @return A properly formatted Success response, or null if a message cannot be properly formed
	 */
	Message writeSuccessResponse() {
		String xmlString = Message.responseHeader(clientIdToServer) +
				"<signInResponse id='" + myEventId + "' ";
		if (myDLE.getEventType() == EventType.CLOSED || myDLE.getEventType() == EventType.FINISHED)
			xmlString = xmlString + "type='closed' ";
		else
			xmlString = xmlString + "type='open' ";

		if (myDLE.getBehavior() == Behavior.ROUNDROBIN)
			xmlString = xmlString + "behavior='roundRobin' ";
		else
			xmlString = xmlString + "behavior='asynchronous' ";

		xmlString = xmlString + "question='" + myDLE.getQuestion() + "' " +
				" numChoices='" + myDLE.getNumberOfChoices() + "' numRounds='" + myDLE.getNumberOfEdges() + "' " +
				"position='" + newUser.getPosition() + "'>"; 
		
		for (int i = 0; i < myDLE.getChoices().size(); i++) {
			Choice tmpChoice = myDLE.getChoices().get(i);
			xmlString = xmlString + "<choice value='" + tmpChoice.getName() + "' index='" + tmpChoice.getOrder() + "'/>";
		}
		
		ArrayList<User> userList = myDLE.getUsers();
		for (int i = 0; i < userList.size(); i++) {
			if (!userList.get(i).equals(newUser)) {
				xmlString = xmlString + "<user name='" + userList.get(i).getUser() + "'/>";
			}	
		}
				
		xmlString = xmlString + "</signInResponse></response>";
		Message myMsg = new Message(xmlString);
		System.out.println("Responding: " + myMsg.toString());
		
		return myMsg;
	}

	/**
	 * This method creates the appropriate failure response.  
	 * 
	 * @param reason - the reason for the failure
	 * @return a properly formatted XML response
	 */
	Message writeFailureResponse(String reason) {
		String xmlString;
		if (myDLE == null) {
			xmlString = Message.responseHeader(clientIdToServer, reason) + 
					"<signInResponse id='" + myEventId + "' type='closed' behavior='roundRobin' question='question' " +
					"numChoices='0' numRounds='1' position='0'/></response>";
		}
		else {
			xmlString = Message.responseHeader(clientIdToServer, reason) + 
					"<signInResponse id='" + myDLE.getUniqueId() + "' ";
			if (myDLE.getEventType() == EventType.CLOSED || myDLE.getEventType() == EventType.FINISHED)
				xmlString = xmlString + "type='closed' ";
			else
				xmlString = xmlString + "type='open' ";
	
			if (myDLE.getBehavior() == Behavior.ROUNDROBIN)
				xmlString = xmlString + "behavior='roundRobin' ";
			else
				xmlString = xmlString + "behavior='asynchronous' ";
	
			xmlString = xmlString + "question='" + myDLE.getQuestion() + "' " +
					" numChoices='" + myDLE.getNumberOfChoices() + "' numRounds='" + myDLE.getNumberOfEdges() + "' " +
					"position='" + newUser.getPosition() + "'>"; 
			
			for (int i = 0; i < myDLE.getChoices().size(); i++) {
				Choice tmpChoice = myDLE.getChoices().get(i);
				xmlString = xmlString + "<choice value='" + tmpChoice.getName() + "' index='" + tmpChoice.getOrder() + "'/>";
			}
			xmlString = xmlString + "</signInResponse></response>";
		}
		System.out.println("Error Response: " + xmlString);
		return new Message(xmlString);
	}
}
