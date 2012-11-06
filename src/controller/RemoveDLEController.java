package controller;


import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import entity.DecisionLineEvent;
import entity.DecisionLineEvent.EventType;
import entity.Model;
import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class RemoveDLEController implements IProtocolHandler {
	Model myModel;
	DecisionLineEvent dle;
	String myKey;
	String reason;
	boolean isSuccess;
	ArrayList<DecisionLineEvent> dles;
    int numberRemoved=0;
    String xmlString;
    ArrayList<DecisionLineEvent> deleteList = null;
	/** constructor for RemoveDLEController
	 * 
	 */
	public RemoveDLEController(){
		
	}
	/**
	 * This method is the calling entry point for this controller.  It is assumed that the message type is appropriate
	 * for this controller.
	 * This controller checks whether the key is the admin key stored in Model
	 * If not it returns failure, if it does it continues
	 * Next the controller checks whether 'id' exists as an attribute
	 * if so it deletes that dle.
	 * if not it gets the completed/uncompleted and days old field and
	 * deletes these.
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request) {
		System.out.println("Request:"+request); //print request to line
		myModel=Model.getInstance(); //Get the singleton
		
		//Access the message tree
		Node child = request.contents.getFirstChild();
		myKey=child.getAttributes().getNamedItem("key").getNodeValue();
		
		//check the key and if it is wrong we need a failure
		if (myModel.checkKey(myKey)!=true){
			//need to return reason for failure	
			isSuccess=false;
			reason = "Invalid Key";
		}
		//if it matches the model key we can proceed to delete dle
		else if(myModel.checkKey(myKey)==true){
			//check id attribute and remove one or several dles
			//this requires checking if the first node name is "id" or "completed"
			String firstNodeName = child.getAttributes().item(0).getNodeName();
			System.out.println(firstNodeName);
			if(firstNodeName.equals("id")){
				//remove the one dle by id
				String dleID = child.getAttributes().getNamedItem("id").getNodeValue();
				dle=myModel.getDecisionLineEvent(dleID);
				if(!(myModel.removeDecisionLineEvent(dle)));{} //removeDLE from model

				//delete from database
				numberRemoved = DatabaseSubsystem.deleteEventById(dleID);
				isSuccess=true;
				reason="";
			}
			else {
				//remove dles which correspond to isCompleted and dayOld from database
				boolean isCompleted = Boolean.valueOf(child.getAttributes().getNamedItem("completed").getNodeValue());
				System.out.println(isCompleted);
				int daysOld = Integer.valueOf(child.getAttributes().getNamedItem("daysOld").getNodeValue());
				System.out.println("DaysOld:"+daysOld);
				java.util.Date currentDate = new java.util.Date();
				java.util.Date deleteByDate = new java.util.Date(currentDate.getTime() - 1000*3600*24*daysOld);
				numberRemoved=DatabaseSubsystem.deleteEventsByAge(deleteByDate, isCompleted);
				isSuccess=true;
				reason="";
				
				//remove dles which correspond to isCompleted from Model
				ArrayList<DecisionLineEvent> dleList = myModel.getDecisionLineEvents();
				//cycle through dles in model
					for (DecisionLineEvent DLE: dleList){
						EventType type = DLE.getEventType();
						Date dleDate = DLE.getDate();
						// is the dle older than deleteByDate if so add to the deleteList
						if(dleDate.before(deleteByDate)){
							if((type.equals(EventType.OPEN) || type.equals(EventType.CLOSED)) && isCompleted==false){
								deleteList.add(DLE);
							}
							else if(type.equals(EventType.CLOSED) && isCompleted==true){
								deleteList.add(DLE);
							}
						}
						
					}
					for (DecisionLineEvent DLE : deleteList){
						myModel.removeDecisionLineEvent(DLE);
					}
					
				}
		}
		
		if (isSuccess) 
			xmlString = Message.responseHeader(state.id()) + 
			"<removeResponse numberAffected='"+numberRemoved+"'/></response>";
		else
			xmlString = Message.responseHeader(state.id(), reason) +
			 "<removeResponse numberAffected='"+numberRemoved+"'/></response>";

		Message response = new Message(xmlString);
		System.out.println("Response:"+response);
		return response;
	}

}
