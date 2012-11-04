package controller;

import org.w3c.dom.Node;

import entity.Model;

import server.ClientState;
import server.IProtocolHandler;
import boundary.DatabaseSubsystem;
import xml.Message;

public class AdminLogInController implements IProtocolHandler {
	Model myModel;
	String msgAdminID;
	String msgAdminCredentials;
	String userID;
	String key;
	Message response;
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
		myModel = Model.getInstance();
		/* Walking through the message contents isn't always straight forward.  There are several extra
		 * nodes in the list that do not correspond to anything that I can identify, so it's largely
		 * trial an error.  To build this list I first went to my CreateDLEController class and
		 * reviewed the message parsing function.  From that I figured out to throw away most of the 
		 * First Child records.  There might be some real logic here, but I'll be damned if I can find 
		 * it.  But the one good thing is that once you define it successfully, the XSD verifier will 
		 * guaranty that all future messages are formatted in a similar manner.
		 */
		
		//parse the message
		Node child = request.contents.getFirstChild();
		msgAdminID = new String(child.getAttributes().getNamedItem("name").getNodeValue());
		msgAdminCredentials= new String(child.getAttributes().getNamedItem("password").getNodeValue());
		userID = child.getAttributes().getNamedItem("id").getNodeValue();
		
		//print them to line
		System.out.println(msgAdminID);
		System.out.println(msgAdminCredentials);
		
		if (DatabaseSubsystem.verifyAdminCredentials(msgAdminID,  msgAdminCredentials)){
			//get key
			//TODO define this method
			//key = myModel.getKey();
			writeSuccess();
		}	
		
		else {
			writeFailure();
			
		}


		//TODO: Need to finish out how these controllers lookqa
		
		
		return response;
	}
	
	/**
	 * This method will return the response with the key if id/password match.
	 * @return
	 */
	public Message writeSuccess(){
		String xmlString = Message.responseHeader(userID) + 
				"<adminResponse key='"+ key+ "'/>"+
				"</response>";
		Message retval = new Message(xmlString);
		return(retval);
	}
	/** This method returns a failure response for invalid credentials
	 * 
	 * @return
	 */
	public Message writeFailure(){
		String xmlString = Message.responseHeader(userID, "Invalid Credentials") +
				"<adminResponse/>"+
				"<r/esponse>";
		Message retval = new Message(xmlString);
		return(retval);
	}

}
