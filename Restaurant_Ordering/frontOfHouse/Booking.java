package frontOfHouse;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * <h1> Booking</h1>
 * The class for representing a booking
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Booking
{
    // The preset status
    public final static int CANCELLED = -2;
    public final static int BOOKED = -1;
    public final static int PLACED = 0;
    public final static int FINISHED = 1;
    public final static int BILLED = 2;

    // The necessary attributes
    private final String bookingID;
    private String bookerName;
    private String contact;
    private String tablesRequested;
    private Timestamp date;
    private String comment;

    private ArrayList<FOHOrder> orderList;
    private ArrayList<String> tableList;
    private int status;

    /**
     * Constructor
     * @param bookingID the ID of the booking
     * @param bookerName For bookerName value
     * @param contact For contact value
     * @param tablesRequested The tables requested
     * @param date For Setting the date value
     * @param comment For comment value
     * @param status For status value
     */
    public Booking(String bookingID, String bookerName, String contact, String tablesRequested, Timestamp date, String comment, int status)
    {
        this.bookingID = bookingID;
        this.bookerName = bookerName;
        this.contact = contact;
        this.tablesRequested = tablesRequested;
        this.date = date;
        this.comment = comment;
        this.orderList = new ArrayList<FOHOrder>();
        this.tableList = new ArrayList<String>();
        this.status = status;
    }

    /**
     * Method for getting the ID of the booking
     * @return String The ID of the booking
     */
    public String getBookingID()
    {
        return this.bookingID;
    }

    /**
     * Method for getting the name of the booker of the booking
     * @return String The name of the booker of the booking
     */
    public String getBookerName()
    {
        return this.bookerName;
    }

    /**
     * Method for getting the contact of the booker
     * @return String The contact of the booker
     */
    public String getContact()
    {
        return this.contact;
    }

    /**
     * Method for getting the size of the booking
     * @return Pair<Integer, Integer> The size of the booking
     */
    public String getTablesRequested()
    {
        return this.tablesRequested;
    }

    /**
     * Method for getting the date and time of the booking
     * @return String The date and time of the booking
     */
    public Timestamp getDate()
    {
        return this.date;
    }

    /**
     * Method for getting the comments and requirements of the booking
     * @return String The comments and requirements of the booking
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * Method for getting the list of order
     * @return ArrayList<Order> The order list
     */
    public ArrayList<FOHOrder> getOrderList()
    {
        return this.orderList;
    }

    public FOHOrder getOrder(String orderID)
    {
        for(int i = 0; i < orderList.size(); i++)
        {
            if(orderList.get(i).getOrderID().equals(orderID))
            {
                return orderList.get(i);
            }
        }
        return null;
    }

    /**
     * Method for getting the list of table
     * @return ArrayList<String> The table list
     */
    public ArrayList<String> getTableList()
    {
        return this.tableList;
    }

    /**
     * Method for getting the status
     * @return int The status
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * Method for setting the comments or requirements of the booking
     * @param comment The comments or requirements of the booking
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * Method for setting the date of the date of the booking
     * @param date The date
     */
    public void setDate(Timestamp date)
    {
        this.date = date;
    }

    /**
     * Method for setting the status of the booking
     * @param status The status of the booking
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Method for removing a table from the booking
     * @param tableID The table ID
     */
    public void removeTable(String tableID)
    {
        boolean removed = false;
        for(int i = 0; i < tableList.size() & !removed; i ++)
        {
            if(tableList.get(i).equalsIgnoreCase(tableID))
            {
                tableList.remove(i);
                removed = true;
            }
        }
    }

    /**
     * Method to make a String representation of the object
     * @return The String representation
     */
    public String toString()
    {
        return "BookingID:" + bookingID + "; " + "bookerName: " + bookerName + "; " + "contact: " + contact + "; " + "tablesRequested: " + tablesRequested + "; " + "date:" + date + "; " + "comment:" + comment;
    }

    /**
     * Method for getting the String representative of the status
     * @param status The status in integer value
     * @return THe status in String value
     */
    public static String status(int status)
    {
        if(status == CANCELLED)
        {
            return "CANCELLED";
        }
        else if(status == BOOKED)
        {
            return "BOOKED";
        }
        else if(status == PLACED)
        {
            return "PLACED";
        }
        else if(status == FINISHED)
        {
            return "FINISHED";
        }
        else
        {
            return "BILLED";
        }
    }

}
