package controller;

import java.util.ArrayList;

import shared.ClientState;
import shared.IProtocolHandler;
import xml.Message;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;

public class DeleteChoiceController implements IProtocolHandler {
	public DeleteChoiceController()
	{
		
	}

	public boolean deleteChoice(entity.Choice choice, String uniqueId)
	{
		DecisionLineEvent DLE = Model.getModel().getDecisionLineEvent(uniqueId);
		ArrayList<Choice> choices = DLE.getChoices();
		for(int i = 0; i < choices.size(); i++)
		{
			if(choices.get(i).equals(choice))
			{
				choices.remove(i);
				return true;
			}
		}
		return false;
	}
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// TODO Auto-generated method stub
		return null;
	}
}
