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
		//get ID of event and the dle
		if(child.getAttributes().getNamedItem("name").getNodeValue() != null)
		{
			String eventID = new String(child.getAttributes()
					.getNamedItem("name").getNodeValue());
			DecisionLineEvent dle = model.getDecisionLineEvent(eventID);

			// finish the dle
			dle.setType(EventType.FINISHED);
			dle.getFinalOrder();
			int count = 0; 
				/*  
				 * model.getDecisionLineEvent(eventID).getConnectedClients().size();.  
				 * Commented this guy out.  The count should be the number of DLEs that were closed and not the number of connected clients 
				 */
			xmlString = new String(Message.responseHeader(request.id())
					+ "<numberAffected=" + count + "/></response>");
		}else
		{
			int daysOld = new Integer(child.getAttributes()
					.getNamedItem("daysOld").getNodeValue());

			int count = 0; 
			/*  
			 * model.getDecisionLineEvent(eventID).getConnectedClients().size();.  
			 * Commented this guy out.  The count should be the number of DLEs that were closed and not the number of connected clients 
			 */
			xmlString = new String(Message.responseHeader(request.id())
					+ "<numberAffected=" + count + "/></response>");
		}
		//TODO Needs to be broadcasted to all users of the dle
		
		
		response = new Message(xmlString);
		return response;
		
	}

}
