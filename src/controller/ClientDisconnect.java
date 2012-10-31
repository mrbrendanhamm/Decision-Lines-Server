package controller;

import boundary.DatabaseSubsystem;
import entity.*;

public class ClientDisconnect {

	
	/**
	 * This method is the calling entry point for this controller.  It is assumed that the message type is appropriate
	 * for this controller.
	 * 
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	public synchronized boolean disconnectClient(String clientId) {
		Model myModel = Model.getInstance();
		
		for (int i = 0; i < myModel.getDecisionLineEvents().size(); ) {
			myModel.getDecisionLineEvents().get(i).disconnectClientId(clientId);
			
			if (myModel.getDecisionLineEvents().get(i).connectedClientCount() == 0) {
				//No clients are connected to this DLE.  Save it to DB and remove it from memory
				DatabaseSubsystem.writeDecisionLineEvent(myModel.getDecisionLineEvents().get(i));
				myModel.getDecisionLineEvents().remove(myModel.getDecisionLineEvents().get(i));
			}
			else
				i++;
		}

		return true;
	}
}
