package controller;

import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;
import server.*;
import xml.Message;

public class AddEdgeController implements IProtocolHandler {

	@Override
	public synchronized Message process(ClientState state, Message request) {
		String xmlString;
		Message response = null;
		Model model = Model.getInstance();
		Node child = request.contents.getChildNodes().item(1).getChildNodes().item(1);
		//get ID of event and the dle
		String eventID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		int left = new Integer(child.getAttributes().getNamedItem("left").getNodeValue());
		int right = new Integer(child.getAttributes().getNamedItem("right").getNodeValue());
		int height = new Integer(child.getAttributes().getNamedItem("height").getNodeValue());
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		if(dle.addEdge(edge));
		{
			xmlString = new String(Message.responseHeader(request.id())
					+ "<name=" + eventID + "/><number=" + order + "/><choice="
					+ eventID + "/></response>");
		}else
		{
			xmlString = new String(Message.responseHeader(request.id(),"Too many choices")
					+ "<name=" + eventID + "/><number=" + order + "/><choice="
					+ eventID + "/></response>");
		}
		
		//TODO Needs to be broadcasted to all users of the dle
		
		response = new Message(xmlString);
		return response;
	}

}
