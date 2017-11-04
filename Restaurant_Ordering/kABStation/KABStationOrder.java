package kABStation;

import java.sql.Timestamp;

/**
 * <h1> Order</h1>
 * The class for representing each of order
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABStationOrder
{
    // The preset status
    public final static int ERROR = -4;
    public final static int CANCELLED = -3;
    public final static int REFUSED = -2;
    public final static int SHORTAGE = -1;
    public final static int PLACED = 0;
    public final static int PREPARING = 1;
    public final static int PREPARED = 2;
    public final static int PRESENTED = 3;
    public final static int HIGHPRIORITY = 4;

    // The necessary attributes
    private final String orderID;
    private final String bookingID;
    private final String menuItemID;
    private int status;
    private String comment;

    // When the order is finished
    private Timestamp eTP;

    /**
     * Constuctor
     * @param orderID For orderID value
     * @param bookingID For bookingID value
     * @param menuItemID For menuItemID value
     * @param comment For comment value
     * @param status For status value
     */
    public KABStationOrder(String orderID, String bookingID, String menuItemID, String comment, String status)
    {
        this.orderID = orderID;
        this.bookingID = bookingID;
        this.menuItemID = menuItemID;
        this.status = Integer.parseInt(status);
        this.comment = comment;
        this.eTP = null;
    }

    /**
     * Method for getting the ID of the order that the order item belongs to
     * @return String The ID of the order
     */
    public String getOrderID()
    {
        return this.orderID;
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
     * Method for getting the ID of the frontOfHouse.menu item of the order item
     * @return String The ID of the frontOfHouse.menu item
     */
    public String getMenuItemID()
    {
        return this.menuItemID;
    }

    /**
     * Method for getting the status of the order item
     * @return int The number represent the status
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * Method for getting the comment of the order
     * @return String The comment of the order
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * Method for getting the time when the order is finished
     * @return long The time until the order is finished
     */
    public Timestamp getETP()
    {
        return this.eTP;
    }

    /**
     * Method for setting the status of the order item
     * @param status
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Method for setting the ETP(estimated time prepared)
     * @param second The time to make
     */
    public void setETP(long second)
    {
        this.eTP = new Timestamp(System.currentTimeMillis() + second * 1000);
    }

    /**
     * Method for setting the comment of the order
     * @param comment The comment
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * Method for getting the String representation of an order item
     * @return String The string representation of the order item
     */
    public String toString()
    {
        return "Order:" + this.orderID + " of " + this.bookingID + "; " + "menuItemID:" + this.menuItemID + "; " + "; " + "status:" + status(this.status);
    }

    /**
     * Method for changing a status integer to status String
     * @param status The status in integer
     * @return String The status in string
     */
    public static String status(int status)
    {
        if(status == ERROR)
        {
            return "ERROR";
        }
        else if(status == CANCELLED)
        {
            return "CANCELLED";
        }
        else if(status == REFUSED)
        {
            return "REFUSED";
        }
        else if(status == SHORTAGE)
        {
            return "SHORTAGE";
        }
        else if(status == PLACED)
        {
            return "PLACED";
        }
        else if(status == PREPARING)
        {
            return "PREPARING";
        }
        else if(status == PREPARED)
        {
            return "PREPARED";
        }
        else if(status == PRESENTED)
        {
            return "PRESENTED";
        }
        else
        {
            return "HIGHPRIORITY";
        }
    }
}
