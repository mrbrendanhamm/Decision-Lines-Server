package controller;

import java.util.UUID;

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
	String clientIdToServer;
	String myVersion;
	UUID serverIdForClient;
	
	public ConnectToDLEController() {
		clientIdToServer = new String("");
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
		
		serverIdForClient = UUID.randomUUID();

		//stored the ClientState object somewhere along with it's uniqueId
		
		return writeSuccessResponse();
	}

	boolean parseMessage(Message request) {
		myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());

		return true;
	}	

	Message writeSuccessResponse() {
		String xmlString = new String ("<?xml version='1.0' encoding='UTF-8'?><response ");
		xmlString = xmlString + "id='" + clientIdToServer + "' version='" + myVersion + "' ";
		xmlString = xmlString + "success='true'>";
		xmlString = xmlString + "<connectResponse id='" + serverIdForClient.toString() + "' />";
		xmlString = xmlString + "</response>";
		System.out.println(xmlString);
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
	
	Message writeFailureResponse() {
		return null;
	}
}
