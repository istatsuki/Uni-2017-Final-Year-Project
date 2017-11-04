package kitchenAndBar;

import java.util.ArrayList;
import resources.FoodType;
import resources.Pair;

/**
 * <h1> Kitchen or Bar Station</h1>
 * The class for representing a kitchen or bar station
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Station
{
    // The necessary attribute
    private final String stationID;
    private final String name;
    private boolean onlineStatus;
    private final int portNum;
    private final String specialtyList;
    private ArrayList<Pair<String, String>> orderList;

    /**
     * Constructor
     * @param stationID The ID of the station
     * @param name The name value
     * @param portNum The port number
     * @param specialtyList The list of specialties of the station
     */
    public Station(String stationID, String name, int portNum, String specialtyList)
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
        this.orderList = new ArrayList<Pair<String, String>>();
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
    public ArrayList<Pair<String, String>> getOrderList()
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
}
