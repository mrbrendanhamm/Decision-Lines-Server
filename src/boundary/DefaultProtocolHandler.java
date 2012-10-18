/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package boundary;

import org.w3c.dom.Node;

import controller.*;
import xml.*;
import shared.*;

/**
 * Sample implementation of a protocol handler to respond to messages received from clients.
 * You should follow this template when designing YOUR protocol handler.
 * <p>
 * To avoid issues with multiple clients submitting requests concurrently,
 * simply make the {@link #process(ClientState, Message)} method synchronized.
 * This will ensure that no more than one server thread executes this method
 * at a time.
 * <p>
 * Also extended to support detection of client disconnects so these can release the lock
 * if indeed the client was the one locking the model.
 */
public class DefaultProtocolHandler implements IShutdownHandler {
	
	@Override
	public synchronized Message process (ClientState st, Message request) {
		Node child = request.contents.getFirstChild();
		String type = child.getLocalName();
		
		System.out.println (request);
		
		if (type.equals("connectRequest")) 
			return new ConnectToDLEController().process(st, request);
		else if (type.equals("addChoiceRequest")) 
			return new AddChoiceController().process(st,  request);
		else if (type.equals("addEdgeRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("adminRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("closeRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("createRequest")) 
			return new CreateDLEController().process(st, request);
		else if (type.equals("forceRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("removeRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("reportRequest")) 
			return null; //TODO write the appropriate controller link here
		else if (type.equals("signInRequest")) 
			return new SignIntoDLEController().process(st, request);
		//there does not appear to be a DeleteChoice option.  
		
		// unknown? no idea what to do
		System.err.println("Unable to handle message:" + request);
		return null;
	}

	@Override
	public void logout(ClientState st) {
		//TODO to be replace by our customer disconnect controller
		//new ClientDisconnectController().process(st);		
	} 
}
