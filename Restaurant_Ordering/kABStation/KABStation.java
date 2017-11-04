package kABStation;

import java.util.ArrayList;

import resources.FoodType;

/**
 * <h1> Kitchen or Bar Station</h1>
 * The class for representing a kitchen or bar station
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABStation
{
    // The necessary attribute
    private final String stationID;
    private final String name;
    private boolean onlineStatus;
    private final int portNum;
    private final String specialtyList;
    private ArrayList<KABStationOrder> orderList;

    /**
     * Constructor
     * @param stationID The ID of the station
     * @param name The name value
     * @param portNum The port number of the station server
     * @param specialtyList The list of specialties of the station
     */
    public KABStation(String stationID, String name, int portNum, String specialtyList)
    {
        // Check typeList validity
        String typeListCheck = specialtyList;
        while(typeListCheck.contains(";"))
        {
            try
            {
                FoodType foodType = new FoodType(typeListCheck.substring(0, typeListCheck.indexOf(";")));
                typeListCheck = typeListCheck.substring(typeListCheck.indexOf(";") + 1);
            }
            catch(IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
        }

        try
        {
            FoodType foodType = new FoodType(typeListCheck);
        }
        catch(IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        this.stationID = stationID;
        this.name = name;
        this.onlineStatus = false;
        this.portNum = portNum;
        this.specialtyList = specialtyList;
        this.orderList = new ArrayList<KABStationOrder>();
    }

    /**
     * Method for getting the ID of the station
     * @return String The ID of the station
     */
    public String getStationID()
    {
        return this.stationID;
    }

    /**
     * Method for getting the name of the station
     * @return String The name of the station
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Method for getting the online status of the station
     * @return boolean The online status of the station
     */
    public boolean getOnlineStatus()
    {
        return this.onlineStatus;
    }

    public int getPortNum()
    {
        return this.portNum;
    }

    /**
     * Method for getting the list of the specialties of the staion
     * @return String[] The list of the specialties
     */
    public String getSpecialtyList()
    {
        return this.specialtyList;
    }

    /**
     * Method for getting the list of order items of the station
     * @return ArrayList<String> The list of order items
     */
    public ArrayList<KABStationOrder> getOrderList()
    {
        return this.orderList;
    }

    /**
     * Method for setting the online status of the station
     * @param onlineStatus The online status of the station
     */
    public void setOnlineStatus(boolean onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }

    /**
     * Method to make a String representation of the object
     * @return The String representation
     */
    public String toString()
    {
        return "StationID:" + this.stationID + "; " + "name:"+ this.name + "; " + "specialties:"+ this.specialtyList + "; " +  online(this.onlineStatus);
    }

    /**
     * Method to create a string of online value
     * @param online The online value
     * @return String The string value
     */
    public static String online(boolean online)
    {
        if(online)
        {
            return "online";
        }
        else
        {
            return "offline";
        }
    }

    /**
     * Method to reArranging the order (preparing first, then high priority and then others)
     */
    public void reArrangeOrderList()
    {
        ArrayList<KABStationOrder> tempList = new ArrayList<KABStationOrder>();
        for(int i = 0; i < orderList.size(); i++)
        {
            if(orderList.get(i).getStatus() == KABStationOrder.PREPARING)
            {
                tempList.add(orderList.get(i));
            }
        }

        for(int i = 0; i < orderList.size(); i++)
        {
            if(orderList.get(i).getStatus() == KABStationOrder.HIGHPRIORITY)
            {
                tempList.add(orderList.get(i));
            }
        }

        for(int i = 0; i < orderList.size(); i++)
        {
            if(!tempList.contains(orderList.get(i)))
            {
                tempList.add(orderList.get(i));
            }
        }

        this.orderList = tempList;
    }

    /**
     * Method to get an order
     * @param orderID The ID of the order
     * @param bookingID The ID of the booking
     * @return The order
     */
    public KABStationOrder getOrder(String orderID, String bookingID)
    {
        for(int i = 0; i < orderList.size(); i++)
        {
            if(orderList.get(i).getOrderID().equals(orderID) && orderList.get(i).getBookingID().equals(bookingID))
            {
                return orderList.get(i);
            }
        }
        return null;
    }
}
