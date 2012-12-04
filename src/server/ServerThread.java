/**
 * Taken directly and completely from Professor Heineman's fusion project Draw2Choose, 
 * from the java project ClientServer 
 */

package server;

import java.io.*;
import java.net.*;
import java.util.UUID;

import org.w3c.dom.*;

import controller.ClientDisconnect;

import xml.*;

/** Thread to handle individual requests from a client. */
public class ServerThread extends Thread implements ClientState {
	final Socket client;             // Socket used by thread to communicate with client.
	final BufferedReader fromClient; // Used to process strings from client. 
	final PrintWriter toClient;      // Used to send strings to client.
	final IProtocolHandler handler;  // Handler to process protocol.
	final Server server;             // Server for which we are operating.
	final String id;                 // Associated unique ID for this thread.
	Object data;               		 // User-defined object associated with each thread.
	
	/** Create objects to handle input/output to client. */
	ServerThread (Server srv, Socket s, IProtocolHandler h) throws IOException {
		server = srv;
		fromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
		toClient = new PrintWriter (s.getOutputStream(), true);
		client = s;
		handler = h;
		id = UUID.randomUUID().toString();
	}

	/**
	 * Thread receives strings from the client, processes them and returns
	 * result back to the client. 
	 * 
	 * Each client connection is assigned a unique identifier by which one can reference
	 * it in the future.
	 * 
	 * If protocol is violated, then immediately close connection to client.
	 */
	public void run() {
		// Initial connect request comes in
		Message m = Parser.extractRequest(fromClient);
		Node child = m.contents.getFirstChild();
		if (!child.getLocalName().equals (Parser.connectRequest)) {
			return;
		}
		
		System.out.println("Receiving: " + m);

		// Return connect response with our (statistically) unique ID.
		String xmlString = Message.responseHeader(m.id()) + "<connectResponse id='" + id + "'/></response>";
		Message r = new Message (xmlString);
		if (!sendMessage (r)) {
			System.err.println("Unable to respond to connect Request from remote Client.");
			return;
		}

		// register our thread with the server
		Server.register(id, this);
		
		// have handler manage the protocol until it decides it is done.
		while ((m = Parser.extractRequest(fromClient)) != null) {
			
			Message response = handler.process(this, m);
			if (!sendMessage(response)) {
				break;
			}
		}
		
		// client is done so thread can be de-registered
		if (handler instanceof IShutdownHandler) {
			((IShutdownHandler)handler).logout(Server.getState(id));
		}
		Server.unregister(id);

		// close communication to client.
		try {
			fromClient.close();
			toClient.close();
			client.close();
		} catch (IOException e) {
			System.err.println("Unable to close connection:" + e.getMessage());
		}
	}
	
	/** Return (statistically) unique id. */
	public String id() { return id; }
	
	/**
	 * Associate new object data as the user-defined data for the thread and return the old previous value.
	 */
	public Object setData(Object newData) {
		Object old = data;
		data = newData;
		return old;
	}
	
	/**
	 * Retrieve user-defined data associated with this thread.
	 */
	public Object getData() {
		return data;
	}
	
	/** 
	 * Send the given message to the client on whose behalf this thread is executing and return true
	 * on success, false on error. 
	 */
	public boolean sendMessage (Message m) { 
		if (m == null) { return false; }
		
		toClient.println(m.toString());
		
		return !toClient.checkError();
	}
}