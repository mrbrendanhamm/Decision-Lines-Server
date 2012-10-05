/**
 * @author Team RamRod
 */

import java.io.IOException;
import boundary.DefaultProtocolHandler;
import server.Server;
import shared.DatabaseSubsystem;
import xml.*;

public class ApplicationMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		
		//sample access method, all static so no need to generate an object to use
		DatabaseSubsystem.readDecisionLineEvent(null);

		/**
		 *  Code taken directly and nearly completely from Professor Heineman's Project: ClientServerEBC, 
		 *  Class: ServerLauncher  
		 */
		// FIRST thing to do is register the protocol being used. There will be a single class protocol
		// that will be defined and which everyone will use. For now, demonstrate with skeleton protocol.
		if (!Message.configure("XMLMessageStructure.xsd")) { //TODO: replace this with the new message structure once finalized
			System.exit(0);
		}
		
		// Start server and have ProtocolHandler be responsible for all XML messages.
		Server server = new Server(new DefaultProtocolHandler(), 9371);
	
		try {
			server.bind();
		} catch (IOException ioe) {
			System.err.println("Unable to launch server:" + ioe.getMessage());
			System.exit(-1);
		}

		// process all requests and exit.
		System.out.println("Server awaiting client connections");
		try {
			server.process();
			System.out.println("Server shutting down.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}    
	}
}

