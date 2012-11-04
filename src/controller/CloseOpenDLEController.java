package controller;

import java.util.ArrayList;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import entity.DecisionLineEvent;
import entity.User;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import xml.Message;

public class CloseOpenDLEController implements IProtocolHandler{

	DecisionLineEvent myDLE; //should only care about 1 dle
	boolean isSuccess; //used to respond whether message was success or not
	String reason; //used in case of failure null otherwise
	String moderator; //in case we want to check the moderator sent the message
	ArrayList<User> userList; //we need to broadcast to all users of dle
	String xmlString; //string to construct response message
	
	public CloseOpenDLEController(){
		
	}
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
		myDLE = model.getDecisionLineEvent(dleID);
		moderator = myDLE.getModerator();	
		userList = myDLE.getUsers();
		
		//Check if DLE is already closed
		if(myDLE.getEventType().equals(EventType.OPEN)){
			//set DLE to closed if open and fix success and reason
			myDLE.setType(EventType.CLOSED);
			DatabaseSubsystem.writeDecisionLineEvent(myDLE); //update the database
			isSuccess=true;
			reason="";
		}
		else if(myDLE.getEventType().equals(EventType.CLOSED)){
			//set success =false and reason for failure
			isSuccess=false;
			reason = "Event already closed";
		}
		else if(myDLE.getEventType().equals(EventType.FINISHED)){	
			//set success =false and reason for failure
			isSuccess=false;
			reason = "Event already finished";
		}
		//generate response
		//xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";	
		
		if (isSuccess) { // you have an error here, the reason string can only be used during failures
			xmlString=	"<?xml version='1.0' encoding='UTF-8'?>"+
					"<response id='"+request.id()+"' version='1.0' success='true'>"+ 
					"<closeResponse/>"+
					"</response>";
		}
		else {
			xmlString=	"<?xml version='1.0' encoding='UTF-8'?>"+
					"<response id='"+request.id()+"' version='1.0' success='false' reason='"+this.reason+"'>"+ 
					"<closeResponse/>"+
					"</response>";
		}
		
		System.out.println(xmlString);
		//convert string xmlString to a Message
		Message response = new Message(xmlString); 
		// broadcast to all connected clients except self
		for (User user: userList) {
			if (!user.getUser().equals(moderator)) {
				Server.getState(user.getClientStateId()).sendMessage(response);
			}
		}
		
			return response;
	
	}
	
}