package entity;

import java.util.ArrayList;
import java.util.UUID;

import boundary.DatabaseSubsystem;

public class Model {
	private ArrayList<DecisionLineEvent> decisionLineEvents = null;
	static Model thisModel = null;
	final String key = UUID.randomUUID().toString(); 
	
	Model() {
	}

	public static Model getInstance() {
		if (thisModel == null) {
			thisModel = new Model();
		}
		
		return thisModel;
	}
	public ArrayList<DecisionLineEvent> getDecisionLineEvents()
	{
		if (decisionLineEvents == null)
			decisionLineEvents = new ArrayList<DecisionLineEvent>();
		
		return this.decisionLineEvents;
	}
	public boolean removeDecisionLineEvent(DecisionLineEvent delDLE) {
		DecisionLineEvent dle;
		int indexOf;
		
		indexOf = decisionLineEvents.indexOf(delDLE);
		if (indexOf < 0)
			return false;
		dle = decisionLineEvents.remove(indexOf);
		DatabaseSubsystem.writeDecisionLineEvent(dle);
		
		return true;
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
