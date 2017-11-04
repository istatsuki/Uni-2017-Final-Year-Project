package database;

import java.sql.*;

/**
 * <h1> Database Population</h1>
 * The class for populating the test database
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
class TableTest
{
	// Setting databse connection attribute
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost:5000/resttest?useSSL=false";

	// Setting database credentials
	private static final String USER = "root";
	private static final String PASS = "looping";
	
	/**
	 * Contructor method to run the population
	 */
	TableTest()
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
			
			// Create Stock table
			sql = "CREATE TABLE stock(ingredientID VARCHAR(20) PRIMARY KEY, name VARCHAR(100) NOT NULL, amount BigInt NOT NULL, unit VARCHAR(20) NOT NULL)";
			stmt.executeUpdate(sql);
			
			// Populate Stock table
			for(int i = 1; i < 8; i++)
			{
				String ingredientID = "Dessert" + String.valueOf(i);
				populateStock(conn, ingredientID, ingredientID, "unit", 1000L);
			}

			for(int i = 1; i < 8; i++)
			{
				String ingredientID = "Drink" + String.valueOf(i);
				populateStock(conn, ingredientID, ingredientID, "unit", 1000L);
			}

			for(int i = 1; i < 17; i++)
			{
				String ingredientID = "Ing" + String.valueOf(i);
				long amount = 4000000L;
				if(i == 4 || i == 9)
				{
					amount = 0L;
				}
				populateStock(conn, ingredientID, ingredientID, "unit", amount);
			}
			
			// Create Menu table
			sql = "CREATE TABLE menu(menuItemID VARCHAR(20) PRIMARY KEY, name VARCHAR(100) NOT NULL, type VARCHAR(30) NOT NULL, image VARCHAR(200) NOT NULL, ingredientList VARCHAR(500) NOT NULL, availability BOOLEAN DEFAULT 1, problem VARCHAR(200), time BIGINT NOT NULL, price DECIMAL(10,2) NOT NULL, specials VARCHAR(200))";
			stmt.executeUpdate(sql);
			
			// Populate Menu table
			for(int i = 1; i < 9; i++)
			{
				String menuItemID = "Starter" + String.valueOf(i);
				double price = 3 + i * 0.2;
				String ingredientList = "Ing" + String.valueOf(i) + ":100,Ing" + String.valueOf(i + 1) + ":100";
				populateMenu(conn, menuItemID, menuItemID, "starter", "starter.png", ingredientList, true, "", 300, price, "");
			}

			for(int i = 1; i < 15; i++)
			{
				String menuItemID = "Main" + String.valueOf(i);
				double price = 10 + i * 0.4;
				String ingredientList = "Ing" + String.valueOf(i) + ":100,Ing" + String.valueOf(i + 1) + ":100,Ing" + String.valueOf(i + 2) + ":100";
				String special = "";
				boolean availability = true;
				if(i == 4)
				{
					special = "gluten";
					availability = false;
				}
				else if(i == 8)
				{
					special = "vegan";
				}
				populateMenu(conn, menuItemID, menuItemID, "main", "main.png", ingredientList, availability, "", 900, price, special);
			}

			for(int i = 1; i < 8; i++)
			{
				String menuItemID = "Dessert" + String.valueOf(i);
				double price = 4 + i * 0.1;
				String ingredientList = menuItemID + ":1";
				populateMenu(conn, menuItemID, menuItemID, "dessert", "dessert.png", ingredientList, true, "", 300, price, "");
			}

			for(int i = 1; i < 8; i++)
			{
				String menuItemID = "Drink" + String.valueOf(i);
				double price = 5 + i * 0.2;
				String ingredientList = menuItemID + ":1";
				populateMenu(conn, menuItemID, menuItemID, "drink", "drink.png", ingredientList, true, "", 100, price, "");
			}

			// Create Tables table
			sql = "CREATE TABLE tables(tableID VARCHAR(20) PRIMARY KEY, size INT NOT NULL, availability BOOLEAN DEFAULT 1, problem VARCHAR(200))";
			stmt.executeUpdate(sql);
			
			// Populate Tables table
			populateTables(conn, "Table1", 4, false, "Broken");
			populateTables(conn, "Table2", 4, true, "");
			populateTables(conn, "Table3", 4, true, "");
			populateTables(conn, "Table4", 4, true, "");
			populateTables(conn, "Table5", 4, true, "");
			populateTables(conn, "Table6", 6, true, "");
			populateTables(conn, "Table7", 6, false, "Broken");
			populateTables(conn, "Table8", 6, true, "");
			populateTables(conn, "Table9", 8, true, "");
			populateTables(conn, "Table10", 8, true, "");
			populateTables(conn, "Table11", 8, true, "");
			populateTables(conn, "Table12", 8, true, "");

			
			// Create Bookings table
			sql = "CREATE TABLE bookings(bookingID VARCHAR(20) PRIMARY KEY, bookerName VARCHAR(50), contact VARCHAR(200), tablesRequested VARCHAR(40), tableList VARCHAR(100), bookingDate VARCHAR(30) , comment VARCHAR(200), status INT)";
			stmt.executeUpdate(sql);
			
			// Populate Bookings table
			populateBookings(conn, "Booking1", "Bob", "email", "6:1;4:1", "2017-04-28 17:00:00", "", -1);
			populateBookings(conn, "Booking2", "Bob", "email", "4:1;8:1", "2017-04-28 17:00:00", "", -1);
			populateBookings(conn, "Booking3", "Bob", "email", "6:2;4:2", "2017-04-28 17:00:00", "", -1);
			populateBookings(conn, "Booking4", "Bob", "email", "6:1;4:2", "2017-04-28 17:00:00", "", -1);
			populateBookings(conn, "Booking5", "Bob", "email", "6:1", "2017-04-29 17:00:00", "", -1);
			populateBookings(conn, "Booking6", "Bob", "email", "6:1;4:1", "2017-04-29 17:00:00", "", -1);
			populateBookings(conn, "Booking7", "Bob", "email", "6:1;4:1", "2017-04-29 17:00:00", "", -2);
			populateBookings(conn, "Booking8", "Bob", "email", "8:1;4:1", "2017-04-29 17:00:00", "", -2);
			populateBookings(conn, "Booking9", "Bob", "email", "6:2", "2017-04-30 17:00:00", "", -2);
			populateBookings(conn, "Booking10", "Bob", "email", "4:2", "2017-04-30 17:00:00", "", -2);
			populateBookings(conn, "Booking11", "Bob", "email", "8:1", "2017-04-30 17:00:00", "", -1);

			// Create Stations table
			sql = "Create TABLE stations(stationID VARCHAR(20) PRIMARY KEY, name VARCHAR(50) NOT NULL, portNum INT NOT NULL, userName VARCHAR(50), password VARCHAR(50), specialtyList VARCHAR(500) NOT NULL)";
			stmt.executeUpdate(sql);
			
			// Populate Stations table
			populateStations(conn, "Kitchen1", "John", 8300, "starter");
			populateStations(conn, "Kitchen2", "Anna", 8301,"main");
			populateStations(conn, "Kitchen3", "Jack", 8302,"main");
			populateStations(conn, "Kitchen4", "Ceilia", 8303,"dessert");
			populateStations(conn, "Bar1", "Manny", 8304,"drink");
			populateStations(conn, "Bar2", "Sue", 8305,"drink");

			// Create orders table
			sql = "Create TABLE orders(orderID VARCHAR(20), bookingID VARCHAR(20), tableID VARCHAR(20), menuItemID VARCHAR(20), status INT, comment VARCHAR(200), eTP VARCHAR(30))";
			stmt.executeUpdate(sql);

			// Close the resources
			stmt.close();
			conn.close();
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
	
	/**
	 * Method for populating Stock table with a row
	 * @param conn The connection
	 * @param ingredientID For ingredientID value
	 * @param name For name value
	 * @param unit For unit value
	 * @param amount For amount value
	 */
	private void populateStock(Connection conn, String ingredientID, String name, String unit, long amount)
	{
		// Creating the statement
		String sql = "INSERT INTO stock(ingredientID, name, unit, amount)" + "VALUES (?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		
		try
		{
			// Setting the values for the statement
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, ingredientID);
			preparedStmt.setString(2, name);
			preparedStmt.setString(3, unit);
			preparedStmt.setLong(4, amount);
			
			// Execute the statment
			preparedStmt.execute();
			
			// Close the statement
			preparedStmt.close();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				if(preparedStmt != null)
				{
					preparedStmt.close();
				}
			}
			catch (SQLException se)
			{
			}
		}
	}
	
	/**
	 * Method for populating Menu table with a row
	 * @param conn The connection
	 * @param menuItemID For menuItemID value
	 * @param name For name value
	 * @param type For type value
	 * @param image For image value indicating the location of the image file
	 * @param ingredientList For creating a table of the frontOfHouse.menu item holding the recipe
	 * @param availability For availability value
	 * @param problem For problem when the frontOfHouse.menu item is not availability
	 * @param time For time to make the frontOfHouse.menu item
	 * @param price For price value
	 * @param special For special features
	 */
	private void populateMenu(Connection conn, String menuItemID, String name, String type, String image, String ingredientList, boolean availability, String problem, long time, double price, String special)
	{
		// Creating the statement
		String sql = "INSERT INTO menu(menuItemID, name, type, image, ingredientList, availability, problem, time, price, specials)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		
		try
		{
			// Setting the values for the statement
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, menuItemID);
			preparedStmt.setString(2, name);
			preparedStmt.setString(3, type);
			preparedStmt.setString(4, image);
			preparedStmt.setString(5, ingredientList);
			preparedStmt.setBoolean(6, availability);
			preparedStmt.setString(7, problem);
			preparedStmt.setLong(8, time);
			preparedStmt.setDouble(9, price);
			preparedStmt.setString(10, special);
			
			// Execute the statment
			preparedStmt.execute();

			// Close the statment
			preparedStmt.close();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				if(preparedStmt != null)
				{
					preparedStmt.close();
				}
			}
			catch (SQLException se)
			{
			}
		}
	}
	
	/**
	 * Method for populating Tables table with a row
	 * @param conn The connection
	 * @param tableID For tableID value
	 * @param size For size value
	 * @param availability For availability value
	 * @param problem For problem value
	 */
	private void populateTables(Connection conn, String tableID, int size, boolean availability, String problem)
	{
		// Creating the statement
		String sql = "INSERT INTO tables(tableID, size, availability, problem)" + "VALUES (?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		
		try
		{
			// Setting the value for the statement
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, tableID);
			preparedStmt.setInt(2, size);
			preparedStmt.setBoolean(3, availability);
			preparedStmt.setString(4, problem);
			
			// Execute the statement
			preparedStmt.execute();
			
			// Close the statement
			preparedStmt.close();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				if(preparedStmt != null)
				{
					preparedStmt.close();
				}
			}
			catch (SQLException se)
			{
			}
		}
	}
	
	/**
	 * Method for populating the Booking table
	 * @param conn The connection
	 * @param bookingID For bookingID value
	 * @param bookerName For bookerName value
	 * @param contact For contact value
	 * @param tablesRequested For size value indicating both lower and upper boundaries
	 * @param bookingDate For date value
	 * @param comment For comment value indicating any special requirements of assessment from the customers
	 * @param status For status value
	 */
	private void populateBookings(Connection conn, String bookingID, String bookerName, String contact, String tablesRequested, String bookingDate, String comment, int status)
	{
		// Creating the statement
		String sql = "INSERT INTO bookings(bookingID, bookerName, contact, tablesRequested, bookingDate, comment, status )" + "VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		
		try
		{
			// Setting the value for the statement
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, bookingID);
			preparedStmt.setString(2, bookerName);
			preparedStmt.setString(3, contact);
			preparedStmt.setString(4, tablesRequested);
			preparedStmt.setString(5, bookingDate);
			preparedStmt.setString(6, comment);
			preparedStmt.setInt(7, status);
			
			// Execute the statement
			preparedStmt.execute();
			
			// Close the statement
			preparedStmt.close();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				if(preparedStmt != null)
				{
					preparedStmt.close();
				}
			}
			catch (SQLException se)
			{
			}
		}
	}
	
	/**
	 * Method for populating Stations table
	 * @param conn The connection
	 * @param stationID For station ID value
	 * @param name For name value
	 * @param portNum The port number of the station
	 * @param specialtyList For specialties value
	 */
	private void populateStations(Connection conn, String stationID, String name, int portNum, String specialtyList)
	{
		// Creating the statement
		String sql = "INSERT INTO stations(stationID, name, portNum, userName, password, specialtyList)" + "VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		
		try
		{
			// Setting the value for the statement
			preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, stationID);
			preparedStmt.setString(2, name);
			preparedStmt.setInt(3, portNum);
			preparedStmt.setString(4, stationID);
			preparedStmt.setString(5, stationID);
			preparedStmt.setString(6, specialtyList);
			
			// Execute the statement
			preparedStmt.execute();
			
			// Close the statement
			preparedStmt.close();
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				if(preparedStmt != null)
				{
					preparedStmt.close();
				}
			}
			catch (SQLException se)
			{
			}
		}
	}
}
