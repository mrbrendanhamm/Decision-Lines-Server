package boundary;
/**
 * @author Team RamRod
 * 
 * Some code taken from Professor's Heineman's ClientServerEBC project, db.Manager.java file
 */


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
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from edge where eventId=(?)");
			pstmt.setString(1, readEvent.getUniqueId());

			ResultSet myRS = pstmt.executeQuery();
			Edge newEdge;
			Choice tmpChoice;
			User selectedUser, tmpUser;
			int height, choiceId, indexOf;
			String tmpUserName;
			
			while (myRS.next()) {
				height = myRS.getInt("height");
				choiceId = myRS.getInt("choiceId");
				tmpUserName = myRS.getString("userName");
				selectedUser = null;
				Iterator<User> myUsers = readEvent.getUsersAndEdges().keySet().iterator();
				
				while (myUsers.hasNext()) {
					tmpUser = myUsers.next();
					
					if (tmpUser.getUser().equals(tmpUserName))
						selectedUser = tmpUser;
				}
				
				if (selectedUser == null) 
					return false; //user not found in list
				
				indexOf = readEvent.getChoices().indexOf(new Choice("", choiceId));
				if (indexOf == -1) //error, choice dictated is not valid
					return false;
				tmpChoice = readEvent.getChoices().get(indexOf);
				
				newEdge = new Edge(tmpChoice, height);
				readEvent.getUsersAndEdges().get(selectedUser).add(newEdge);
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
	public static int writeEdge(Edge writeEdge, String decisionLineId, User byUser) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateEdge(?, ?, ?, ?)");
			pstmt.setString(1, decisionLineId);
			pstmt.setInt(2, writeEdge.getHeight());
			pstmt.setInt(3, writeEdge.getChoice().getOrder());
			pstmt.setString(4, byUser.getUser());

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
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
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateChoice(?, ?, ?)");
			pstmt.setString(1, decisionLineId);
			pstmt.setInt(2, writeChoice.getOrder());
			pstmt.setString(3, writeChoice.getName());

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
	}
	
	/**
	 * Reads in Users for a given DecisionLineEvent.  Must be called before the ReadEdges method
	 * 
	 * @param readEvent
	 * @return
	 */
	public static boolean readUsers(DecisionLineEvent readEvent) { 
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT * from user where eventId=(?)");
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
				readEvent.getUsersAndEdges().put(newUser, new ArrayList<Edge>());
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

			if (!readUsers(newDLE)) 
				return null;

			if (!readEdges(newDLE)) 
				return null;

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
			PreparedStatement pstmt = getConnection().prepareStatement("CALL procUpdateEvent(?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, writeEvent.getUniqueId());
			pstmt.setString(2, writeEvent.getQuestion());
			pstmt.setInt(3, writeEvent.getNumberOfChoice());
			pstmt.setInt(4, writeEvent.getNumberOfEdge());
			if (writeEvent.getBehavior() == Behavior.ASYNCHRONOUS)
				pstmt.setBoolean(5, true);
			else
				pstmt.setBoolean(5, false);
			pstmt.setString(6, writeEvent.getModerator());
			if (writeEvent.getIsFinished())
				pstmt.setInt(7, 2);
			else if (writeEvent.getIsClosed())
				pstmt.setInt(7, 1);
			else
				pstmt.setInt(7, 0);
			int retVal = pstmt.executeUpdate();
			
			if (retVal < 0) 
				return -1; //error
			
			for (int i = 0; i < writeEvent.getChoices().size(); i++) {
				if (writeChoice(writeEvent.getChoices().get(i), writeEvent.getUniqueId()) < 0)
					return -1; //error while writing choices
			}
			
			Iterator<User> myUsers = writeEvent.getUsersAndEdges().keySet().iterator();
			User tmpUser;
			while (myUsers.hasNext()) {
				tmpUser = myUsers.next();
				if (writeUser(tmpUser, writeEvent.getUniqueId()) < 0)
					return -1; //errors while writing users

				ArrayList<Edge> edgeArray = writeEvent.getUsersAndEdges().get(tmpUser);
				for (int i = 0; i < edgeArray.size(); i++) {
					if (writeEdge(edgeArray.get(i), writeEvent.getUniqueId(), tmpUser) < 0)
						return -1; //error while writing edges
				}
			}
			
			return retVal;
		} catch (SQLException e) {
			System.out.println("error executing SQL statement!");
		}
		
		return -1;
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
