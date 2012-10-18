/**
 * @author Team RamRod
 * 
 * Some code taken from Professor's Heineman's ClientServerEBC project, db.Manager.java file
 */

package shared;

import java.io.*;
import java.sql.*;
import java.util.*;

import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

public class DatabaseSubsystem {
	/** Hard-coded database access information */
	private static final String SERVER   = "mysql.wpi.edu";
	private static final String USER     = "azafty";
	private static final String PASSWORD = "UjnyxY";
	private static final String DATABASE = "decisionsserver";

	// as long as you're using mysql, leave this alone.
	private static final String DATABASE_TYPE = "mysql";

	/* ------------- SQL Variables ------------- */
	/** The SQL connection to the database */
	static Connection con;

	
	/**
	 * Gets a connection to for the manager and makes a good effort to
	 * make sure it is open
	 * Taken directly from Professor Heinemen's files
	 * @return either an open connection or null
	 */
	static synchronized Connection getConnection() {
		try {
			if (con != null && con.isClosed()) {
				con = null;
			}
		} catch (SQLException e) {
			con = null;
		}
		connect();
		return con;
	}

	/** Closes the database connection 
	 *	Taken directly from Professor Heinemen's files
	 */
	public static void disconnect () {
		if (con == null) {
			return;
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		con = null;
	}


	/** 
	 * Utility method to validate connection is valid.
	 * Taken directly from Professor Heinemen's files
	 * @return true if DATABASE is available; false otherwise.
	 */
	public static boolean isConnected() {
		if (con == null) { return false; }

		try {
			return !con.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/*
	 * Taken directly from Professor Heinemen's files
	 */
	public static boolean connect() {
		// already connected.
		if (con != null) { return true; }

		// Register the JDBC driver for MySQL. Simply accessing the class
		// will properly initialize everything.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			System.err.println ("Unable to locate mySQL drivers");
			return false;
		}
		
		//Define URL for database server 
		// NOTE: must fill in DATABASE NAME
		String url =  "jdbc:" + DATABASE_TYPE + "://" +	SERVER + "/" + DATABASE;

		try {
			// Get a connection to the database for a
			// user with the given user name and password.
			con = DriverManager.getConnection(url, USER, PASSWORD);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public static boolean readEdges(DecisionLineEvent readEvent) { 
		//TODO implement
		return true;
	}
	
	public static boolean writeEdge(Edge writeEdge, String decisionLineId) {
		//TODO implement
		return true;
	}
	
	public static boolean readChoices(DecisionLineEvent readEvent) { 
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from choice where eventId=(?) ORDER BY orderValue asc");
			pstmt.setString(1, readEvent.getUniqueId());

			ResultSet myRS = pstmt.executeQuery();
			Choice newChoice;
			String name;
			int order;
			while (myRS.next()) { // error while executing the query, no results returned
				name = new String(myRS.getString("name"));
				order = myRS.getInt("orderValue");
				newChoice = new Choice(name, order);
				readEvent.getChoices().add(newChoice);
			}

			return true;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
	}
	
	public static boolean writeChoice(Choice writeChoice, String decisionLineId) {
		//TODO implement
		return true;
	}
	
	public static boolean readUsers(DecisionLineEvent readEvent) { 
		//TODO implement
		return true;
	}
	
	public static boolean writeUser(User writeUser, String decisionLineId) {
		//TODO implement
		return true;
	}
	
	public static DecisionLineEvent readDecisionLineEvent(String decisionLineId) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from event where id=(?)");
			pstmt.setString(1, decisionLineId);

			ResultSet myRS = pstmt.executeQuery();
			if (!myRS.next()) { // error while executing the query, no results returned
				return null;
			}
			
			String uniqueId = new String(myRS.getString("id"));
			String question = new String(myRS.getString("question"));
			int numberOfChoices = myRS.getInt("numberOfChoices");
			int numberOfEdges = myRS.getInt("numberOfEdges");
			EventType newType = EventType.OPEN;
			if (myRS.getInt("playStatus") == 0) // is Open
				newType = EventType.OPEN;
			else if (myRS.getInt("playStatus") == 1)  // is Closed
				newType = EventType.CLOSED;
			else if (myRS.getInt("playStatus") == 2)  // is Finished
				newType = EventType.FINISHED;

			Behavior newBehavior = Behavior.ROUNDROBIN;
			if (myRS.getBoolean("isAsynchronous")) // is Open
				newBehavior = Behavior.ASYNCHRONOUS;
			else 
				newBehavior = Behavior.ROUNDROBIN;

			DecisionLineEvent newDLE = new DecisionLineEvent(uniqueId, question, numberOfChoices, numberOfEdges, newType, newBehavior);
			newDLE.setModerator(myRS.getString("moderator"));

			if (!readChoices(newDLE)) 
				return null;

			if (!readEdges(newDLE)) 
				return null;

			if (!readUsers(newDLE)) 
				return null;

			return newDLE;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return null;
	}
	
	public static boolean writeDecisionLineEvent(DecisionLineEvent writeEvent) {
		//TODO implement
		return true;
	}
	
	public static boolean verifyAdminCredentials(String adminId, String credentials) throws IllegalArgumentException {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT COUNT(*) as CountAmt FROM administration WHERE adminId=? and adminCredentials=MD5(?);");
			pstmt.setString(1, adminId);
			pstmt.setString(2, credentials);

			ResultSet myRS = pstmt.executeQuery();
			
			if (!myRS.next()) { // error while executing the query, no results returned
				return false;
			}
			
			if (myRS.getInt("CountAmt") > 0)
				return true;
			else
				throw new IllegalArgumentException ("Invalid username or password.");
				
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
		
	}
}
