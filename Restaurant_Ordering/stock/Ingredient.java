package stock;

import java.sql.*;

/**
 * <h1> Ingredient Representation</h1>
 * The class for representing the ingredient in the stock
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Ingredient
{
    // the value of the ingredient
    private final String ingredientID;
    private final String name;
    private final String unit;
    private long amount;

    /**
     * Constructor
     * @param ingredientID For ingredientID value
     * @param name For name value
     * @param amount For amount value
     */
    Ingredient(String ingredientID, String name, String unit, long amount)
    {
        this.ingredientID = ingredientID;
        this.name = name;
        this.unit = unit;
        this.amount = amount;
    }

    /**
     * Method to get ingredientID value
     * @return String the ingredientID value
     */
    public String getIngredientID()
    {
        return this.ingredientID;
    }

    /**
     * Method for getting the name value
     * @return String the name value
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Method for getting the unit value
     * @return String the unit value
     */
    public String getUnit()
    {
        return this.unit;
    }

    /**
     * Method for getting the amount
     * @return long the amount value
     */
    public long getAmount()
    {
        return this.amount;
    }

    /**
     * Method for changing the amount
     * @param change the amount change
     */
    public void changeAmount(long change)
    {
        this.amount += change;
        // Initiating the resources for the database connection
    }

    /**
     * Method to make a String representation of the object
     * @return The String representation
     */
    public String toString()
    {
        return "IngredientID:" + this.ingredientID + "; " + "name:"+ this.name + "; " + this.amount + " " + this.unit;
    }

}
