package controller;

import org.w3c.dom.Node;

import server.ClientState;
import server.IProtocolHandler;
import boundary.DatabaseSubsystem;
import xml.Message;

public class AdminLogInController implements IProtocolHandler {
	boolean adminVerified=false;
	String msgAdminID;
	String msgAdminCredentials;
	public AdminLogInController(){
		
	}
	
	/**
	 * This method is the calling entry point for this controller.  It is assumed that the message type is appropriate
	 * for this controller.
	 * 
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Initialize local variables
		Message response = null;

		/* Walking through the message contents isn't always straight forward.  There are several extra
		 * nodes in the list that do not correspond to anything that I can identify, so it's largely
		 * trial an error.  To build this list I first went to my CreateDLEController class and
		 * reviewed the message parsing function.  From that I figured out to throw away most of the 
		 * First Child records.  There might be some real logic here, but I'll be damned if I can find 
		 * it.  But the one good thing is that once you define it successfully, the XSD verifier will 
		 * guaranty that all future messages are formatted in a similar manner.
		 */
		Node child = request.contents.getChildNodes().item(1).getChildNodes().item(1);
		
		//Not sure if these are the proper .getNamedItem
		msgAdminID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		msgAdminCredentials= new String(child.getAttributes().getNamedItem("password").getNodeValue());

		if (DatabaseSubsystem.verifyAdminCredentials(msgAdminID,  msgAdminCredentials))
			adminVerified = true;
		else
			adminVerified = false;

		//TODO: Generate response. Must contain the 'key' for future admin messages
		// I would imagine 'key' must be stored in Model as the controllers life span is short
		
		return response;
	}

}
