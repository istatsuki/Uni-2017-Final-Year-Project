package database;

/**
 * <h1> MYSQL database creation</h1>
 * The class for creating the initial database
 * Currently with some test data
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class DatabaseCreation
{
	/**
	 * Just the main function for running
	 * @param args This is the argument, there shouldn't be one
	 */
	public static void main(String[] args)
	{
		// For wiping and creating new database for testing
		DatabaseTest preTest = new DatabaseTest();
		
		// Populating the database for testing
		TableTest test = new TableTest();
	}
}
