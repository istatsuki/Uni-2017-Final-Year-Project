package frontOfHouse;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import resources.Menu;
import resources.MenuItem;
import resources.Client;
import resources.Pair;
import resources.StringConversion;

/**
 * <h1> Front Of House</h1>
 * The class for managing all front-of-house operations
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FrontOfHouse
{
    // Singleton settings
    private static FrontOfHouse frontOfHouse = new FrontOfHouse();
    public static FrontOfHouse instance()
    {
        return frontOfHouse;
    }

    // The database setting
    private String databaseURL = "jdbc:mysql://127.0.0.1:5000/resttest?useSSL=false";
    private String userName = "root";
    private String password = "looping";
    private Connection conn;

    // The lists
    private ArrayList<Booking> bookingList;
    private Table[] tableList;

    // The menu
    private Menu menu;

    // The WorkerThread
    private ExecutorService executor;

    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    /**
     * Constructor
     */
    private FrontOfHouse()
    {
        // Initiating the lists
        this.bookingList = new ArrayList<Booking>();
        this.tableList = null;

        // Try statement for the connection
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(databaseURL, userName, password);

            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;
            ResultSet rs;

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

            // Firstly, get the number of the table
            sql = "SELECT COUNT(*) FROM tables";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            rs.next();
            this.tableList = new Table[rs.getInt(1)];

            // Get all tables information
            sql = "SELECT * FROM tables";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            int tableCount = 0;

            // Creating each of the table object
            while (rs.next()) {
                // getting information by the column name
                String tableID = rs.getString("tableID");
                int size = rs.getInt("size");
                boolean availability = rs.getBoolean("availability");
                String problem = rs.getString("problem");

                this.tableList[tableCount] = new Table(tableID, size, availability, problem);
                tableCount++;
            }

            // Get all booking information
            sql = "SELECT * FROM bookings WHERE DATE(bookingDate) >= DATE(CURDATE())";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();

            // Creating each of the booking object
            while (rs.next())
            {
                // getting the information by the column name
                String bookingID = rs.getString("bookingID");
                String bookerName = rs.getString("bookerName");
                String contact = rs.getString("contact");
                String tablesRequested = rs.getString("tablesRequested");
                Timestamp date = Timestamp.valueOf(rs.getString("bookingDate"));
                String comment = rs.getString("comment");
                int status = rs.getInt("status");

                this.bookingList.add(new Booking(bookingID, bookerName, contact, tablesRequested, date, comment, status));

                String tableList_ex = rs.getString("tableList");
                if(tableList_ex != null && !tableList_ex.equals(""))
                {
                    ArrayList<String> tableList = StringConversion.stringToArrayList(tableList_ex);
                    for (int i = 0; i < tableList.size(); i++) {
                        getBooking(bookingID).getTableList().add(tableList.get(i));
                        getTable(tableList.get(i)).setAvailability(false);
                    }
                }
            }

            // Get the orders for the booking if there are
            for(int i = 0; i < bookingList.size(); i++)
            {
                if(bookingList.get(i).getStatus() != Booking.BOOKED || bookingList.get(i).getStatus() != Booking.CANCELLED)
                {
                    sql = "SELECT * FROM orders WHERE bookingID = ?";
                    preparedStmt = conn.prepareStatement(sql);
                    preparedStmt.setString(1, bookingList.get(i).getBookingID());
                    rs = preparedStmt.executeQuery();

                    while (rs.next())
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
                            bookingList.get(i).getOrderList().add(new FOHOrder(orderID, bookingID, tableID, menuItemID, comment, status, eTP));
                        }
                        else
                        {
                            bookingList.get(i).getOrderList().add(new FOHOrder(orderID, bookingID, tableID, menuItemID, comment, status));
                        }
                    }
                }
            }

            // Creating ThreadPool for handling requests from the other sub-systems
            executor = Executors.newFixedThreadPool(6);



            // Closing the resources
            rs.close();
            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method for getting the list of bookings
     * @return ArrayList<Booking> The list of booking
     */
    public ArrayList<Booking> getBookingList()
    {
        return this.bookingList;
    }

    /**
     * Method for getting the list of tables
     * @return Table[] The list of tables
     */
    public Table[] getTableList()
    {
        return this.tableList;
    }

    /**
     * Method for getting the menu
     * @return menu The menu
     */
    public Menu getMenu()
    {
        return this.menu;
    }

    /**
     * Method for getting the executor
     * @return executor The executor
     */
    public ExecutorService getExecutor()
    {
        return this.executor;
    }

    /**
     * Method for getting the alert booking number
     */
    public int getAlertNum()
    {
        int alertCount = 0;
        for(int i = 0; i < bookingList.size(); i++)
        {
            if(bookingList.get(i).getStatus() == Booking.BOOKED)
            {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long timeDifference = Math.abs(timestamp.getTime() - bookingList.get(i).getDate().getTime());
                if(timeDifference < 60 * 60 * 1000)
                {
                    alertCount++;
                }
            }
        }
        return alertCount;
    }

    /**
     * Method for getting the booking with the booking ID associated
     * @param bookingID The booking ID
     * @return Booking The booking
     */
    public Booking getBooking(String bookingID)
    {
        for(int i = 0; i < this.bookingList.size(); i++)
        {
            if(this.bookingList.get(i).getBookingID().equals(bookingID))
            {
                return this.bookingList.get(i);
            }
        }
        return null;
    }

    /**
     * Method for getting the table
     * @param tableID The ID of the table
     * @return
     */
    public Table getTable(String tableID)
    {
        for(int i = 0; i < this.tableList.length; i++)
        {
            if(this.tableList[i].getTableID().equals(tableID))
            {
                return this.tableList[i];
            }
        }
        return null;
    }

    /**
     * Method for creating a new booking
     * @param bookerName For booker name value
     * @param contact For contact value
     * @param tablesRequested For table requested value
     * @param date For date value
     * @param comment For comment value
     */
    public void newBooking(String bookerName, String contact, String tablesRequested, String date, String comment)
    {
        // Create the booking ID using the current date
        timeUpdated = new Timestamp(System.currentTimeMillis());
        long timestamp = timeUpdated.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        String bookingID = String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf(cal.get(Calendar.MONTH)) + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

        int count = 0;
        for(int i = 0; i < bookingList.size(); i++)
        {
            if(bookingList.get(i).getBookingID().contains(bookingID))
            {
                count++;
            }
        }

        bookingID = bookingID + "$" + String.valueOf(count);
        bookingList.add(new Booking(bookingID, bookerName, contact, tablesRequested, Timestamp.valueOf(date), comment, Booking.BOOKED));

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "INSERT INTO bookings(bookingID, bookerName, contact, tablesRequested, bookingDate, comment, status)" + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            // Setting the value for the statement
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, bookingID);
            preparedStmt.setString(2, bookerName);
            preparedStmt.setString(3, contact);
            preparedStmt.setString(4, tablesRequested);
            preparedStmt.setString(5, date);
            preparedStmt.setString(6, comment);
            preparedStmt.setInt(7, Booking.BOOKED);

            // Execute the statement
            preparedStmt.execute();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method for getting the time updated
     * @return String the time updated
     */
    public String getTimeUpdated()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.timeUpdated);
    }

    /**
     * Method for changing a booking status
     * @param bookingID The ID of the booking
     * @param status The status to be changed into
     */
    public void changeBookingStatus(String bookingID, int status)
    {
        getBooking(bookingID).setStatus(status);
        // If the status is billed, get the bill frame
        if(status == Booking.BILLED)
        {
            ArrayList<String> bookingTableList = getBooking(bookingID).getTableList();
            for(int i = 0; i < bookingTableList.size(); i++)
            {
                getTable(bookingTableList.get(i)).setAvailability(true);
            }
            BillFrame billFrame = new BillFrame(getBooking(bookingID).getOrderList());
        }

        if(status == Booking.PLACED)
        {
            boolean finished = true;
            ArrayList<FOHOrder> orderList = getBooking(bookingID).getOrderList();

            for (int i = 0; i < orderList.size() && finished; i++) {
                FOHOrder order = orderList.get(i);
                if (order.getStatus() != FOHOrder.ERROR && order.getStatus() != FOHOrder.CANCELLED && order.getStatus() != FOHOrder.REFUSED && order.getStatus() != FOHOrder.PRESENTED) {
                    finished = false;
                }
            }

            if (finished) {
                changeBookingStatus(bookingID, Booking.FINISHED);
            }
        }

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE bookings SET status = ? WHERE bookingID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setInt(1, status);
            preparedStmt.setString(2, bookingID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for adding a table to a booking
     * @param bookingID The ID of the booking
     * @param tableID The ID of the table
     * @return If it is possible
     */
    public boolean addBookingTable(String bookingID, String tableID)
    {
        if(getTable(tableID).getAvailability())
        {
            getBooking(bookingID).getTableList().add(tableID);
            changeTableAvailable(tableID, false, "occupied");
            timeUpdated = new Timestamp(System.currentTimeMillis());

            // update database information
            try {
                // Initiating more resources
                System.out.println("Creating statement...");
                String sql;
                PreparedStatement preparedStmt;

                sql = "UPDATE bookings SET tableList = ? WHERE bookingID = ?";
                preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setString(1, StringConversion.arrayListToString(getBooking(bookingID).getTableList()));
                preparedStmt.setString(2, bookingID);

                preparedStmt.executeUpdate();

                preparedStmt.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Method for creating new order
     * @param bookingID The ID of the booking
     * @param tableID The ID of the table
     * @param menuItemID The ID of the dish
     * @param comment The comment
     */
    public void newOrder(String bookingID, String tableID, String menuItemID, String comment)
    {
        String orderID = "Order" + String.valueOf(getBooking(bookingID).getOrderList().size() + 1);
        getBooking(bookingID).getOrderList().add(new FOHOrder(orderID, bookingID, tableID, menuItemID, comment));

        if(getBooking(bookingID).getStatus() == Booking.FINISHED)
        {
            changeBookingStatus(bookingID, Booking.PLACED);
        }

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "INSERT INTO orders(orderID, bookingID, tableID, menuItemID, status, comment, eTP)" + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            // Setting the value for the statement
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, orderID);
            preparedStmt.setString(2, bookingID);
            preparedStmt.setString(3, tableID);
            preparedStmt.setString(4, menuItemID);
            preparedStmt.setInt(5, FOHOrder.PLACED);
            preparedStmt.setString(6, comment);
            preparedStmt.setString(7, "");

            // Execute the statement
            preparedStmt.execute();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Notify the other system
        String outputText = "NewOrder/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + tableID + Client.infoSeperator + menuItemID + Client.infoSeperator + comment;
        Client.clientToKAB(outputText);
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing the status of an order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param status The status
     * @param notifyKAB to notify kitchen and bar or not
     */
    public void changeOrderStatus(String orderID, String bookingID, int status, boolean notifyKAB)
    {
        if(getBooking(bookingID).getOrder(orderID) != null)
        {
            getBooking(bookingID).getOrder(orderID).setStatus(status);

            // check if the booking is finished
            boolean finished = true;
            ArrayList<FOHOrder> orderList = getBooking(bookingID).getOrderList();
            for (int i = 0; i < orderList.size() && finished; i++) {
                FOHOrder order = orderList.get(i);
                if (order.getStatus() != FOHOrder.ERROR && order.getStatus() != FOHOrder.CANCELLED && order.getStatus() != FOHOrder.REFUSED && order.getStatus() != FOHOrder.PRESENTED) {
                    finished = false;
                }
            }
            if (finished) {
                changeBookingStatus(bookingID, Booking.FINISHED);
            }

            // check if the menu item is no longer available
            if (status == FOHOrder.SHORTAGE) {
                String menuItemID = getBooking(bookingID).getOrder(orderID).getMenuItemID();
                changeMenuItemAvailable(menuItemID, false);
            }

            // notify kitchen and bar if required
            if (notifyKAB) {
                Client.clientToKAB("ChangeOrderStatus/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + status + Client.infoSeperator + "false" + Client.infoSeperator + "true");
            }

            // update database information
            try {
                // Initiating more resources
                System.out.println("Creating statement...");
                String sql;
                PreparedStatement preparedStmt;

                sql = "UPDATE orders SET status = ? WHERE orderID = ? && bookingID = ?";
                preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setInt(1, status);
                preparedStmt.setString(2, orderID);
                preparedStmt.setString(3, bookingID);

                preparedStmt.executeUpdate();

                preparedStmt.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            timeUpdated = new Timestamp(System.currentTimeMillis());
        }
    }

    /**
     * Method for changing the table of a booking
     * @param bookingID The ID of the booking
     * @param tableID1 The current table
     * @param tableID2 The table to changed into
     * @return is is possible
     */
    public boolean changeBookingTable(String bookingID, String tableID1, String tableID2)
    {
        if(getTable(tableID2).getAvailability())
        {
            ArrayList<String> bookingTableList = getBooking(bookingID).getTableList();
            boolean found = false;
            for (int i = 0; i < bookingTableList.size() && !found; i++) {
                if (bookingTableList.get(i).equals(tableID1)) {
                    getBooking(bookingID).getTableList().remove(i);
                    found = true;
                }
            }

            if (found) {
                getBooking(bookingID).getTableList().add(tableID2);
                changeTableAvailable(tableID1, true, "");
                changeTableAvailable(tableID2, false, "occupied");

                ArrayList<FOHOrder> orderList = getBooking(bookingID).getOrderList();

                for (int i = 0; i < orderList.size(); i++) {
                    if (orderList.get(i).getTableID().equals(tableID1)) {
                        changeOrderTable(orderList.get(i).getOrderID(), bookingID, tableID2);
                    }
                }

                // update database information
                try {
                    // Initiating more resources
                    System.out.println("Creating statement...");
                    String sql;
                    PreparedStatement preparedStmt;

                    sql = "UPDATE bookings SET tableList = ? WHERE bookingID = ?";
                    preparedStmt = conn.prepareStatement(sql);
                    preparedStmt.setString(1, StringConversion.arrayListToString(getBooking(bookingID).getTableList()));
                    preparedStmt.setString(2, bookingID);

                    preparedStmt.executeUpdate();

                    for (int i = 0; i < orderList.size(); i++) {
                        if (orderList.get(i).getTableID().equals(tableID1)) {
                            sql = "UPDATE orders SET tableID = ? WHERE orderID = ? && bookingID = ?";
                            preparedStmt = conn.prepareStatement(sql);
                            preparedStmt.setString(1, tableID2);
                            preparedStmt.setString(2, orderList.get(i).getOrderID());
                            preparedStmt.setString(3, bookingID);

                            preparedStmt.executeUpdate();
                        }
                    }

                    preparedStmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                timeUpdated = new Timestamp(System.currentTimeMillis());
                return true;
            }
            else
            {
                timeUpdated = new Timestamp(System.currentTimeMillis());
                return false;
            }
        }
        else
        {
            timeUpdated = new Timestamp(System.currentTimeMillis());
            return false;
        }
    }

    /**
     * Method for change the table of an order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param tableID The ID of the table to change to
     */
    public void changeOrderTable(String orderID, String bookingID, String tableID)
    {
        getBooking(bookingID).getOrder(orderID).setTableID(tableID);
        // notify kitchen and bar
        Client.clientToKAB("ChangeOrderTable/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + tableID);

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE orders SET tableID = ? WHERE orderID = ? && bookingID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, tableID);
            preparedStmt.setString(2, orderID);
            preparedStmt.setString(3, bookingID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for change the comment of an order
     * @param orderID The ID or the order
     * @param bookingID The ID of the booking
     * @param comment THe comment
     */
    public void changeOrderComment(String orderID, String bookingID, String comment)
    {
        getBooking(bookingID).getOrder(orderID).setComment(comment);
        // notify kitchen and bar
        Client.clientToKAB("ChangeOrderComment/" + orderID + Client.infoSeperator + bookingID + Client.infoSeperator + comment);

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE orders SET comment = ? WHERE orderID = ? && bookingID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, comment);
            preparedStmt.setString(2, orderID);
            preparedStmt.setString(3, bookingID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for removing a table from a booking
     * @param bookingID The ID of the booking
     * @param tableID1 The ID of the current table
     * @param tableID2 The ID of the table to send order to
     * @param keepAll Whether or not to keep all orders
     */
    public void removeBookingTable(String bookingID, String tableID1, String tableID2, boolean keepAll)
    {
        ArrayList<String> bookingTableList = getBooking(bookingID).getTableList();
        boolean found = false;
        for(int i = 0; i < bookingTableList.size() && !found; i++)
        {
            if(bookingTableList.get(i).equals(tableID1))
            {
                getBooking(bookingID).getTableList().remove(i);
                changeTableAvailable(tableID1, true, "");
                found = true;
            }
        }

        ArrayList<FOHOrder> orderList = getBooking(bookingID).getOrderList();

        for(int i = 0; i < orderList.size(); i++)
        {
            FOHOrder order = orderList.get(i);
            if(order.getTableID().equals(tableID1))
            {
                if(!keepAll)
                {
                    if(order.getStatus() == FOHOrder.HIGHPRIORITY || order.getStatus() == FOHOrder.PLACED || order.getStatus() == FOHOrder.SHORTAGE)
                    {
                        changeOrderStatus(order.getOrderID(), bookingID, FOHOrder.CANCELLED, true);
                    }
                        changeOrderTable(order.getOrderID(), bookingID, tableID2);
                }
                else
                    {
                    changeOrderTable(order.getOrderID(), bookingID, tableID2);
                }
            }
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for change the ETP of an order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @param eTP The ETP of the order
     */
    public void changeOrderETP(String orderID, String bookingID, String eTP)
    {
        getBooking(bookingID).getOrder(orderID).setETP(Timestamp.valueOf(eTP));

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE orders SET eTP = ? WHERE orderID = ? && bookingID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, eTP);
            preparedStmt.setString(2, orderID);
            preparedStmt.setString(3, bookingID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for changing the menu item availability
     * @param menuItemID The ID of the menu item
     * @param available The availability
     */
    public void changeMenuItemAvailable(String menuItemID, boolean available)
    {
        this.menu.getMenuItem(menuItemID).setAvailability(available);
        if(!available)
        {
            this.menu.getMenuItem(menuItemID).setProblem("Shortage of ingredient");
        }
        else
        {
            this.menu.getMenuItem(menuItemID).setProblem("");
        }

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE menu SET availability = ?, problem = ? WHERE menuItemID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setBoolean(1, available);
            preparedStmt.setString(2, this.menu.getMenuItem(menuItemID).getProblem());
            preparedStmt.setString(3, menuItemID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    public void changeTableAvailable(String tableID, boolean available, String problem)
    {
        getTable(tableID).setAvailability(available);
        getTable(tableID).setProblem(problem);

        // update database information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE tables SET availability = ?, problem = ? WHERE tableID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setBoolean(1, available);
            preparedStmt.setString(2, getTable(tableID).getProblem());
            preparedStmt.setString(3, tableID);

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for closing the connection to the database
     */
    public void closeDown()
    {
        try
        {
            // Closing the resources
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
