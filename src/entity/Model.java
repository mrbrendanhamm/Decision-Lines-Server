package entity;

import java.util.ArrayList;
import java.util.UUID;

import boundary.DatabaseSubsystem;

public class Model {
	// All decisionLineEvents stored in an ArrayList
	private ArrayList<DecisionLineEvent> decisionLineEvents = null;
	// Singleton Model instance
	static Model thisModel = null;
	
	final String key = UUID.randomUUID().toString(); 
	
	/**
	 * Default constructor
	 */
	protected Model() {
	}
	
	/**
	 * This method is to return the instance of the Model
	 * 
	 * @return the Model instance
	 */
	public static Model getInstance() {
		if (thisModel == null) {
			thisModel = new Model();
		}
		
		return thisModel;
	}
	
	/**
	 * This method is to get the private attribute DecisionLineEvents of Model
	 * 
	 * @return ArrayList of DecisionLineEvent
	 */
	public ArrayList<DecisionLineEvent> getDecisionLineEvents()
	{
		if (decisionLineEvents == null)
			decisionLineEvents = new ArrayList<DecisionLineEvent>();
		
		return this.decisionLineEvents;
	}
	
	/**
	 * This method is to remove the certain DecisionLineEvent from the Model
	 * 
	 * @param DecisionLineEvent delDLE - the DecisionLineEvent need to be removed
	 * @return True if success, false if the certain DLE is not in this Model
	 */
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
	
	/**
	 * This method is to get the certain DecisionLineEvent by uniqueId in the Modle
	 * 
	 * @param String uniqueId - the uniqueId of the DecisionLineEvent requiring
	 * @return The DecisionLineEvent with the certaion uniqueId
	 */
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

	public boolean checkKey(String testKey) {
		if(testKey.equals(key)){
			return true;
		}
		else return false;
	}
}
