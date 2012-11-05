package controller;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

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
		
		/* 
		 * check 1) can edge be added (height), 
		 * 2) can the user add the edge (RR or Asynch), 
		 * 3) is the DLE in a closed state?, 
		 * 4) is the left choice really to the left of the right
		 * some of these are done in the add edge function
		 * include some sort of a writeFailureMessage or similar functionality that automates the failure XML response
		 */
		if(dle.addEdge(edge) && leftChoice != null && rightChoice != null)
		{
			xmlString = new String(Message.responseHeader(request.id())
					+ "<addEdgeResponse id='" + eventID + "' left='" + left + "' right='" + right + "' /><height="
					+ "<id=" + eventID + "/><left=" + left + "/><right=" + right + "/><height="
					+ height + "/></response>");
		}else
		{
			xmlString = new String(Message.responseHeader(request.id(),"Vaild Edge")
					+ "<id=" + eventID + "/><left=" + left + "/><right=" + right + "/><height="
					+ height + "/></response>");
		}
		
		//TODO Needs to be broadcasted to all users of the dle
		
		//determine the next user's turn
		/*
		 * under round robin - broadcast out the edge and broadcast to the next user that it is their turn (turnResponse)
		 * under asynchronous - broadcast to everyone if no more turns remain for everyone, otherwise just return the 
		 * current turn status to the requesting user
		 */
		
		response = new Message(xmlString);
		
		//dle.getNumberOfChoice() * dle.getNumberOfEdge();
		//check to see if all edges have been played, if so then calculate the final order of choices
		if((dle.getUsers().size() * dle.getNumberOfEdge()) <= dle.getEdges().size())
		{
			dle.getFinalOrder();
			//write final order out to database
			DatabaseSubsystem.writeDecisionLineEvent(dle);
		}
		//Model.getInstance().getDecisionLineEvents().add(dle);
		
		//write out to database
		DatabaseSubsystem.writeEdge(edge, dle.getUniqueId());
		
		return response;
	}

}
