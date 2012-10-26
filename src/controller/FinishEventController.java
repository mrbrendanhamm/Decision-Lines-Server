package controller;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class FinishEventController implements IProtocolHandler{

	
	public FinishEventController(){

	}
	
	/** Takes the message and gets the corresponding dle.  Sets the DLE type
	 *  to FINISHED.  Response is broadcasted in the form of number of affected users
	 */
	@Override
	public Message process(ClientState state, Message request) {
		String xmlString;
		Model model = Model.getInstance();
		Message response = null;
		
		Node child = request.contents.getChildNodes().item(1).getChildNodes().item(1);
		//get ID of event and the dle
		String eventID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		
		//finish the dle
		dle.setType(EventType.FINISHED);
		
		int count = model.getDecisionLineEvent(eventID).getConnectedClients().size();
		xmlString = new String(Message.responseHeader(request.id()) + "<numberAffected=" + count + 
				"/></response>");
		
		//TODO Needs to be broadcasted to all users of the dle
		
		
		response = new Message(xmlString);
		return response;
	}
	
	
}
