package entity;

/**
 * Helper class to enable test cases to silently (and secretly) eliminate instances created by the
 * singleton invocation of {@link Model#getInstance()}.
 * Taken directly from Professor Heineman's ClearModelInstance class from the ClientServerEBC project's Test folder
 */
public class ClearModelInstance {
	
	/**
	 * Singleton elimination. One of the problematic issues regarding a Singleton object occurs 
	 * during testing. Using the {@link #clearInstance()} method, the tester can eliminate a singleton
	 * and "start fresh".
	 * <p>
	 * Avoid using this method during routine development.
	 */
	public static void clearInstance() {  
		Model.thisModel = null;
	}
}
