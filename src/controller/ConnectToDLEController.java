package controller;

import shared.ClientState;
import shared.IProtocolHandler;
import xml.Message;

public class ConnectToDLEController implements IProtocolHandler {
	ConnectToDLEController() {
	}

	@Override
	public Message process(ClientState state, Message request) {
		// Initialize local variables
		Message response = null;
		String eventId = new String();
		
		// Read in the Request
		
		// Verify that the Request properly formatted and all required inputs are present 
		// (might not need to be done because of the xsd schematic)
		
		// parse out Message for input parameters
		
		// Execute necessary Controller functionality
		/*
			eventId = ....
			DecisionLineEvent myDLE = DatabaseSubsystem.readDecisionLineEvent(eventId);
			response = XMLProcotolLayer.writeDLEMessage(myDLE);
		 */
		
		// Write message Response
		return response;
	}
}
