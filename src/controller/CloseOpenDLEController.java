package controller;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import xml.Message;

public class CloseOpenDLEController implements IProtocolHandler{

	@Override
	public synchronized Message process(ClientState state, Message request) {
		String xmlString;
		
		Model model = Model.getInstance();
		Node child = request.contents.getFirstChild();
		
		//get ID of Decision Line Event
		String dleID = new String(child.getAttributes().getNamedItem("id").getNodeValue());
		
		DecisionLineEvent dle = model.getDecisionLineEvent(dleID);
		//Check if DLE is already closed
		if(dle.getEventType()!=EventType.CLOSED){
			//set DLE to closed
			dle.setType(EventType.CLOSED);
		}
		//generate response
		xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";	
		Message response = new Message(xmlString); 
		
		// broadcast to all connected clients except self
		for (String id : Server.ids()) {
			if (!id.equals(state.id())) {
				Server.getState(id).sendMessage(response);
			}
		}
		
			return response;
	
	}

}
