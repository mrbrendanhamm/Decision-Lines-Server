package controller;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.Model;
import entity.DecisionLineEvent.EventType;
import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class ForceFinishController implements IProtocolHandler {

	
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
		Model model = Model.getInstance();
		Message response = null;
		
		Node child = request.contents.getChildNodes().item(1).getChildNodes().item(1);
		
		// if the attribute Key is present, then this call originates from an Administrator
		//otherwise, this is originating from a Moderator and is for one specific event
		
		//get ID of event and the dle
		if(child.getAttributes().getNamedItem("name").getNodeValue() != null)
		{
			String eventID = new String(child.getAttributes()
					.getNamedItem("name").getNodeValue());
			//validate that this DLE exists
			DecisionLineEvent dle = model.getDecisionLineEvent(eventID);

			// finish the dle
			dle.setType(EventType.FINISHED);
			dle.getFinalOrder();
			int count = 0; 
				/*  
				 *  The count should be the number of DLEs that were closed and not the number of connected clients 
				 */
			
			// write to database

			xmlString = new String(Message.responseHeader(request.id())
					+ "<numberAffected=" + count + "/></response>");
			

			//TODO Needs to be broadcasted to all users of the dle
		}else
		{
			int daysOld = new Integer(child.getAttributes()
					.getNamedItem("daysOld").getNodeValue());
			
			// step 1) iterate through each DLE in memory
			//		1a) is it older than daysOld, then finish the DLE
			//		1b) notify any connected clients that the dle has been finished
			//		1c) write DLE to database
			//		2) run the function DatabaseSubsystem.finishDLEBasedOnDate() to finish any DLEs not in memory

			int count = 0; 
			/*  
			 *The count should be the number of DLEs that were closed and not the number of connected clients 
			 */
			xmlString = new String(Message.responseHeader(request.id())
					+ "<numberAffected=" + count + "/></response>");
		}
		
		
		response = new Message(xmlString);
		return response;
		
	}

}
