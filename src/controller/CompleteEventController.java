package controller;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class CompleteEventController implements IProtocolHandler{

	public CompleteEventController(){
		
	}
	
	/** CompleteEventController.process(state, request) calls for a final decision
	 * to be made in a dle? Not sure if this should call for it.  At the least it
	 * should be returning the ordered list of the decisions (requires that 
	 * DecisionLineEvent entity is change to reflect this
	 */
	
	//My idea for now is that it should return an array of integers corresponding to
	//the Choices such that array[0]=first priority decision ...array[n]=(n+1) priority
	//TODO Everything with this controller
	@Override
	public Message process(ClientState state, Message request) {
		// TODO Auto-generated method stub
		Message response=null;
		
		return response;
	}

}
