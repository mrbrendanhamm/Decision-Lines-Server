package controller;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ClientState;
import shared.IProtocolHandler;
import xml.Message;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

//TODO change once entity defined
class Choice { 
	public String value;
	public int index;
	
	Choice(String value, int index) {
		this.value = value;
		this.index = index;
	}
	
	/**
	 * This is required for the ArrayList object to function properly.  Essentially I am enforcing
	 * that the index number is unique.  If an index number is already used then future
	 * additions are ignored.  The actual code in here is a very technical requirement of java, and it
	 * is probably best if I explain it in person rather than have you review it here and research it
	 * online.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		
		if (!(o instanceof Choice))
			return false;

		Choice tmp = (Choice) o;
		
		if (tmp.index == this.index) 
			return true;
		
		return false;
	}
}

public class CreateDLEController implements IProtocolHandler {
	ArrayList<Choice> myChoices; 
	EventType myType;
	Behavior myBehavior;
	String myQuestion;
	int numOfChoices;
	int numOfRounds;
	String moderator;
	String moderatorPassword;
	String myEventId;
	String myVersion;
	
	public CreateDLEController() {
		myChoices = new ArrayList<Choice>();
		myQuestion = new String();
		moderator = new String();
		moderatorPassword = new String();
	}
	
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// Read in the Request
		if (!parseMessage(request)) {
			//do error message
			return null;
		}
		
		//Check for missing parameters
		if (myQuestion.equals("")) /* test all parameters here */ { 
			//debug message for a mal-formed message
			return null;
		}
		
		//Both open and closed DLEs are handled through this request, so we must branch the program logic
		// to handle each type differently 
		if (myType == EventType.OPEN) {
			if (!createOpenDLE()) 
				return null;
		}
		else if (!createClosedDLE()) //must be closed
			return null;
		
		//Update the model appropriately
		
		// Write message Response.  This might change if the response for an Open DLE is different from the
		// response for a closed DLE
		
		//send a message to all other connected clients
		
		return writeSuccessResponse(); //this specific message is sent back to the requesting client
	}
	
	/**
	 * Creates the Open DLE.  This is where the model access and/or database querying would happen
	 * @return
	 */
	boolean createOpenDLE() {
		//TODO implement
		// Execute necessary Controller functionality
		/*
			eventId = ....
			DecisionLineEvent myDLE = DatabaseSubsystem.readDecisionLineEvent(eventId);
			response = XMLProcotolLayer.writeDLEMessage(myDLE);
		 */
		return true;
	}
	
	/**
	 * Creates the Closed DLE.  This is where the model access and/or database querying would happen
	 * @return
	 */
	boolean createClosedDLE() {
		//TODO implement
		// Execute necessary Controller functionality
		/*
			eventId = ....
			DecisionLineEvent myDLE = DatabaseSubsystem.readDecisionLineEvent(eventId);
			response = XMLProcotolLayer.writeDLEMessage(myDLE);
		 */
		return true;
	}
	
	
	/**
	 * This method performs the XML manipulation to read the message and separate out the data.
	 * In general there are two types of data: Nodes and Attributes.  Attributes exist only on Nodes
	 * Nodes can hold other Nodes.  So parsing really just means traversing the node list and knowing
	 * when to query for attributes.  
	 *  
	 * @param request - XML Message of type createRequest that needs to be parsed for inputs
	 * @return true if successfully parsed
	 */
	boolean parseMessage(Message request) {
		NamedNodeMap myAttributes = request.contents.getAttributes();
		/*
		 * Parse through the node list.  I don't necessarily know the order that they appear in, so 
		 * I have to examine each node to determine the type.  And just be warned, there are additional nodes
		 * which serve an unknown purpose (at least as far as I know).
		 */
		for (int i = 0; i < myAttributes.getLength(); i++) {
			if (myAttributes.item(i).getLocalName().equals("version")) {
				myVersion = new String(myAttributes.item(i).getNodeValue());
			}
			else if (myAttributes.item(i).getLocalName().equals("id")) {
				myEventId = new String(myAttributes.item(i).getNodeValue());
			}
		}

		Node child = request.contents.getFirstChild();
		child = child.getNextSibling();
		myAttributes = child.getAttributes(); //grab the XML attributes for this node
		
		for (int i = 0; i < myAttributes.getLength(); i++) { 
			if (myAttributes.item(i).getLocalName().equals("type")) {
				if (myAttributes.item(i).getNodeValue().equals("open"))
					myType = EventType.OPEN;
				else
					myType = EventType.CLOSED;
			}
			else if (myAttributes.item(i).getLocalName().equals("behavior")) {
				if (myAttributes.item(i).getNodeValue().equals("roundRobin"))
					myBehavior = Behavior.ROUNDROBIN;
				else
					myBehavior = Behavior.ASYNCHRONOUS;
			}
			else if (myAttributes.item(i).getLocalName().equals("question")) {
				myQuestion = new String(myAttributes.item(i).getNodeValue());
			}
			else if (myAttributes.item(i).getLocalName().equals("numChoices")) {
				numOfChoices = Integer.parseInt(myAttributes.item(i).getNodeValue());
			}
			else if (myAttributes.item(i).getLocalName().equals("numRounds")) {
				numOfRounds = Integer.parseInt(myAttributes.item(i).getNodeValue());
			}
		}
		
		//one layer deep are the nodes that hold information for the Choices and the Moderator.
		//unfortunately children nodes are accessed through a separate entity called a 'NodeList'
		NodeList myList = child.getChildNodes();

		/*
		 * Parse through each child.  Each of these children also have attributes
		 */
		for (int i = 0; i < myList.getLength(); i++) { 
			if (myList.item(i).getNodeName().equals("choice")) {  //A Choice has been found
				String choiceName = new String("");
				int indexOf = -1;
				
				myAttributes = myList.item(i).getAttributes();
				
				for (int x = 0; x < myAttributes.getLength(); x++) {
					if (myAttributes.item(x).getNodeName().equals("value"))
						choiceName = new String(myAttributes.item(x).getNodeValue());
					else if (myAttributes.item(x).getNodeName().equals("index"))
						indexOf = Integer.parseInt(myAttributes.item(x).getNodeValue());
				}
				
				//combine these two pieces of information into the choice, and add it to our choice array
				Choice newChoice = new Choice(choiceName, indexOf);
				myChoices.add(newChoice);
			}
			else if (myList.item(i).getNodeName().equals("user")) {  //A Moderator has been found
				//similar deal for users.  these classes have two parts, user name and password, that must be
				// parsed from the attributes
				String userName = new String("");
				String password = new String("");
				
				myAttributes = myList.item(i).getAttributes();
				
				for (int x = 0; x < myAttributes.getLength(); x++) {
					if (myAttributes.item(x).getNodeName().equals("name"))
						userName = new String(myAttributes.item(x).getNodeValue());
					else if (myAttributes.item(x).getNodeName().equals("password"))
						password = new String(myAttributes.item(x).getNodeValue());
				}
				
				//In reality, I would create a User entity here and assign it these values.  since
				// one doesn't exist right now, I'll store it in the String variables listed in the controller's
				// definition
				moderator = new String(userName);
				moderatorPassword = new String(password);
			}
		}
		
		//Theoretically, the message has been fully parsed at this point
		return true;
	}

	/**
	 * This method creates the appropriate response.  Might need to be split into two methods, one for 
	 * Open and one for Closed DLEs
	 *  
	 * @return A properly formatted Success method, or null if a message cannot be properly formed
	 */
	Message writeSuccessResponse() {
		return null;
	}
}

