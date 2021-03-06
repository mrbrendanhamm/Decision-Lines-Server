package controller;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import xml.Message;
import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;
import entity.User;
import entity.DecisionLineEvent.EventType;

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
		// get clientId of User -- not used
		//String clientId = new String(request.contents.getAttributes().getNamedItem("id").getNodeValue());
		// get ID of event and the DLE
		String eventID = new String(child.getAttributes().getNamedItem("id")
				.getNodeValue());
		DecisionLineEvent dle = model.getDecisionLineEvent(eventID);
		
		if (dle == null) {
			dle = DatabaseSubsystem.readDecisionLineEvent(eventID);
			if (dle == null) {
				Message newMsg = new Message(new String(Message.responseHeader(request.id(),
						"Decision Line Event does not exist")
						+ "<addChoiceResponse id='" + eventID + "' number='0' choice=''/></response>")); 
				System.out.println("Error: " + newMsg);
				return newMsg;
			}
			model.getDecisionLineEvents().add(dle);
		}
		
		// get User
		User user = dle.getUserFromClientId(state.id());
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
		 * order is a valid order number(no Choice with the same order exists 
		 * and the order number equal to User's position)
		 */
		if (user.getPosition() != order && user.getPosition() != 0)
		{
			Message newMsg = new Message(new String(Message.responseHeader(request.id(),
					"Cannot add Choice for other Users")
					+ "<addChoiceResponse id='"
					+ eventID
					+ "' number='"
					+ order
					+ "' choice='" + choiceString + "'/></response>"));
			// generate failure message since User's position is not equal to
			// Choice's order
			System.out.println("Error: " + newMsg);
			return  newMsg;
		}
		else if (dle.addChoice(choice))
		{
			// generate success message
			xmlString = new String(Message.responseHeader(request.id())
					+ "<addChoiceResponse id='" + eventID + "' number='" + order
					+ "' choice='" + choiceString + "'/></response>");

			// write to database.  If the state has changed then rewrite the whole thing, otherwise just write the choice
			if (dle.getEventType() == EventType.CLOSED)
				DatabaseSubsystem.writeDecisionLineEvent(dle);
			else
				DatabaseSubsystem.writeChoice(choice, dle.getUniqueId());
		}
		else
		{
			Message newMsg = new Message(new String(Message.responseHeader(request.id(),
					"Cannot add Choice anymore")
					+ "<addChoiceResponse id='"
					+ eventID
					+ "' number='"
					+ order
					+ "' choice='"
					+ choiceString + "'/></response>"));
			System.out.println("Error: " + newMsg);
			return newMsg;
		}

		System.out.print("Broadcast to all clients: " + xmlString);
		response = new Message(xmlString);
		// Broadcast to all connected clients except the originating client
		for(int i = 0; i < dle.getUsers().size(); i++) {
			String processing = dle.getUsers().get(i).getClientStateId();
			if (!processing.equals(state.id()) && !processing.equals("")) {
				Server.getState(processing).sendMessage(response);
			}
		}
		
		System.out.println(response);
		
		return response;
	}
}
