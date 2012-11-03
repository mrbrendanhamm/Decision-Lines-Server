package controller;

import java.util.ArrayList;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.User;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import xml.Message;

public class CloseOpenDLEController implements IProtocolHandler{

	private DecisionLineEvent myDLE; //should only care about 1 dle
	private boolean isSuccess; //used to respond whether message was success or not
	private String reason; //used in case of failure null otherwise
	private String moderator; //in case we want to check the moderator sent the message
	private ArrayList<User> userList; //we need to broadcast to all users of dle
	private String xmlString; //string to construct response message
	/**
	 * This method is used to process a Close message from the moderator
	 * It is assumed right now that only the moderator can send this message
	 * so it does not check this.  If a DLE is open it sets it to closed and
	 * responds to all users in that event.  Otherwise it returns a false
	 * 'success' parameter.
	 * 	 * 
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request) {
		Model model = Model.getInstance(); //get the singleton model
		Node child = request.contents.getFirstChild(); //first child in message tree
		
		//get ID of Decision Line Event, access the DLE, and get a list of users.
		String dleID = child.getAttributes().getNamedItem("id").getNodeValue().toString();
		this.myDLE = model.getDecisionLineEvent(dleID);
		this.moderator = this.myDLE.getModerator();	
		this.userList = this.myDLE.getUsers();
		
		//Check if DLE is already closed
		if(this.myDLE.getEventType().equals(EventType.OPEN)){
			//set DLE to closed if open and fix success and reason
			this.myDLE.setType(EventType.CLOSED);
			isSuccess=true;
			reason="";
		}
		else if(this.myDLE.getEventType().equals(EventType.CLOSED)){
			//set success =false and reason for failure
			isSuccess=false;
			reason = "Event already closed";
		}
		else if(this.myDLE.getEventType().equals(EventType.FINISHED)){	
			//set success =false and reason for failure
			isSuccess=false;
			reason = "Event already finished";
		}
		//generate response
		//xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";	
		xmlString=	"<?xml version='1.0' encoding='UTF-8'?>"+
				"<response id='"+request.id()+"' version='1.0' success= '"+isSuccess+"'"+"reason='"+this.reason+"'>"+
				"<closeResponse>"+
				"</closeResponse>"+
				"</response>";
		System.out.println(xmlString);
		//convert string xmlString to a Message
		Message response = new Message(xmlString); 
		// broadcast to all connected clients except self
		for (User user: this.userList) {
			if (!user.getUser().equals(this.moderator)) {
				Server.getState(user.getClientStateId()).sendMessage(response);
			}
		}
		
			return response;
	
	}
	
}