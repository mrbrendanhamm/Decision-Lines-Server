package controller;

import org.w3c.dom.Node;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;
import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;

public class AddChoiceController implements IProtocolHandler {
	public AddChoiceController()
	{
		
	}
	
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
		String xmlString;
		Message response = null;
		Model model = Model.getInstance();
		Node child = request.contents.getChildNodes().item(1).getChildNodes().item(1);
		
		//get ID of event and the dle
		String eventID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		String choiceString = new String(child.getAttributes().getNamedItem("choice").getNodeValue());
		int order = new Integer(child.getAttributes().getNamedItem("number").getNodeValue());
		Choice choice = new Choice(choiceString,order);
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		
		/*
		 * validate that the DLE is open
		 * that the order is a valid order number (the position of the choice is the same as the position of the user)
		 * maybe use an easier error message construction method here
		 */
		if(dle.addChoice(choice))
		{
			xmlString = new String(Message.responseHeader(request.id())
					+ "<name=" + eventID + "/><number=" + order + "/><choice="
					+ choiceString + "/></response>");
		}else
		{
			xmlString = new String(Message.responseHeader(request.id(),"Too many choices")
					+ "<name=" + eventID + "/><number=" + order + "/><choice="
					+ choiceString + "/></response>");
		}
		
		//TODO Needs to be broadcasted to all users of the dle
		// how do we determine when all choices have been created?  what is the mechanism (turnResponse?)?
		// should this dle be converted to closed?
		
		//write to database
		
		response = new Message(xmlString);
		return response;
	}
}
