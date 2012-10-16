package entity;

import java.util.ArrayList;

public class Model {
	private ArrayList<DecisionLineEvent> decisionLineEvents;
	
	public Model()
	{
		this.decisionLineEvents = new ArrayList<DecisionLineEvent>();
	}
	public void setDecisionLineEvents(DecisionLineEvent DLE)
	{
		this.decisionLineEvents.add(DLE);
	}
	public ArrayList<DecisionLineEvent> getDecisionLineEvents()
	{
		return this.decisionLineEvents;
	}
}
