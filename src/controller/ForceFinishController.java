package controller;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import entity.DecisionLineEvent;
import entity.Model;
import entity.DecisionLineEvent.EventType;
import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class ForceFinishController implements IProtocolHandler
{

	/**
	 * This method is the calling entry point for this controller. It is assumed
	 * that the message type is appropriate for this controller.
	 * 
	 * @param state
	 *            - The ClientState of the requesting client
	 * @param request
	 *            - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request)
	{
		String xmlString;
		Model model = Model.getInstance();
		Message response = null;

		Node child = request.contents.getFirstChild();
		String myKey = child.getAttributes().getNamedItem("key").getNodeValue();

		// check the key and if it is wrong we need a failure
		if (!model.checkKey(myKey))
		{
			xmlString = new String(Message.responseHeader(request.id(), "Invalid key")
					+ "numberAffected=0/></response>");
		}
		else if (child.getAttributes().getNamedItem("id") != null)
		{
			// get ID of event and the DLE
			String eventID = new String(child.getAttributes()
					.getNamedItem("id").getNodeValue());
			DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
			// validate that this DLE exists
			if (dle != null)
			{
				// finish the DLE
				dle.setType(EventType.FINISHED);
				dle.getFinalOrder();
				// write to database
				DatabaseSubsystem.writeDecisionLineEvent(dle);
				// generate the success message
				xmlString = new String(Message.responseHeader(request.id())
						+ "numberAffected=1/></response>");
			}else
			{
				xmlString = new String(Message.responseHeader(request.id(), "Invalid Event Id")
						+ "numberAffected=0/></response>");
			}

			// TODO Needs to be broadcasted to all users of the dle
		}
		else
		{
			int daysOld = new Integer(child.getAttributes()
					.getNamedItem("daysOld").getNodeValue());

			// step 1) iterate through each DLE in memory
			// 1a) is it older than daysOld, then finish the DLE
			// 1b) notify any connected clients that the dle has been finished
			// 1c) write DLE to database
			// 2) run the function DatabaseSubsystem.finishDLEBasedOnDate() to
			// finish any DLEs not in memory

			int count = 0;
			/*
			 * The count should be the number of DLEs that were closed and not
			 * the number of connected clients
			 */
			xmlString = new String(Message.responseHeader(request.id())
					+ "<numberAffected=" + count + "/></response>");
		}

		response = new Message(xmlString);
		return response;

	}
}
