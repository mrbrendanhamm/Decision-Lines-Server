package controller;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;
import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;

public class AddChoiceController implements IProtocolHandler
{

	/**
	 * Default constructor
	 */
	public AddChoiceController()
	{

	}

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
		Message response = null;
		// Get Model instance
		Model model = Model.getInstance();
		Node child = request.contents.getFirstChild();
		// get clientId of User
		String clientId = new String(request.contents.getAttributes()
				.getNamedItem("id").getNodeValue());
		// get ID of event and the DLE
		String eventID = new String(child.getAttributes().getNamedItem("id")
				.getNodeValue());
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		// get User
		User user = dle.getUserFromClientId(clientId);
		// get Choice of event
		String choiceString = new String(child.getAttributes()
				.getNamedItem("choice").getNodeValue());
		// get Order of Choice
		int order = Integer.valueOf(child.getAttributes().getNamedItem("number")
				.getNodeValue());
		// create a Choice
		Choice choice = new Choice(choiceString, order);

		/*
		 * First check whether User's position is equal to Choice's order Then
		 * addChoice() method checks 1) whether the DLE is open 2) whether the
		 * order is a valid order number(no Choice with the same order exists)
		 */
		if (user.getPosition() != order)
		{
			// generate failure message since User's position is not equal to
			// Choice's order
			xmlString = new String(Message.responseHeader(request.id(),
					"Cannot add Choice for other Users")
					+ "<addChoiceResponse id='"
					+ eventID
					+ "' number="
					+ order
					+ " choice='" + choiceString + "'/></response>");
		}
		else if (dle.addChoice(choice))
		{
			// generate success message
			xmlString = new String(Message.responseHeader(request.id())
					+ "<addChoiceResponse id='" + eventID + "' number=" + order
					+ " choice='" + choiceString + "'/></response>");
		}
		else
		{
			// generate failure message
			xmlString = new String(Message.responseHeader(request.id(),
					"Cannot add Choice anymore")
					+ "<addChoiceResponse id='"
					+ eventID
					+ "' number="
					+ order
					+ " choice='"
					+ choiceString + "'/></response>");
		}

		// TODO Needs to be broadcasted to all users of the DLE
		// how do we determine when all choices have been created? what is the
		// mechanism (turnResponse?)?
		// should this dle be converted to closed? If so, how do we notify users
		// of this? How do we notify users of the current turn?

		// write to database
		DatabaseSubsystem.writeChoice(choice, dle.getUniqueId());

		response = new Message(xmlString);
		return response;
	}
}
