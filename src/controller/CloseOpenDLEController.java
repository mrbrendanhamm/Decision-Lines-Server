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
	Message response;
	
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
		System.out.println("Request:"+request);
		
		Model model = Model.getInstance(); //get the singleton model
		Node child = request.contents.getFirstChild(); //first child in message tree
		
		//get ID of Decision Line Event, access the DLE, and get a list of users.
		String dleID = child.getAttributes().getNamedItem("id").getNodeValue().toString();
		
		//check that the DLE is currently loaded in memory, aka is this a valid identifier?
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
		
		// broadcast to all connected clients except self
		for (User user: userList) {
			if (!user.getUser().equals(moderator)) {
				if(isSuccess) response = GenerateSuccessMessage(user.getClientStateId());
				else response = GenerateFailureMessage(user.getClientStateId());
				Server.getState(user.getClientStateId()).sendMessage(response);
				System.out.println("Response:"+response);
			}
		}
		// <turnResponse> needs to be generated and sent to various clients depending on Round Robin/Asynchronous 
		// could be an opportunity to have the turnResponse creation be controlled by a devoted controller or within the DLE instance
		if(isSuccess) response = GenerateSuccessMessage(request.id());
		else response = GenerateFailureMessage(request.id());
		
		System.out.println("Response:"+response);
			return response;
	
	}
	
	/** This method generates a success response for the given clientStateID
	 * 
	 * @param xmlEnd
	 * @return
	 */
	public Message GenerateSuccessMessage(String ID){
		String xmlString="<response id='"+ID+"' version='1.0' success='true'>"+ 
		"<closeResponse/>"+
		"</response>";
		Message retVal = new Message(xmlString);
		return(retVal);
	}
	
	/** This method generates a failure response for the given clientStateID
	 * 
	 * @param xmlEnd
	 * @return
	 */
	public Message GenerateFailureMessage(String ID){
		String xmlString="<response id='"+ID+"' version='1.0' success='false' reason='"+reason+"'>"+ 
				"<closeResponse/>"+
				"</response>";
		Message retVal = new Message(xmlString);
		return(retVal);
	}
	
}