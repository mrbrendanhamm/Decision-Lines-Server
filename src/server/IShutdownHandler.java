/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package server;


/** 
 * If you want to be told when a client exits, make sure your ProtocolHandler
 * implements {@link IShutdownHandler} instead of {@link IProtocolHandler}.
 * <p>
 */
public interface IShutdownHandler extends IProtocolHandler {

	/**
	 * When client terminates connection, this method is invoked, but only if
	 * the ProtocolHandler also implements IShutdownHandler.
	 * <p> 
	 * Parameter is the state of the thread being terminated.
	 */
	void logout(ClientState state);
}