package server;
/**
 * @author Team RamRod
 */

import java.io.IOException;

import boundary.DefaultProtocolHandler;
import server.Server;
import boundary.DatabaseSubsystem;
import xml.*;

public class ApplicationMain {
	public static boolean useLocalXSD = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int portNum;
		
		if (args.length < 1) {
			System.out.println("Using default port:9371");
			portNum = 9371;
		}
		else {
			System.out.println("Configuring to use port: " + args[0]);
			portNum = Integer.parseInt(args[0]);
		}
		
		if (args.length < 2) {
			System.out.println("Using default Test database connection");
			DatabaseSubsystem.configurationTestDBAccess();
		}
		else if (args[1].equals("-p")) {
			System.out.println("Using specified Production database connection");
			DatabaseSubsystem.configurationProductionDBAccess();
		}
		else {
			System.out.println("Using default Test database connection");
			DatabaseSubsystem.configurationTestDBAccess();
		}
		
		/**
		 *  Code taken directly and nearly completely from Professor Heineman's Project: ClientServerEBC, 
		 *  Class: ServerLauncher  
		 */
		// FIRST thing to do is register the protocol being used. There will be a single class protocol
		// that will be defined and which everyone will use. For now, demonstrate with skeleton protocol.
		if (!Message.configure(getMessageXSD())) { 
			System.exit(0);
		}
		
		if (!DatabaseSubsystem.connect()) {
			System.out.println("Error, cannot connect to the database");
			System.exit(0);
		}
		
		// Start server and have ProtocolHandler be responsible for all XML messages.
		Server server = new Server(new DefaultProtocolHandler(), portNum);
	
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
	
	public static String getMessageXSD() {
		if (useLocalXSD)
			return "draw2choose.xsd";
		else
			return "http://draw2choose.com/draw2choose.xsd";
	}
}

