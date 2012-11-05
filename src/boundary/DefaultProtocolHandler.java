/**
 * Taken directly and almost completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer.
 */

package boundary;

import org.w3c.dom.Node;

import controller.*;
import xml.*;
import server.ClientState;
import server.IShutdownHandler;

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
		
		System.out.println ("Receiving: " + request);
		
		/* this code is never called, because the ConnectResponse is handled by the Server at a lower level before it gets here
		if (type.equals("connectRequest")) 
			return new ConnectToDLEController().process(st, request);
		*/
		if (type.equals("addChoiceRequest")) 
			return new AddChoiceController().process(st,  request);
		else if (type.equals("addEdgeRequest")) 
			return new AddEdgeController().process(st, request);
		else if (type.equals("adminRequest")) 
			return new AdminLogInController().process(st, request);
		else if (type.equals("closeRequest")) 
			return new CloseOpenDLEController().process(st, request);
		else if (type.equals("createRequest")) 
			return new CreateDLEController().process(st, request);
		else if (type.equals("forceRequest")) 
			return new ForceFinishController().process(st, request);
		else if (type.equals("removeRequest")) 
			return new RemoveDLEController().process(st, request);
		else if (type.equals("reportRequest")) 
			return new ProduceReportController().process(st, request);
		else if (type.equals("signInRequest")) 
			return new SignIntoDLEController().process(st, request);
		
		// unknown? no idea what to do
		System.err.println("Unable to handle message:" + request);
		return null;
	}

	/**
	 * This method is called when a client disconnects from the server 
	 * 
	 * @param st - the ClientState object that corresponds to the disconnecting client
	 */
	@Override
	public void logout(ClientState st) {
		//notify model that the client is disconnecting
		new ClientDisconnect().disconnectClient(st.id());	
	} 
}
