package controller;

import shared.ClientState;
import shared.DatabaseSubsystem;
import shared.IProtocolHandler;
import xml.Message;

public class AdminLogInController implements IProtocolHandler {

	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Initialize local variables
		Message response = null;
		boolean adminVerified=false;
		/**  These are the incoming data (not sure how we get them)
		 *   Should this be added to input parameters of process?)
		 *   Need to be changed from null
		 */
		String msgAdminID = null;
		String msgAdminCredentials= null;;

		// Andrew: instead of this how about use the database subsystem function call? 
		if (DatabaseSubsystem.verifyAdminCredentials(msgAdminID,  msgAdminCredentials))
			adminVerified = true;
		else
			adminVerified = false;
		
		/** Temporary store for a Admin and their Credentials. 
		 *  Probably be in database realisticly.
		 */
		/*
		String adminId = "TheAdmin123";	
		String adminCredentials= "TheAdminPassword123";
		
		// Check if credentials match
		if (msgAdminID == adminId && msgAdminCredentials == adminCredentials) {
			adminVerified=true;
		}
		else adminVerified=false;
		*/
				
		return response;
	}

}
