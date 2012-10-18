package controller;

import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.Model;
import shared.ClientState;
import shared.DatabaseSubsystem;
import shared.IProtocolHandler;
import xml.Message;

public class SignIntoDLEController implements IProtocolHandler {
	String myEventId;
	String clientIdToServer;
	String myVersion;
	DecisionLineEvent myDLE;
	
	public SignIntoDLEController() {
		myEventId = new String("");
		myVersion = new String("");
	}

	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Initialize local variables
		
		// Read in the Request
		if (!parseMessage(request)) {
			//do error message
			return null;
		}
		
		//is it already in the model?
		int indexOf = Model.getInstance().getDecisionLineEvents().indexOf(new DecisionLineEvent(myEventId));
		if (indexOf < 0) { //doesn't exist in the model yet.  Read from DB
			myDLE = DatabaseSubsystem.readDecisionLineEvent(myEventId);
			
			if (myDLE == null) //not found in DB, return failure
				return writeFailureResponse("Does not exist in database!");
			
			Model.getInstance().getDecisionLineEvents().add(myDLE);
		}
		else
			myDLE = Model.getInstance().getDecisionLineEvents().get(indexOf);
			
		//associate ClientState with this event id, or some other mechanism to link the two
		
		return writeSuccessResponse();
	}

	boolean parseMessage(Message request) {
		myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());
		
		Node child = request.contents.getFirstChild();
		child = child.getNextSibling();
		
		myEventId = child.getAttributes().getNamedItem("id").getNodeValue();
		
		return true;
	}	

	Message writeSuccessResponse() {
		String xmlString = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<response id='" + clientIdToServer + "' version='" + myVersion + "' success='true'>" +
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
				"position='1'>"; //TODO define the position indicator here
		
		for (int i = 0; i < myDLE.getChoices().size(); i++) {
			Choice tmpChoice = myDLE.getChoices().get(i);
			xmlString = xmlString + "<choice value='" + tmpChoice.getName() + "' index='" + tmpChoice.getOrder() + "'/>";
		}
		
		xmlString = xmlString + "</signInResponse></response>";
		System.out.println("Responding: " + xmlString);
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
	
	Message writeFailureResponse(String reason) {
		return null;
	}
}
