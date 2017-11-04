package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <h1> Database ReSetting</h1>
 * The class for clearing the database
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
class DatabaseTest
{	
	// Setting databse connection attribute
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost:5000?useSSL=false";

	// Setting database credentials
	private static final String USER = "root";
	private static final String PASS = "looping";
	
	/**
	 * Contructor method to run the resetting
	 * @exception SQLException On sql error
	 * @exception Exception On Class problem
	 */
	DatabaseTest()
	{
		// Initiating the resources
		Connection conn = null;
		Statement stmt = null;
		
		// Try statement for the connection
		try
		{
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			// Initiating more resources
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;

			// Clear the test database
			sql = "DROP DATABASE IF EXISTS RestTest";
			stmt.executeUpdate(sql);

			// Create a new test database
			sql = "CREATE DATABASE IF NOT EXISTS RestTest";
			stmt.executeUpdate(sql);
			
			stmt.close();
			conn.close();
		}		
		catch(SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally
		{
			// Block used to close resources
			try
			{
				if(stmt!=null)
				{
					stmt.close();
				}
			}
			catch(SQLException se)
			{
			}// Nothing we can do

			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(SQLException se2)
			{
				se2.printStackTrace();
			}
		} 
	} 
		
}
