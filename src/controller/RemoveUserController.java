package controller;

import java.util.ArrayList;

import org.w3c.dom.Node;

import entity.DecisionLineEvent;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import entity.User;
import server.ClientState;
import server.IProtocolHandler;
import server.Server;
import xml.Message;

public class RemoveUserController implements IProtocolHandler{
	Model myModel;
	ArrayList<User> userList = new ArrayList<User>();
	Message response;
	String reason;
	DecisionLineEvent dleID;
	Boolean success = false;
	Boolean isCompleted;
	String clientId;
	Message xmlMessage;
	
	RemoveUserController(){

	}
	


	@Override
	public Message process(ClientState state, Message request) {
		String xmlString=null;
		//print the request
		System.out.println("Request:"+request);
		
		//get the model singleton
		myModel = Model.getInstance();
		

		
		//Access the message tree
		Node child = request.contents.getFirstChild();
		clientId = request.contents.getAttributes().getNamedItem("id").getNodeValue();
		String dleString = child.getAttributes().getNamedItem("id").getNodeValue();
		String user2Kick = child.getAttributes().getNamedItem("user").getNodeValue();
		
		//Get the DLE from model
		dleID = myModel.getDecisionLineEvent(dleString);
		userList = dleID.getUsers();
		
		
		//Check if DLE is Round Robin. Proceed if it is, generate failure otherwise
		if (dleID.getBehavior().equals(Behavior.ROUNDROBIN)){
			xmlString = kickUser(user2Kick);
		}
		else {
			reason = "Event is not RoundRobin";
			xmlString = createFailureString(reason);
		}
		
		
		//Broadcast response if success
		if (success==true){
			for(int i = 0; i < dleID.getUsers().size(); i++) {
				String localClientId = dleID.getUsers().get(i).getClientStateId();

				if (!localClientId.equals(clientId) && !localClientId.equals("")) {
					String xmlStringBroadcast = Message.responseHeader(localClientId) + xmlString;
					xmlMessage = new Message(xmlStringBroadcast);
					Server.getState(localClientId).sendMessage(xmlMessage);
					System.out.println("Broadcast: " + xmlMessage);
				}
			}
			
			xmlString = Message.responseHeader(clientId)+xmlString;
			response = new Message(xmlString);
		}
		else {
			response = new Message(xmlString);
		}
		
		
		System.out.println("Response:"+response);
		return response;
	}

/**
 * This method will access the dle to kick the specified user
 * @param user2Kick
 * @return
 */
	private String kickUser(String user2Kick) {
		String retVal;
		//Check the userList for the one to kick and kick if exists
		for (User user: userList){
			if (user.getUser().equals(user2Kick)){
				success=dleID.removeUser(user);
				break;
			}
		}
		
		isCompleted = checkCompleted();
		
		//Generate strings corresponding to success/failure
		if (success==true){
			retVal = createSucessString();
		}
		else {
			reason = "User is not in User List";
			retVal = createFailureString(reason);
		}
		
		
		return retVal;
	}

/**This method will check if the event has been finished
 * 
 * @return
 */
	private Boolean checkCompleted() {
		Boolean retVal = false;
		
		if (dleID.getEventType().equals(EventType.FINISHED))
			retVal = true;
		
		return retVal;
	}



	private String createFailureString(String reason) {
		String retVal = Message.responseHeader(clientId,reason)+"<kickResponse completed='false'/></response>";
		
		return retVal;
	}



	private String createSucessString() {
		// TODO Auto-generated method stub
		String retVal="<kickResponse completed='"+isCompleted.toString()+"'/></response>";
		
		return retVal;
	}
	
}
