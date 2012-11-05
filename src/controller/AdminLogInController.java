package controller;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	 * This method takes an xml message for AdminLogIn and either passes back
	 * the admin verification key, or a failed message
	 * 
	 * @param state - The ClientState of the requesting client
	 * @param request - An XML request
	 * @return A properly formatted XML response or null if one cannot be formed
	 */
	@Override
	public synchronized Message process(ClientState state, Message request) {
		System.out.println("Request:"+request);
		// get the model
		myModel = Model.getInstance();

		//parse the message
		Node child = request.contents.getFirstChild();
		NodeList listChild = child.getChildNodes();
		userID = request.contents.getAttributes().getNamedItem("id").getNodeValue();//NamedItem("id").getNodeValue();
		msgAdminID = listChild.item(0).getAttributes().getNamedItem("name").getNodeValue();
		msgAdminCredentials = listChild.item(0).getAttributes().getNamedItem("password").getNodeValue();
		

		//verify the admin credentials and generate message with key
		if (DatabaseSubsystem.verifyAdminCredentials(msgAdminID,  msgAdminCredentials)){
			key = myModel.getKey();
			response = writeSuccess();
		}	
		// generate failure message
		else {
			response = writeFailure();
			
		}
		System.out.println("Response:"+response);
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
				"</response>";
		Message retval = new Message(xmlString);
		return(retval);
	}

}
