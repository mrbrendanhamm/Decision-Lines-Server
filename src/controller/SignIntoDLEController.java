package controller;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.Edge;
import entity.Model;
import entity.User;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import boundary.DatabaseSubsystem;
import xml.Message;

public class SignIntoDLEController implements IProtocolHandler {
	String myEventId;
	String clientIdToServer;
	String myVersion;
	DecisionLineEvent myDLE;
	User newUser;
	
	public SignIntoDLEController() {
		myEventId = new String("");
		myVersion = new String("");
	}

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

		//associate ClientState with this event id
		myDLE.getConnectedClients().add(clientIdToServer);
		
		//Set the appropriate position and add user to DLE
		if (myDLE.getUsersAndEdges().containsKey(newUser)) { //user has already logged in
			Iterator<User> tmpIt = myDLE.getUsersAndEdges().keySet().iterator();
			
			while (tmpIt.hasNext()) {
				User tmpUse = tmpIt.next();
				if (newUser.getUser().equals(tmpUse.getUser())) {
					if (newUser.getPassword().equals(tmpUse.getPassword()))
						newUser = tmpUse;
					else //error, incorrect password
						return writeFailureResponse("Invalid password for user: " + newUser.getUser());
				}
			}
		}
		else { //new user that has never logged in
			newUser.setPosition(myDLE.getUsersAndEdges().keySet().size());
			myDLE.getUsersAndEdges().put(newUser, new ArrayList<Edge>());
			DatabaseSubsystem.writeUser(newUser,  myDLE.getUniqueId());
		}
		
		//notify all other connected clients that a new client is on board
		ArrayList<String> connectedList = myDLE.getConnectedClients();
		for (String processing : connectedList) {
			if (!processing.equals(clientIdToServer)) {
				Server.getState(processing).sendMessage(writeJoinNotification(processing));
			}
		}
		
		return writeSuccessResponse();
	}

	boolean parseMessage(Message request) {
		myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());
		
		Node child = request.contents.getFirstChild();
		child = child.getNextSibling();
		
		myEventId = child.getAttributes().getNamedItem("id").getNodeValue();
		
		child = child.getFirstChild().getNextSibling();
		String userName = child.getAttributes().getNamedItem("name").getNodeValue();
		String userPassword;
		
		if (child.getAttributes().getNamedItem("password") != null)
			userPassword = child.getAttributes().getNamedItem("password").getNodeValue();
		else
			userPassword = new String("");
		
		newUser = new User(userName, userPassword, -1);

		return true;
	}	

	Message writeJoinNotification(String localClientId) {
		String xmlString = Message.responseHeader(localClientId) +
				"<joinResponse id='" + localClientId + "'>" +
				"  <user name='" + newUser.getUser() + "' />" +
				"</joinResponse></response>";
		
		Message newMsg = new Message(xmlString);
		System.out.println("Broadcast: " + newMsg);
		
		return newMsg;
	}
	
	Message writeSuccessResponse() {
		String xmlString = Message.responseHeader(clientIdToServer) +
				"<signInResponse id='" + myDLE.getUniqueId() + "' ";
		if (myDLE.getIsClosed())
			xmlString = xmlString + "type='closed' ";
		else
			xmlString = xmlString + "type='open' ";

		if (myDLE.getBehavior() == Behavior.ROUNDROBIN)
			xmlString = xmlString + "behavior='roundRobin' ";
		else
			xmlString = xmlString + "behavior='asynchronous' ";

		xmlString = xmlString + "question='" + myDLE.getQuestion() + "' " +
				" numChoices='" + myDLE.getNumberOfChoice() + "' numRounds='" + myDLE.getNumberOfEdge() + "' " +
				"position='" + newUser.getPosition() + "'>"; 
		
		for (int i = 0; i < myDLE.getChoices().size(); i++) {
			Choice tmpChoice = myDLE.getChoices().get(i);
			xmlString = xmlString + "<choice value='" + tmpChoice.getName() + "' index='" + tmpChoice.getOrder() + "'/>";
		}
		
		xmlString = xmlString + "</signInResponse></response>";
		Message myMsg = new Message(xmlString);
		System.out.println("Responding: " + myMsg.toString());
		
		return myMsg;
	}
	
	Message writeFailureResponse(String reason) {
		String xmlString = Message.responseHeader(clientIdToServer, reason) + 
				"<signInResponse id='" + myDLE.getUniqueId() + "' ";
		if (myDLE.getIsClosed())
			xmlString = xmlString + "type='closed' ";
		else
			xmlString = xmlString + "type='open' ";

		if (myDLE.getBehavior() == Behavior.ROUNDROBIN)
			xmlString = xmlString + "behavior='roundRobin' ";
		else
			xmlString = xmlString + "behavior='asynchronous' ";

		xmlString = xmlString + "question='" + myDLE.getQuestion() + "' " +
				" numChoices='" + myDLE.getNumberOfChoice() + "' numRounds='" + myDLE.getNumberOfEdge() + "' " +
				"position='" + newUser.getPosition() + "'>"; 
		
		for (int i = 0; i < myDLE.getChoices().size(); i++) {
			Choice tmpChoice = myDLE.getChoices().get(i);
			xmlString = xmlString + "<choice value='" + tmpChoice.getName() + "' index='" + tmpChoice.getOrder() + "'/>";
		}
		xmlString = xmlString + "</signInResponse></response>";
		System.out.println("Error Response: " + xmlString);
		return new Message(xmlString);
	}
}
