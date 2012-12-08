package controller;

import java.util.ArrayList;
import java.util.Date;

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
			xmlString = new String(Message.responseHeader(request.id(),
					"Invalid key") + "<forceResponse numberAffected='0'/></response>");
		}
		else if (child.getAttributes().getNamedItem("id") != null)
		{
			// get ID of event and the DLE
			String eventID = new String(child.getAttributes()
					.getNamedItem("id").getNodeValue());
			DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
			if (dle==null){
				dle = DatabaseSubsystem.readDecisionLineEvent(eventID);
				if (dle!=null){
					model.getDecisionLineEvents().add(dle);
				}
			}
			
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
						+ "<forceResponse numberAffected='1'/></response>");
			}
			else
			{
				xmlString = new String(Message.responseHeader(request.id(),
						"Invalid Event Id") + "<forceResponse numberAffected='0'/></response>");
			}

			// TODO Needs to be broadcasted to all users of the dle
		}
		else
		{
			int daysOld = new Integer(child.getAttributes()
					.getNamedItem("daysOld").getNodeValue());
			int count = 0;
			Date currentDate = new java.util.Date();
			Date deleteByDate = new java.util.Date(
					currentDate.getTime() - 1000 * 3600 * 24 * daysOld);

			// get the DLE list from Model
			ArrayList<DecisionLineEvent> dles = model.getDecisionLineEvents();
			// iterate through each DLE in memory
			for (DecisionLineEvent dle : dles)
			{
				// check whether it is older than daysOld
				if (dle.getDate().before(deleteByDate))
				{
					// check whether it has been finished
					if (!dle.getEventType().equals(EventType.FINISHED))
					{
						// finish the DLE
						dle.setType(EventType.FINISHED);
						dle.getFinalOrder();
						// write to database
						DatabaseSubsystem.writeDecisionLineEvent(dle);
						count++;
					}
				}

			}
			// finish any DLEs not in memory
			DatabaseSubsystem.finishDLEBasedOnDate(deleteByDate);
			xmlString = new String(Message.responseHeader(request.id())
					+ "<forceResponse numberAffected='" + count + "'/></response>");
		}

		response = new Message(xmlString);
		return response;

	}
}
