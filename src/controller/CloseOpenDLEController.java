package controller;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class CloseOpenDLEController implements IProtocolHandler{

	@Override
	public Message process(ClientState state, Message request) {
		Model model = Model.getInstance();
		Node child = request.contents.getFirstChild();
		
		//get ID of Decision Line Event
		String dleID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		
		DecisionLineEvent dle = model.getDecisionLineEvent(dleID);
				
		//set DLE 
		dle.setType(EventType.CLOSED);
				
		
		// TODO Auto-generated method stub
		return null;
	}

}
