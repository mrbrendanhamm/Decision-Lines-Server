package boundary;
/**
 * @author Team RamRod
 * 
 * Some code taken from Professor's Heineman's ClientServerEBC project, db.Manager.java file
 */


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
//import java.util.Date;

import entity.*;
import entity.DecisionLineEvent.Behavior;
import entity.DecisionLineEvent.EventType;

/**
 * This class uses the Facade design template to manage all communication to and from the database. 
 */
public class DatabaseSubsystem {
	/** Hard-coded database access information */
	private static final String SERVER   = "mysql.wpi.edu";
	private static final String USER     = "azafty";
	private static final String PASSWORD = "UjnyxY";
	private static final String DATABASE = "decisionsserver";
	/* full connection string from SSH 
	 * mysql -hmysql.wpi.edu -uazafty -pUjnyxY decisionsserver
	 */


	// as long as you're using mysql, leave this alone.
	private static final String DATABASE_TYPE = "mysql";

	/* ------------- SQL Variables ------------- */
	/** The SQL connection to the database */
	static Connection con;

	
	/**
	 * Gets a connection to for the manager and makes a good effort to
	 * make sure it is open
	 * Taken directly from Professor Heineman's files
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

	/** 
	 *  Closes the database connection 
	 *	Taken directly from Professor Heineman's files
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
	 * Taken directly from Professor Heineman's files
	 * 
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

	/**
	 * Taken directly from Professor Heineman's files
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

	/**
	 * This method reads in the edges associated with the readEvent's unique id from the database.  All edges are added
	 * to the event.
	 * 
	 * @param readEvent - the event to have it's edges read in from the DB
	 * @return - true if successfully read, false otherwise
	 */
	public static boolean readEdges(DecisionLineEvent readEvent) { 
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from edge where eventId=(?)");
			pstmt.setString(1, readEvent.getUniqueId());

			ResultSet myRS = pstmt.executeQuery();
			Edge newEdge;
			Choice leftChoice, rightChoice;
			int height, choiceId, indexOf;
			
			while (myRS.next()) {
				height = myRS.getInt("height");
				choiceId = myRS.getInt("choiceId");
				
				indexOf = readEvent.getChoices().indexOf(new Choice("", choiceId, -1));
				if (indexOf == -1) //error, choice dictated is not valid
					return false;
				leftChoice = readEvent.getChoices().get(indexOf);
				
				indexOf = readEvent.getChoices().indexOf(new Choice("", choiceId+1, -1));
				if (indexOf == -1) //error, choice dictated is not valid
					return false;
				rightChoice = readEvent.getChoices().get(indexOf);
				
				newEdge = new Edge(leftChoice, rightChoice, height);
				readEvent.getEdges().add(newEdge);
			}

			return true;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
	}
	
	/**
	 * The method writes an edge to the database.  An edge is unique if it's height and choice are different from 
	 * any existing edge.  If they are the same then the record is an update (new otherwise).
	 * 
	 * @param writeEdge - The edge to be written
	 * @param decisionLineId - the id of the parent event
	 * @param byUser - the User that played the edge
	 * @return -1 if an error was encountered, the number of records affected otherwise
	 */
	public static int writeEdge(Edge writeEdge, String decisionLineId) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateEdge(?, ?, ?)");
			pstmt.setString(1, decisionLineId);
			pstmt.setInt(2, writeEdge.getHeight());
			pstmt.setInt(3, writeEdge.getLeftChoice().getOrder());

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * This method reads in all the choices linked to the requested event from the database.  All choices are added to the
	 * event
	 * 
	 * @param readEvent - the event to have it's choices read in
	 * @return true if successfully processed, false otherwise
	 */
	public static boolean readChoices(DecisionLineEvent readEvent) { 
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from choice where eventId=(?) ORDER BY orderValue asc");
			pstmt.setString(1, readEvent.getUniqueId());

			ResultSet myRS = pstmt.executeQuery();
			Choice newChoice;
			String name;
			int order;
			int finalDecision;
			while (myRS.next()) { // error while executing the query, no results returned
				name = new String(myRS.getString("name"));
				order = myRS.getInt("orderValue");
				finalDecision = myRS.getInt("finalDecisionOrder");
				newChoice = new Choice(name, order, finalDecision);
				readEvent.getChoices().add(newChoice);
			}

			return true;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
	}
	
	/**
	 * This method writes a choice to the database.  If a Choice order number is already present, then this
	 * is treated as an update.  If the choice order number is not in the database, then it is a new record
	 * 
	 * @param writeChoice - the Choice to be written
	 * @param decisionLineId - the Id of the parent Event
	 * @return -1 if an error is encountered, the number of records affected otherwise
	 */
	public static int writeChoice(Choice writeChoice, String decisionLineId) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateChoice(?, ?, ?, ?)");
			pstmt.setString(1, decisionLineId);
			pstmt.setInt(2, writeChoice.getOrder());
			pstmt.setString(3, writeChoice.getName());
			pstmt.setInt(4,  writeChoice.getFinalDecisionOrder());

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * Reads in Users for a given DecisionLineEvent from the database.  The event should be free of any users before calling.  
	 * 
	 * @param readEvent - The event to have it's users read in
	 * @return true if successful, false if any error is encountered
	 */
	public static boolean readUsers(DecisionLineEvent readEvent) { 
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT userName, userPassword, position from user where eventId=(?)");
			pstmt.setString(1, readEvent.getUniqueId());

			ResultSet myRS = pstmt.executeQuery();
			User newUser;
			String name, password;
			int position;
			while (myRS.next()) { // error while executing the query, no results returned
				name = new String(myRS.getString("userName"));
				password = new String(myRS.getString("userPassword"));
				position = myRS.getInt("position");
				newUser = new User(name, password, position);
				readEvent.getUsers().add(newUser);
			}

			return true;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
	}
	
	/**
	 * This method writes a user to the database.  If the user name and event id are unique, then this is
	 * treated as a new record.  If the user name and event id are already present then this is treated
	 * as an update.  
	 * 
	 * @param writeUser - the User to be written
	 * @param decisionLineId - the id of the Event that the user is part of
	 * @return -1 if an error is reached, the number of affected records otherwise
	 */
	public static int writeUser(User writeUser, String decisionLineId) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateUser(?, ?, ?, ?)");
			pstmt.setString(1, decisionLineId);
			pstmt.setString(2, writeUser.getUser());
			pstmt.setString(3, writeUser.getPassword());
			pstmt.setInt(4,  writeUser.getPosition());

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * This method reads in an entire decision line.  Subsequent calls from this method are made to the readUser, 
	 * readEdge, and readChoice methods.  
	 * 
	 * @param decisionLineId - the unique identifier for the decision line event
	 * @return The fully formed decision line event or null if any error was encountered  
	 */
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
			java.util.Date dleDate = myRS.getDate("createdDate");
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
			newDLE.setDate(dleDate);

			if (!readChoices(newDLE)) 
				return null;

			if (!readUsers(newDLE)) 
				return null;

			if (!readEdges(newDLE)) 
				return null;

			/* 
			 * Check if the decision line event is finished yet no choices have been made.  DLEs can be finished through an offline
			 * manner when ForceFinish is called but the DLE is not currently in memory.
			 */
			
			if (newDLE.getEventType() == EventType.FINISHED) {
				boolean decisionMissing = false;
				for(int i = 0; i < newDLE.getChoices().size(); i++) {
					if (newDLE.getChoice(i).getFinalDecisionOrder() == -1)
						decisionMissing = true;
				}
				if (decisionMissing) { 
					//at least one choice does not have it's final order set.  Set them and write the answer back to the DB
					newDLE.getFinalOrder();
					writeDecisionLineEvent(newDLE);
				}
			}
			
			return newDLE;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return null;
	}

	/**
	 * This method writes an Event to the Database using a stored procedure on the DB.  This method will
	 * write out all associated parts of the Event
	 * 
	 * @param writeEvent - the event to be written to DB
	 * @return - -1 if a failure, otherwise the number of records affected is return
	 */
	public static int writeDecisionLineEvent(DecisionLineEvent writeEvent) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateEvent(?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, writeEvent.getUniqueId());
			pstmt.setString(2, writeEvent.getQuestion());
			pstmt.setInt(3, writeEvent.getNumberOfChoice());
			pstmt.setInt(4, writeEvent.getNumberOfEdge());
			if (writeEvent.getBehavior() == Behavior.ASYNCHRONOUS)
				pstmt.setBoolean(5, true);
			else
				pstmt.setBoolean(5, false);
			pstmt.setString(6, writeEvent.getModerator());
			if (writeEvent.getEventType() == EventType.FINISHED)
				pstmt.setInt(7, 2);
			else if (writeEvent.getEventType() == EventType.CLOSED)
				pstmt.setInt(7, 1);
			else
				pstmt.setInt(7, 0);
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
			pstmt.setString(8, ft.format(writeEvent.getDate()));
			int retVal = pstmt.executeUpdate();
			
			if (retVal < 0) 
				return -1; //error
			
			for (int i = 0; i < writeEvent.getChoices().size(); i++) {
				if (writeChoice(writeEvent.getChoices().get(i), writeEvent.getUniqueId()) < 0)
					return -1; //error while writing choices
			}
			
			ArrayList<Edge> edgeList = writeEvent.getEdges();
			for(int i = 0; i < edgeList.size(); i++) {
				if (writeEdge(edgeList.get(i), writeEvent.getUniqueId()) < 0)
					return -1; //error while writing edges
			}
			
			ArrayList<User> userList = writeEvent.getUsers();
			for(int i = 0; i < userList.size(); i++) {
				if (writeUser(userList.get(i), writeEvent.getUniqueId()) < 0)
					return -1; //errors while writing users
			}
			
			return retVal;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * This method deletes any events from the database that bear this event id.
	 * 
	 * @param eventId - the id of the event to be deleted
	 * @return the number of records that were affected or -1 if there was an error
	 */
	public static int deleteEventById(String eventId) {
		try {
			String qry = "DELETE FROM event where id='" + eventId + "'";
			
			PreparedStatement pstmt = getConnection().prepareStatement(qry);

			int numRecordsAffected = pstmt.executeUpdate();
			
			return numRecordsAffected;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * This method produces a report of events based on the requested event type.
	 * 
	 * @param myType - the type of event that the caller wishes to report upon
	 * @return - an Array of Strings containing the event id, question, and moderator that matches the event type
	 */
	public static ArrayList<String> produceReport(EventType myType) {
		ArrayList<String> retVal = new ArrayList<String>();

		try {
			String qry;
			
			if (myType == EventType.OPEN)
				qry  = "SELECT * FROM event where playStatus=0;";
			else if (myType == EventType.CLOSED)
				qry  = "SELECT * FROM event where playStatus=1;";
			else if (myType == EventType.FINISHED)
				qry  = "SELECT * FROM event where playStatus=2;";
			else
				return retVal;
			
			PreparedStatement pstmt = getConnection().prepareStatement(qry);
			ResultSet myRS = pstmt.executeQuery();

			String field;
			while (myRS.next()) {
				field = new String(myRS.getString("id"));
				retVal.add(field);

				field = new String(myRS.getString("question"));
				retVal.add(field);

				//field = new String(myRS.getString("moderator"));
				//retVal.add(field);
			}
			
			return retVal;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return retVal;
	}
	
	
	/**
	 * This method deletes events based on an age and whether or not their current status is finished.  All events on 
	 * this day or before will be deleted
	 * 
	 * @param deleteByDate - the date to be deleted 
	 * @param finished - set to true if you want to delete only finished events.  false will delete any event on or before the deleteByDate
	 * @return the number of records affected or -1 if there was an error
	 */
	public static int deleteEventsByAge(java.util.Date deleteByDate, boolean finished) {
		try {
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
			String qry;
			if (finished)
				qry = "DELETE FROM event where createdDate<=str_to_date(('" + ft.format(deleteByDate) + "'), '%Y-%m-%d') and playStatus=2";
			else
				qry = "DELETE FROM event where createdDate<=str_to_date(('" + ft.format(deleteByDate) + "'), '%Y-%m-%d')";
			
			PreparedStatement pstmt = getConnection().prepareStatement(qry);

			int numRecordsAffected = pstmt.executeUpdate();
			
			return numRecordsAffected;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	
	/**
	 * This method finishes any OPEN or CLOSED DLE based on a specified date.
	 * 
	 * @param finishByDate - the date to finish DLEs by.  All DLEs this age and older will be finished.
	 * @return the number of records affected or -1 if there was an error
	 */
	public static int finishDLEBasedOnDate(java.util.Date finishByDate) {
		try {
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
			String qry;
			qry = "UPDATE event SET playStatus=2 where createdDate<=str_to_date(('" + ft.format(finishByDate) + "'), '%Y-%m-%d')";
			
			PreparedStatement pstmt = getConnection().prepareStatement(qry);
			int numRecordsAffected = pstmt.executeUpdate();
			return numRecordsAffected;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * This method verifies the login and password of a user attempting to access the administrator panel
	 *  
	 * @param adminId - the login name of the administrator
	 * @param credentials - the credentials of the administrator
	 * @return - true if the login-credentials match the database, false otherwise
	 */
	public static boolean verifyAdminCredentials(String adminId, String credentials) {
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
				return false;
				
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return false;
		
	}
}
