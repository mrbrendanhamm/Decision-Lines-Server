package entity;

import java.util.ArrayList;

public class Model {
	private ArrayList<DecisionLineEvent> decisionLineEvents;
	private static Model thisModel = null;
	
	Model() {
		this.decisionLineEvents = new ArrayList<DecisionLineEvent>();
	}

	// Changed access method to a singleton so controllers can always directly reference the model
	public static Model getInstance() {
		if (thisModel == null) {
			thisModel = new Model();
		}
		
		return thisModel;
	}
	
	public void setDecisionLineEvents(DecisionLineEvent DLE)
	{
		this.decisionLineEvents.add(DLE);
	}
	public ArrayList<DecisionLineEvent> getDecisionLineEvents()
	{
		return this.decisionLineEvents;
	}
	public DecisionLineEvent getDecisionLineEvent(String uniqueId)
	{
		for(DecisionLineEvent DLE : this.decisionLineEvents)
		{
			if(DLE.getUniqueId().equals(uniqueId))
			{
				return DLE;
			}
		}
		return null;
	}
}
