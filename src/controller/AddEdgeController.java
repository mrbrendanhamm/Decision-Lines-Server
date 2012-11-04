package controller;

import org.w3c.dom.Node;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.Edge;
import entity.Model;
import server.*;
import xml.Message;

public class AddEdgeController implements IProtocolHandler {

	
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
		int left = new Integer(child.getAttributes().getNamedItem("left").getNodeValue());
		int right = new Integer(child.getAttributes().getNamedItem("right").getNodeValue());
		int height = new Integer(child.getAttributes().getNamedItem("height").getNodeValue());
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		Choice leftChoice = dle.getChoice(left);
		Choice rightChoice = dle.getChoice(right);
		Edge edge = new Edge(leftChoice,rightChoice,height);
		if(dle.addEdge(edge) && leftChoice != null && rightChoice != null)
		{
			xmlString = new String(Message.responseHeader(request.id())
					+ "<id=" + eventID + "/><left=" + left + "/><right=" + right + "/><height="
					+ height + "/></response>");
		}else
		{
			xmlString = new String(Message.responseHeader(request.id(),"Vaild Edge")
					+ "<id=" + eventID + "/><left=" + left + "/><right=" + right + "/><height="
					+ height + "/></response>");
		}
		
		//TODO Needs to be broadcasted to all users of the dle
		
		response = new Message(xmlString);
		if((dle.getUsers().size() * dle.getNumberOfEdge()) <= dle.getEdges().size())
		{
			dle.getFinalOrder();
		}
		Model.getInstance().getDecisionLineEvents().add(dle);
		return response;
	}

}
