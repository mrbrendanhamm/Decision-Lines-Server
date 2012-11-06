package controller;

import java.util.ArrayList;

import org.w3c.dom.Node;

import boundary.DatabaseSubsystem;

import entity.DecisionLineEvent.EventType;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;

public class ProduceReportController implements IProtocolHandler {
	String xmlString;

	
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
		System.out.println("Request:"+request);
		
		Node child = request.contents.getFirstChild();
		String eventType = child.getAttributes().getNamedItem("type").getNodeValue();
		

		
		//translate from string to eventType
		EventType myType = EventType.ERROR;
		if (eventType.equals("open"))
			myType = EventType.OPEN;
		else if (eventType.equals("closed"))
			myType = EventType.OPEN;
		else if (eventType.equals("finished"))
			myType = EventType.FINISHED;
			
		
		//this one probably needs more definition from the professor, but a first shot would look like this:
		
		//create header for the XML response string
		xmlString = "<response id='"+request.id()+"'+version='1.0'>"+ 
				"<reportResponse>";

		//read from database
		ArrayList<String> reportResults = DatabaseSubsystem.produceReport(myType);

		
		for (int i = 0; i < reportResults.size(); i++) {
			//iterate through the returned ArrayList, adding entries to the XML response for each element
			String value = reportResults.get(i);
			xmlString = xmlString+"<entry='"+value+"'/>";
		}
		
		
		xmlString = xmlString + "</reportResponse><response>";
		System.out.println(xmlString);
		Message response = new Message(xmlString);
		System.out.println("Response:"+response);
		return response;
	}

}
