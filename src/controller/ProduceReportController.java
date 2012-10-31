package controller;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class ProduceReportController implements IProtocolHandler {

	
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
		// TODO Auto-generated method stub
		return null;
	}

}
