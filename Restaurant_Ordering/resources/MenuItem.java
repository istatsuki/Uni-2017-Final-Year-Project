package resources;

import resources.FoodType;
import resources.Special;

/**
 * <h1> Menu Item</h1>
 * The class for representing a frontOfHouse.menu item
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class MenuItem
{
    // The necessary attribute
    private final String menuItemID;
    private final String name;
    private final String type;
    private final String image;
    private final String ingredientList;
    private boolean availability;
    private String problem;
    private final long time;
    private final double price;
    private final String specialList;

    /**
     * Constructor
     * @param menuItemID For menuItemID value
     * @param name For name value
     * @param type For type value
     * @param image For the image file location
     * @param ingredientList The list of the ingredient required for making the frontOfHouse.menu item
     * @param availability For availability value
     * @param problem For problem if the frontOfHouse.menu item is not available
     * @param time For how long it is required to make
     * @param price For price value
     * @param specialList For specials features
     */
    public MenuItem(String menuItemID, String name, String type, String image, String ingredientList, boolean availability, String problem, long time, double price, String specialList)
    {
        // Check typeList validity
        String typeListCheck = type;
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

        // check special list validity
        String specialListCheck = specialList;
        while(specialListCheck.contains(";"))
        {
            try
            {
                Special special = new Special(specialListCheck.substring(0, specialListCheck.indexOf(";")));
                specialListCheck = specialListCheck.substring(specialListCheck.indexOf(";") + 1);
            }
            catch(IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
        }

        try
        {
            Special special = new Special(specialListCheck);
        }
        catch(IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        this.menuItemID = menuItemID;
        this.name = name;
        this.type = type;
        this.image = image;
        this.ingredientList = ingredientList;
        this.availability = availability;
        this.problem = problem;
        this.time = time;
        this.price = price;
        this.specialList = specialList;
    }

    /**
     * Method for getting the ID of the menu item
     * @return String The ID of the menu item
     */
    public String getMenuItemID()
    {
        return this.menuItemID;
    }

    /**
     * Method for getting the name of the menu item
     * @return String The name of the menu item
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Method for getting the type of the menu item
     * @return String The type of the menu item
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Method for getting the image file location of the menu item
     * @return String The image file location of the menu item
     */
    public String getImage()
    {
        return this.image;
    }

    /**
     * Method for getting the ingredient list required to make the menu item
     * @return String The ingredient list required to make the menu item
     */
    public String getIngredientList()
    {
        return this.ingredientList;
    }

    /**
     * Method for getting the availability of the menu item
     * @return boolean Is the menu item available
     */
    public boolean getAvailability()
    {
        return this.availability;
    }

    /**
     * Method for getting the problem if the menu item is not available
     * @return String The problem
     */
    public String getProblem()
    {
        return this.problem;
    }

    /**
     * Method for getting the time needed to make the menu item
     * @return long The time
     */
    public long getTime()
    {
        return this.time;
    }

    /**
     * Method for getting the price of the menu item
     * @return double The price
     */
    public double getPrice()
    {
        return this.price;
    }

    /**
     * Method for getting the allergy report of the menu item
     * @return String The allergy report
     */
    public String getSpecialList()
    {
        return this.specialList;
    }

    /**
     * Method for setting the availability of the menu item
     * @param availability The availability of the menu item
     */
    public void setAvailability(boolean availability)
    {
        this.availability = availability;
    }

    /**
     * Method for setting the problem of the menu item
     * @param problem The problem of the menu item
     */
    public void setProblem(String problem)
    {
        this.problem = problem;
    }

    /**
     * Method to make a String representation of the object
     * @return The String representation
     */
    public String toString()
    {
        String availability = "";
        if(this.availability == true)
        {
            availability = "available";
        }
        else
        {
            availability = "unavailable due to " + problem;
        }
        return "MenuItemID:" + this.menuItemID +"; " + "name:" + this.name + "; " + "type: " + type + "; " + "ingredientList: " + this.ingredientList + "; " + availability + "; " + "time: " + time + " s" + "; " + "price: " + price + "£" + "; " + "special:" + this.specialList;
    }
}
