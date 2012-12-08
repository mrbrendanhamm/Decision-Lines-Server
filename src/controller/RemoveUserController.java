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
	int excessEdges;
	User currentTurn;
	int kickUserPos; //stores the position in userList array of the user2Kick
	boolean wasKickUserTurn;
	User newTurn;
	
	RemoveUserController(){

	}
	


	@Override
	/** 
	 * This method takes a kickRequest, removes the user from the dle
	 * if it is Round Robin and the user exists.  The success return
	 * message is broadcasted to all users of the event.
	 * Returns a failure response only to the moderator in the event
	 * type is Asynchronous or if user is not in the specified DLE.
	 */
	public Message process(ClientState state, Message request) {
		String xmlString=null;
		//print the request
		System.out.println("Request:"+request);
		
		//get the model singleton
		myModel = Model.getInstance();
		
		wasKickUserTurn=false;
		
		//Access the message tree
		Node child = request.contents.getFirstChild();
		clientId = request.contents.getAttributes().getNamedItem("id").getNodeValue();
		String dleString = child.getAttributes().getNamedItem("id").getNodeValue();
		String user2Kick = child.getAttributes().getNamedItem("user").getNodeValue();
		
		//Get the DLE from model
		dleID = myModel.getDecisionLineEvent(dleString);
		userList = dleID.getUsers();
		if(dleID.getEventType().equals(EventType.CLOSED)){
			currentTurn = dleID.getCurrentTurn();
		}
		else currentTurn=null;

		
		
		//Check if DLE is Round Robin. Proceed if it is, generate failure otherwise
		if (dleID.getBehavior().equals(Behavior.ROUNDROBIN)){
			//is it the user2Kicks turn?
			if (!(currentTurn==null) && currentTurn.getUser().equals(user2Kick) ){
				wasKickUserTurn = true;
			}
			else wasKickUserTurn=false;
			
			xmlString = kickUser(user2Kick);
			
			// Update the turn if DLE is not completed and it was kicked user's turn
			// and the user was successfully kicked
			if (isCompleted == false  && wasKickUserTurn && success==true){
				newTurn = userList.get(kickUserPos);
				dleID.setCurrentTurn(newTurn);	
				sendTurnResponse(newTurn);
			}

			
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

private void sendTurnResponse(User newTurn) {
		Message turnResponse = null;
		String localClientID = newTurn.getClientStateId();
		String stringTurnResponse = Message.responseHeader(localClientID) + 
				"<turnResponse completed ='false'/></response>";
		turnResponse = new Message(stringTurnResponse);
		Server.getState(localClientID).sendMessage(turnResponse);
		System.out.println("Turn Response: " + turnResponse);
		
	}



/**
 * This method will access the dle to kick the specified user
 * and update the number of edges for all other users on a success
 * @param user2Kick
 * @return
 */
	private String kickUser(String user2Kick) {
		String retVal;
		int j=0; // used to find the position of kicked user
		//Check the userList for the one to kick and kick if exists
		for (User user: userList){
			if (user.getUser().equals(user2Kick)){
				excessEdges=user.getEdgesRemaining();
				kickUserPos=j; 
				success=dleID.removeUser(user);
				break;
			}
			j++;
		}
		
		isCompleted = checkCompleted();
		
		//Generate strings corresponding to success/failure and update edges
		// remaining for other players
		if (success==true){
			retVal = createSucessString();
			//Update the userList now that user has been kicked
			userList = dleID.getUsers();
			// determine the number to add per user
			int numberToAdd = excessEdges/userList.size();
			//determine the remainder of turns
			int modularEdges = excessEdges % userList.size();
			//call increment for each user numberToAdd times.
			for (User user: userList){
				for (int i=0; i<numberToAdd; i++){
					user.incrementEdgesRemaining();
				}
			}
			// increment only the first modularEdges users to attribute the leftover
			for (int i=0; i<modularEdges; i++){
				userList.get(i).incrementEdgesRemaining();
			}
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


/** This method creates a failure response with the reason provided it
 * 
 * @param reason
 * @return
 */
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
