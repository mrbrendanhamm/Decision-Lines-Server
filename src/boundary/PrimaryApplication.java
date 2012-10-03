package boundary;

import databaseLayer.DatabaseSubSystem;

public class PrimaryApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		
		//default access method, all static so no need to generate an object
		DatabaseSubSystem.readDecisionLineEvent(null);
	}

}
