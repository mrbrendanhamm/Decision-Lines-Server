/**
 * @author Team RamRod
 */

package shared;

// temporary until the real entities are defined
class Model {}
class Edge {}
class User {}
class Choice {}
class ConnectedClient {}
class DecisionLineEvent {}


public class DatabaseSubsystem {
	private static final String databaseConnectStr = "";
	//local variable to hold database connection here
	
	DatabaseSubsystem() {
		//do initialization stuff here
	}
	
	public static boolean readEdges(DecisionLineEvent readEvent) { 
		return true;
	}
	
	public static boolean writeEdge(Edge writeEdge, String decisionLineId) {
		return true;
	}
	
	public static boolean readChoices(DecisionLineEvent readEvent) { 
		return true;
	}
	
	public static boolean writeChoice(Choice writeChoice, String decisionLineId) {
		return true;
	}
	
	public static boolean readUsers(DecisionLineEvent readEvent) { 
		return true;
	}
	
	public static boolean writeUser(User writeUser, String decisionLineId) {
		return true;
	}
	
	public static boolean readDecisionLineEvent(DecisionLineEvent readEvent) { 
		return true;
	}
	
	public static boolean writeDecisionLineEvent(DecisionLineEvent writeEvent) {
		return true;
	}
}
