package controller;

import shared.ClientState;
import shared.DatabaseSubsystem;
import shared.IProtocolHandler;
import xml.Message;

public class AdminLogInController implements IProtocolHandler {
	boolean adminVerified=false;
	String msgAdminID;
	String msgAdminCredentials;
	public AdminLogInController(){
		
	}
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Initialize local variables
		Message response = null;
		
		
		//Not sure if these are the proper .getNamedItem
		msgAdminID = new String(request.contents.getAttributes().getNamedItem("user").getNodeValue());
		msgAdminCredentials= new String(request.contents.getAttributes().getNamedItem("Credentials").getNodeValue());

		if (DatabaseSubsystem.verifyAdminCredentials(msgAdminID,  msgAdminCredentials))
			adminVerified = true;
		else
			adminVerified = false;

		//TODO: Generate response. Must contain the 'key' for future admin messages
		// I would imagine 'key' must be stored in Model as the controllers life span is short
		
		return response;
	}

}
