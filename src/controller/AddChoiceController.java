package controller;

import entity.DecisionLineEvent;
import entity.Model;

public class AddChoiceController {
	private Model myModel;
	public AddChoiceController()
	{
		
	}
	public AddChoiceController(Model myModel)
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
	public synchronized boolean addChoice(entity.Choice choice, String uniqueId)
	{
		DecisionLineEvent DLE = this.myModel.getDecisionLineEvent(uniqueId);
		if(DLE.canAddChoice())
		{
			DLE.getChoices().add(choice);
			return true;
		}
		return false;
	}
}
