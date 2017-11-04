package kABStation;

import resources.Menu;
import resources.MenuItem;
import resources.Client;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Kitchen or Bar Station Instance</h1>
 * The class for representing a kitchen or bar station system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABStationInstance
{
    // menu component
    private Menu menu;

    // The station object
    private KABStation kabStation;

    // The database setting
    private String databaseURL = "jdbc:mysql://127.0.0.1:5000/resttest?useSSL=false";
    private String userName = "root";
    private String password = "looping";

    // The WorkerThread
    private ExecutorService executor;

    // The date laste updated
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    /**
     * The constructor
     * @param stationID The ID of the station
     */
    public KABStationInstance(String stationID)
    {
        // Initiating the resources for the database connection
        Connection conn = null;
        PreparedStatement preparedStmt = null;
        Statement stmt = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(databaseURL, userName, password);

            // Initiating more resources
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            ResultSet rs;

            sql = "SELECT * FROM stations WHERE stationID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, stationID);

            rs = preparedStmt.executeQuery();
            rs.next();

            String stationName = rs.getString("name");
            String specialtyList = rs.getString("specialtyList");
            int portNum = rs.getInt("portNum");

            this.kabStation = new KABStation(stationID, stationName, portNum, specialtyList);

            // Firstly, get the number of the menu item
            sql = "SELECT COUNT(menuItemID) FROM menu";
            rs = stmt.executeQuery(sql);
            rs.next();
            MenuItem[] menuItemList = new MenuItem[rs.getInt(1)];

            // Get all menu items information
            sql = "SELECT * FROM menu";
            rs = stmt.executeQuery(sql);
            int menuItemCount = 0;

            // Creating each of the menu item object
            while (rs.next()) {
                // getting information by the column name
                String menuItemID = rs.getString("menuItemID");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String image = rs.getString("image");
                String ingredientList = rs.getString("ingredientList");
                boolean availability = rs.getBoolean("availability");
                String problem = rs.getString("problem");
                long time = rs.getLong("time");
                double price = rs.getDouble("price");
                String specials = rs.getString("specials");

                menuItemList[menuItemCount] = new MenuItem(menuItemID, name, type, image, ingredientList, availability, problem, time, price, specials);
                menuItemCount++;
            }

            sql = "SELECT update_time FROM information_schema.tables WHERE  table_schema = 'resttest' AND table_name = 'menu'";
            rs = stmt.executeQuery(sql);
            rs.next();
            Timestamp lastUpdated = rs.getTimestamp(1);

            // Create the menu components
            this.menu = new Menu(menuItemList, lastUpdated);

            // Closing the resources
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception e)
        {
            // Handle errors for Class.forName
            e.printStackTrace();
            System.exit(0);
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
            catch(SQLException se2)
            {
            }// Nothing we can do

            try
            {
                if(conn!=null)
                {
                    conn.close();
                }
            }
            catch(SQLException se)
            {
                se.printStackTrace();
            }
        }

        // Creating ThreadPool for handling requests from the other sub-systems
        executor = Executors.newFixedThreadPool(5);
    }

    /**
     * Method for getting the executor
     * @return ExecutorService The executor
     */
    public ExecutorService getExecutor()
    {
        return this.executor;
    }

    /**
     * Method for getting the list of station
     * @return Station[] The list of stations
     */
    public KABStation getStation()
    {
        return this.kabStation;
    }

    /**
     * Method to get the menu
     * @return Menu The menu
     */
    public Menu getMenu()
    {
        return this.menu;
    }

    /**
     * Method to get the time last updated
     * @return String the time last updated
     */
    public String getTimeUpdated()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.timeUpdated);
    }

    /**
     * Method for creating new order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param menuItemID The ID of the menu item
     * @param comment The comment value
     * @param status The status
     */
    public void newOrder(String orderID, String bookingID, String menuItemID, String comment, String status)
    {
        this.kabStation.getOrderList().add(new KABStationOrder(orderID, bookingID, menuItemID, comment, status));
        calculateOrderETP();
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for removing an order
     * @param orderID The ID of the order
     * @param bookingID THe ID of the booking
     */
    public void removeOrder(String orderID, String bookingID)
    {
        ArrayList<KABStationOrder> orderList = kabStation.getOrderList();

        boolean contain = false;

        for(int i = 0; i < orderList.size() && ! contain; i++)
        {
            if(orderList.get(i).getOrderID().equals(orderID) && orderList.get(i).getBookingID().equals(bookingID))
            {
                kabStation.getOrderList().remove(i);
                contain = true;
            }
        }
        calculateOrderETP();
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method to calculate the estimated time done of the order
     */
    private void calculateOrderETP()
    {
        this.kabStation.reArrangeOrderList();
        ArrayList<KABStationOrder> orderList = this.kabStation.getOrderList();
        for(int i = 0; i < orderList.size(); i++)
        {
            long time = 0L;
            for(int k = 0; k <= i; k++)
            {
                time += menu.getMenuItem(orderList.get(k).getMenuItemID()).getTime();
            }
            orderList.get(i).setETP(time);

            // notify the other systems
            Client.clientToKAB("ChangeOrderETP/" + orderList.get(i).getOrderID() + Client.infoSeperator + orderList.get(i).getBookingID() + Client.infoSeperator + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderList.get(i).getETP()));
            Client.clientToFOH("ChangeOrderETP/" + orderList.get(i).getOrderID() + Client.infoSeperator + orderList.get(i).getBookingID() + Client.infoSeperator + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderList.get(i).getETP()));
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing the status of an order
     * @param orderID For orderID value
     * @param bookingID For bookingID value
     * @param status For status value
     * @param notifyKAB Whether or not to notify the kitchen and bar
     */
    public void changeOrderStatus(String orderID, String bookingID, int status, boolean notifyKAB)
    {
        if(this.kabStation.getOrder(orderID, bookingID) != null)
        {
            this.kabStation.getOrder(orderID, bookingID).setStatus(status);

            if(status == KABStationOrder.ERROR || status == KABStationOrder.CANCELLED || status == KABStationOrder.REFUSED || status == KABStationOrder.PREPARED || status == KABStationOrder.PRESENTED)
            {
                removeOrder(orderID, bookingID);
            }

            if (notifyKAB)
            {
                Client.clientToKAB("ChangeOrderStatus/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + status + Client.infoSeperator + "true" + Client.infoSeperator + "false");
            }
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing the comment of an order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param comment The comment of the order
     */
    public void changeOrderComment(String orderID, String bookingID, String comment)
    {
        this.kabStation.getOrder(orderID, bookingID).setComment(comment);
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

}
