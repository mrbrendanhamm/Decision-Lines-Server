package controller;

import boundary.DatabaseSubsystem;
import entity.*;

/**
 * This class handles the situation when a client terminates the connection with the server.  This class handles
 * all the required cleanup when a client disconnects.
 */
public class ClientDisconnect {
	/**
	 * This method is the calling entry point for this controller.    
	 *
	 * @param clientId - the id from the ClientState object that corresponds to the disconnecting client.
	 * @return always true.
	 */
	public synchronized boolean disconnectClient(String clientId) {
		Model myModel = Model.getInstance();
		
		for (int i = 0; i < myModel.getDecisionLineEvents().size(); ) {
			//unregister the client from the decision line event
			myModel.getDecisionLineEvents().get(i).disconnectClientId(clientId);
			
			//TODO do I need to notify other clients that this guy has disconnected?
			
			//verify that at least one client is still connected
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
