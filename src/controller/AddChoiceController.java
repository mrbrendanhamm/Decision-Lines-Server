package controller;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;
import entity.DecisionLineEvent;
import entity.Model;

public class AddChoiceController implements IProtocolHandler {
	public AddChoiceController()
	{
		
	}
	
	public boolean addChoice(entity.Choice choice, String uniqueId)
	{
		DecisionLineEvent DLE = Model.getInstance().getDecisionLineEvent(uniqueId);
		if(DLE.canAddChoice())
		{
			DLE.getChoices().add(choice);
			return true;
		}
		return false;
	}

	@Override
	public synchronized Message process(ClientState state, Message request) {
		// TODO Auto-generated method stub
		return null;
	}
}
