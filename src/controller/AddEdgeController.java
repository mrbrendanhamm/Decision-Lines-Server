package controller;

import org.w3c.dom.Node;


import boundary.DatabaseSubsystem;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.Edge;
import entity.Model;
import entity.User;
import server.*;
import xml.Message;

public class AddEdgeController implements IProtocolHandler
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
		String xmlString = "";
		Message response = null;
		// get Model instance
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
		// get leftChoice of the Edge
		int left = Integer.valueOf(child.getAttributes().getNamedItem("left")
				.getNodeValue());
		// get rightChoice of the Edge
		int right = Integer.valueOf(child.getAttributes().getNamedItem("right")
				.getNodeValue());
		// get height of the Edge
		int height = Integer.valueOf(child.getAttributes().getNamedItem("height")
				.getNodeValue());
		// get the left and right Choices from DLE
		Choice leftChoice = dle.getChoice(left);
		Choice rightChoice = dle.getChoice(right);
		// create the new Edge
		Edge edge = new Edge(leftChoice, rightChoice, height);

		/*
		 * check 1) whether the height of Edge is valid 2) can the user add the
		 * edge (RR or Asynch), 3) whether the DLE is in finished state 4)
		 * whether the left Choice is really to the left of the right Choice
		 */
		if (dle.getBehavior().equals(Behavior.ROUNDROBIN) && !dle.getCurrentTurn().equals(user))
		{
			// generate failure message since it is not the Turn of the User
			xmlString = new String(Message.responseHeader(request.id(),
					"It is not your Turn now")
					+ "<addEdgeResponse id='"
					+ eventID
					+ "' left='"
					+ left
					+ "' right='"
					+ right
					+ "' height='" + height + "'/></response>");
		}
		else 
		{
			int re = dle.addEdge(edge);
			if (re == 1)
			{
				// determine the next user's turn under RR
				if(dle.getBehavior().equals(Behavior.ROUNDROBIN))
				{
					int newTurn = (user.getPosition() + 1) % dle.getUsers().size();
					for(User userT: dle.getUsers())
					{
						if(userT.getPosition() == newTurn)
						{
							dle.setCurrentTurn(userT);
							break;
						}
					}
				}
				// generate success message
				xmlString = new String(Message.responseHeader(request.id())
						+ "<addEdgeResponse id='" + eventID + "' left='" + left
						+ "' right='" + right + "' height='" + height
						+ "'/></response>");
				// write out to database
				DatabaseSubsystem.writeEdge(edge, dle.getUniqueId());
			}
			else if (re == 2)
			{
				// generate failure message since the status of DLE is finished
				xmlString = new String(Message.responseHeader(request.id(),
						"The Event has been finished")
						+ "<addEdgeResponse id='"
						+ eventID
						+ "' left='"
						+ left
						+ "' right='"
						+ right
						+ "' height='" + height + "'/></response>");
			}
			else if (re == 3)
			{
				// generate failure message since the height is invalid
				xmlString = new String(Message.responseHeader(request.id(),
						"The Edge is too close to others")
						+ "<addEdgeResponse id='"
						+ eventID
						+ "' left='"
						+ left
						+ "' right='"
						+ right
						+ "' height='"
						+ height
						+ "'/></response>");
			}
			else if (re == 4)
			{
				// generate failure message since left Choice is not to the left of
				// the right Choice
				xmlString = new String(Message.responseHeader(request.id(),
						"Invalid Edge")
						+ "<addEdgeResponse id='"
						+ eventID
						+ "' left='"
						+ left
						+ "' right='"
						+ right
						+ "' height='"
						+ height
						+ "'/></response>");
			}
		}
		// TODO Needs to be broadcasted to all users of the dle
		/*
		 * under round robin - broadcast out the edge and broadcast to the next
		 * user that it is their turn (turnResponse) under asynchronous -
		 * broadcast to everyone if no more turns remain for everyone, otherwise
		 * just return the current turn status to the requesting user
		 */

		response = new Message(xmlString);


		// check to see if all edges have been played, if so then calculate the
		// final order of choices
		if ((dle.getNumberOfChoice() * dle.getNumberOfEdge()) <= dle.getEdges()
				.size())
		{
			dle.getFinalOrder();
			// write final order out to database
			DatabaseSubsystem.writeDecisionLineEvent(dle);
		}

		return response;
	}

}
