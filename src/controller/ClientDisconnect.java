package controller;

import boundary.DatabaseSubsystem;
import entity.*;

public class ClientDisconnect {
	
	public boolean disconnectClient(String clientId) {
		DecisionLineEvent myDLE = null;
		Model myModel = Model.getInstance();
		
		for (DecisionLineEvent tempDLE : myModel.getDecisionLineEvents())
			if (tempDLE.getConnectedClients().contains(clientId))
				myDLE = tempDLE;

		if (myDLE == null)
			return true; //no match found, error as this should not be able to occur
		
		myDLE.getConnectedClients().remove(clientId);
		
		if (myDLE.getConnectedClients().isEmpty()) {
			DatabaseSubsystem.writeDecisionLineEvent(myDLE);
			myModel.getDecisionLineEvents().remove(myDLE);
		}

		return true;
	}
}
