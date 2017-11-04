package kitchenAndBar;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import resources.Menu;
import resources.MenuItem;
import resources.Client;
import resources.Pair;

/**
 * <h1> Kitchen and Bar</h1>
 * The class for managing all the working station of kitchen and bar
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KitchenAndBar
{
    // Singleton setting
    private static KitchenAndBar kitchenAndBar = new KitchenAndBar();
    public static KitchenAndBar instance()
    {
        return kitchenAndBar;
    }

    // The database setting
    private String databaseURL = "jdbc:mysql://127.0.0.1:5000/resttest?useSSL=false";
    private String userName = "root";
    private String password = "looping";

    // menu component
    private Menu menu;

    // The lists
    private Station[] stationList;
    private ArrayList<Pair<KABOrder, String>> orderList;

    // The WorkerThread
    private ExecutorService executor;

    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    /**
     * Constructor
     */
    private KitchenAndBar()
    {
        // Create the lists
        this.orderList = new ArrayList<Pair<KABOrder,String>>();

        // Initiating the resources for the database connection
        Connection conn = null;
        PreparedStatement preparedStmt = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(databaseURL, userName, password);

            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            ResultSet rs;

            // Creating the Kitchen and Bar system using the stations Table from the database
            sql = "SELECT COUNT(stationID) FROM stations";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            rs.next();

            // Firstly, get the number of the stations
            Station[] stationList = new Station[rs.getInt(1)];

            // Get all statinos information
            sql = "SELECT * FROM stations";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            int stationCount = 0;

            // Creating each of the station object
            while (rs.next()) {
                // getting information by the column name
                String stationID = rs.getString("stationID");
                String name = rs.getString("name");
                int portNum = rs.getInt("portNum");
                String specialtyList = rs.getString("specialtyList");

                stationList[stationCount] = new Station(stationID, name, portNum, specialtyList);
                stationCount++;
            }

            this.stationList = stationList;

            // Firstly, get the number of the menu item
            sql = "SELECT COUNT(menuItemID) FROM menu";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            rs.next();
            MenuItem[] menuItemList = new MenuItem[rs.getInt(1)];

            // Get all menu items information
            sql = "SELECT * FROM menu";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
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
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            rs.next();
            Timestamp lastUpdated = rs.getTimestamp(1);

            // Create the menu components
            this.menu = new Menu(menuItemList, lastUpdated);

            // Get the orders
            sql = "SELECT * FROM orders WHERE status > ? && status != ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setInt(1, KABOrder.REFUSED);
            preparedStmt.setInt(2, KABOrder.PRESENTED);

            rs = preparedStmt.executeQuery();

            while(rs.next())
            {
                String orderID = rs.getString("orderID");
                String bookingID = rs.getString("bookingID");
                String tableID = rs.getString("tableID");
                String menuItemID = rs.getString("menuItemID");
                String comment = rs.getString("comment");
                int status = rs.getInt("status");
                String eTP_ex = rs.getString("eTP");
                if(!eTP_ex.equals("") && eTP_ex != null)
                {
                    Timestamp eTP = Timestamp.valueOf(eTP_ex);
                    orderList.add(new Pair<>(new KABOrder(orderID, bookingID, tableID, menuItemID, comment, status, eTP), ""));
                }
                else
                {
                    orderList.add(new Pair<>(new KABOrder(orderID, bookingID, tableID, menuItemID, comment, status), ""));
                }
            }

            // Closing the resources
            rs.close();
            preparedStmt.close();
            conn.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        finally
        {
            // Block used to close resources

            try
            {
                if(preparedStmt!=null)
                {
                    preparedStmt.close();
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
     * Method for getting the list of order item
     * @return ArrayList<Pair<OrderItem,String>> the order item list
     */
    public ArrayList<Pair<KABOrder,String>> getOrderList()
    {
        return this.orderList;
    }

    /**
     * Method for getting the list of station
     * @return Station[] The list of stations
     */
    public Station[] getStationList()
    {
        return this.stationList;
    }

    /**
     * Method for getting an order
     * @param orderID the ID of the order
     * @return OrderItem The order
     */
    public Pair<KABOrder, String> getOrder(String orderID, String bookingID)
    {
        for(int i = 0; i < orderList.size(); i++)
        {
            if(orderList.get(i).getFirst().getOrderID().equals(orderID) && orderList.get(i).getFirst().getBookingID().equals(bookingID))
            {
                return orderList.get(i);
            }
        }
        return null;
    }

    /**
     * Method for getting a station
     * @param stationID The ID of the station
     * @return Station The station
     */
    public Station getStation(String stationID)
    {
        for(int i = 0; i < stationList.length; i ++)
        {
            if(stationList[i].getStationID().equals(stationID))
            {
                return stationList[i];
            }
        }
        return null;
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
     * Method for getting the time last updated
     * @return String the time last updated
     */
    public String getTimeUpdated()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.timeUpdated);
    }

    /**
     * Method for changing an order status
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param status The status
     * @param notifyFOH To notify FOH or not
     * @param notifyStation To notify the station or not
     */
    public void changeOrderStatus(String orderID, String bookingID, int status, boolean notifyFOH, boolean notifyStation)
    {
        // Free ingredients
        if(getOrder(orderID, bookingID) != null)
        {
            if(status == KABOrder.CANCELLED || status == KABOrder.REFUSED || status == KABOrder.ERROR)
            {
                if(getOrder(orderID, bookingID).getFirst().getStatus() != KABOrder.SHORTAGE)
                {
                    // Send requests to make changes
                    Client.clientToStock("MoreIngredient/" + menu.getMenuItem(getOrder(orderID, bookingID).getFirst().getMenuItemID()).getIngredientList());
                }
            }

            getOrder(orderID, bookingID).getFirst().setStatus(status);

            if (notifyFOH) {
                // Send requests to make changes
                String outputText1 = "ChangeOrderStatus/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + String.valueOf(status) + Client.infoSeperator + "false";
                Client.clientToFOH(outputText1);
            }

            if (notifyStation && !getOrder(orderID, bookingID).getSecond().equals("")) {
                // Send requests to make changes
                Client.clientToStation("ChangeOrderStatus/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + String.valueOf(status) + Client.infoSeperator + "false", getOrder(orderID, bookingID).getSecond(), getStation(getOrder(orderID, bookingID).getSecond()).getPortNum());
            }

            if(status == KABOrder.ERROR || status == KABOrder.CANCELLED || status == KABOrder.REFUSED || status == KABOrder.PRESENTED)
            {
                boolean found = false;
                for(int i = 0; i < orderList.size() && !found; i++)
                {
                    if(orderList.get(i).getFirst().getOrderID().equals(orderID) && orderList.get(i).getFirst().getBookingID().equals(bookingID))
                    {
                        orderList.remove(i);
                    }
                }
            }

            timeUpdated = new Timestamp(System.currentTimeMillis());
        }
    }

    /**
     * Method for creating new order
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     * @param tableID The tableID value
     * @param menuItemID The menuItemID value
     * @param comment The comment
     */
    public void newOrder(String orderID, String bookingID, String tableID, String menuItemID, String comment)
    {
        getOrderList().add(new Pair<KABOrder, String>(new KABOrder(orderID, bookingID, tableID, menuItemID, comment), ""));
        checkIngredient(orderID, bookingID);
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for checking the ingredients of an orders
     * @param orderID The orderID
     * @param bookingID The bookingID
     */
    public void checkIngredient(String orderID, String bookingID)
    {
        String menuItemID = getOrder(orderID, bookingID).getFirst().getMenuItemID();

        // Send requests
        String outputText = "CheckIngredient/" + menu.getMenuItem(menuItemID).getIngredientList();

        String reply = Client.clientToStock(outputText);

        if(reply.contains("Not Enough Ingredient") && getOrder(orderID, bookingID).getFirst().getStatus() != KABOrder.SHORTAGE)
        {
            changeOrderStatus(orderID, bookingID, KABOrder.SHORTAGE, true, true);

            menu.getMenuItem(menuItemID).setAvailability(false);
            menu.getMenuItem(menuItemID).setProblem("Shortage of ingredient");
        }
        else if(getOrder(orderID, bookingID).getFirst().getStatus() == KABOrder.SHORTAGE && reply.contains("Enough Ingredient"))
        {
            changeOrderStatus(orderID, bookingID, KABOrder.PLACED, true, true);

            menu.getMenuItem(menuItemID).setAvailability(true);
            menu.getMenuItem(menuItemID).setProblem("");
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing an order table
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     * @param tableID The ID of the table to change to
     */
    public void changeOrderTable(String orderID, String bookingID, String tableID)
    {
        getOrder(orderID, bookingID).getFirst().setTableID(tableID);
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing an order comment
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     * @param comment The comment
     */
    public void changeOrderComment(String orderID, String bookingID, String comment)
    {
        getOrder(orderID, bookingID).getFirst().setComment(comment);
        if(!getOrder(orderID, bookingID).getSecond().equals(""))
        {
            // Send requests to make changes
            Client.clientToStation("ChangeOrderComment/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + comment, getOrder(orderID, bookingID).getSecond(), getStation(getOrder(orderID, bookingID).getSecond()).getPortNum());
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing an order station
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     * @param stationID The ID of the station to change to
     */
    public void changeOrderStation(String orderID, String bookingID, String stationID)
    {
        // Notify the current station to free the order
        if(!getOrder(orderID, bookingID).getSecond().equals(""))
        {
            // Send requests to make changes
            Client.clientToStation("RemoveOrder/" + orderID + Client.infoSeperator + bookingID, getOrder(orderID, bookingID).getSecond(), getStation(getOrder(orderID, bookingID).getSecond()).getPortNum());
        }

        // Send requests to make changes
        getOrder(orderID, bookingID).setSecond(stationID);
        Client.clientToStation("NewOrder/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + getOrder(orderID, bookingID).getFirst().getMenuItemID() + Client.infoSeperator + getOrder(orderID, bookingID).getFirst().getComment() + Client.infoSeperator + String.valueOf(getOrder(orderID, bookingID).getFirst().getStatus()), stationID, getStation(stationID).getPortNum());
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing an order ETP
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     * @param eTP THe eTP value
     */
    public void changeOrderETP(String orderID, String bookingID, String eTP)
    {
        getOrder(orderID, bookingID).getFirst().setETP(Timestamp.valueOf(eTP));
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing an online status of a station
     * @param stationID The stationID value
     * @param onlineStatus The online status
     * @return boolean if it is possible to change
     */
    public boolean changeStationOnlineStatus(String stationID, boolean onlineStatus)
    {
        for(int i = 0; i < stationList.length; i++)
        {
            if(stationList[i].getStationID().equals(stationID))
            {
                if(onlineStatus && stationList[i].getOnlineStatus())
                {
                    return false;
                }
                else
                {
                    stationList[i].setOnlineStatus(onlineStatus);
                    if(!onlineStatus)
                    {
                        for(int k = 0; k < orderList.size(); k++)
                        {
                            if(orderList.get(k).getSecond().equals(stationID))
                            {
                                orderList.get(k).setSecond("");
                            }
                        }
                    }
                    timeUpdated = new Timestamp(System.currentTimeMillis());
                    return true;
                }
            }
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
        return false;
    }

    /**
     * Method for rechecking the menu with changes to an ingredient
     * @param ingredientID The Id of the ingredient changed
     */
    public void checkMenu(String ingredientID)
    {
        MenuItem[] menuItemList = this.menu.getMenuItemList();
        for(int i = 0; i < menuItemList.length; i++)
        {
            if(!menuItemList[i].getAvailability())
            {
                if(menuItemList[i].getIngredientList().contains(ingredientID))
                {
                    // Send requests
                    String reply = Client.clientToStock("CheckMenu/" + menuItemList[i].getIngredientList());

                    if (reply.contains("Enough Ingredient")) {
                        this.menu.getMenuItemList()[i].setAvailability(true);
                        this.menu.getMenuItemList()[i].setProblem("");
                        // Send requests to make changes
                        Client.clientToFOH("MenuItemAvailable/" + menuItemList[i].getMenuItemID());
                    }
                }
            }
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for recording a failure
     * @param orderID The orderID value
     * @param bookingID The bookingID value
     */
    public void failure(String orderID, String bookingID)
    {
        checkIngredient(orderID, bookingID);
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }
}
