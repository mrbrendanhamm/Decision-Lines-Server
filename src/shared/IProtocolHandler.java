package shared;
/**
 * Taken directly from Professor Heineman's fusion project Draw2Choose, from java project ClientServer 
 */


import shared.ClientState;
import xml.*;

/** 
 * ServerThread out-sources processing of protocol to implementors 
 * of this interface. Implementor is only concerned with request and response messages.
 */
public interface IProtocolHandler {

	/**
	 * Process the protocol given a Message request received on behalf of the designated 
	 * thread. Return Response Message as the response to the request or null to indicate
	 * that the connection to that client should be terminated.
	 * <p>
	 * ClientState object represents the client on whose behalf the request was received.
	 * Use this object to write back a response.
	 * <p>
	 * Classes that implement this interface must make sure that the
	 * {@link #process(ClientState, Message)} method is synchronized, 
	 * otherwise 
	 */
	Message process(ClientState state, Message request);
}