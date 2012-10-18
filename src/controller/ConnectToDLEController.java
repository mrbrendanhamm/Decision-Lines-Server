package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import shared.ClientState;
import shared.DatabaseSubsystem;
import shared.IProtocolHandler;
import xml.Message;
import entity.*;

public class ConnectToDLEController implements IProtocolHandler {
	String myEventId;
	String myVersion;
	DecisionLineEvent myDLE;
	
	public ConnectToDLEController() {
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
		
		//if not, then load from DB
		myDLE = DatabaseSubsystem.readDecisionLineEvent(myEventId);
		if (myDLE == null)
			return null;
		
		//associate ClientState with this event id, or some other mechanism to link the two

		
		return generateResponse();
	}

	boolean parseMessage(Message request) {
		NamedNodeMap myAttributes = request.contents.getAttributes();

		for (int i = 0; i < myAttributes.getLength(); i++) {
			if (myAttributes.item(i).getLocalName().equals("version")) {
				myVersion = new String(myAttributes.item(i).getNodeValue());
			}
			else if (myAttributes.item(i).getLocalName().equals("id")) {
				myEventId = new String(myAttributes.item(i).getNodeValue());
			}
		}
		return true;
	}	

	//doh, error here, the only response should be success or failure.  data pull is later
	Message generateResponse() {
		String xmlString = new String ("<?xml version='1.0' encoding='UTF-8'?><response ");
		xmlString = xmlString + "id='" + myEventId + "' ";
		xmlString = xmlString + "version='" + myVersion + "' ";
		xmlString = xmlString + "success='true' ";
		//failure event?
		xmlString = xmlString + ">";
		
		xmlString = xmlString + "<connectReponse>";
		
		
		xmlString = xmlString + "</connectResponse></response>";
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
}
