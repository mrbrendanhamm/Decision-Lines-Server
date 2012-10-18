package controller;

import java.util.UUID;

import shared.ClientState;
import shared.IProtocolHandler;
import xml.Message;

public class ConnectToDLEController implements IProtocolHandler {
	String clientIdToServer;
	String myVersion;
	String serverIdForClient;
	
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
		
		serverIdForClient = UUID.randomUUID().toString();

		//stored the ClientState object somewhere along with it's uniqueId
		
		return writeSuccessResponse();
	}

	boolean parseMessage(Message request) {
		myVersion = new String(request.contents.getAttributes().getNamedItem("version").getNodeValue());
		clientIdToServer = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());

		return true;
	}	

	Message writeSuccessResponse() {
		String xmlString = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<response id='" + clientIdToServer + "' version='" + myVersion + "' success='true'>" +
				"  <connectResponse id='" + serverIdForClient + "'/>" +
				"</response>";
		System.out.println(xmlString);
		Message myMsg = new Message(xmlString);
		
		return myMsg;
	}
	
	Message writeFailureResponse(String reason) {
		return null;
	}
}
