package controller;

import java.util.ArrayList;

import entity.Choice;
import entity.DecisionLineEvent;
import entity.Model;

public class DeleteChoiceController {
	private Model myModel;
	public DeleteChoiceController()
	{
		
	}
	public DeleteChoiceController(Model myModel)
	{
		this.myModel = myModel;
	}
	public void setMyModel(Model myModel)
	{
		this.myModel = myModel;
	}
	public Model getMyModel()
	{
		return this.myModel;
	}
	public synchronized boolean deleteChoice(entity.Choice choice, String uniqueId)
	{
		DecisionLineEvent DLE = this.myModel.getDecisionLineEvent(uniqueId);
		ArrayList<Choice> choices = DLE.getChoices();
		for(int i = 0; i < choices.size(); i++)
		{
			if(choices.get(i).equal(choice))
			{
				choices.remove(i);
				return true;
			}
		}
		return false;
	}
}
