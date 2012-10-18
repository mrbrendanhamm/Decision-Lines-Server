package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
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
		//TODO find using the new Equals operator I just added.
		
		//if not, then load from DB
		myDLE = DatabaseSubsystem.readDecisionLineEvent(myEventId);
		if (myDLE == null)
			return writeFailureResponse();
		
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
	
	Message writeFailureResponse() {
		return null;
	}
}
