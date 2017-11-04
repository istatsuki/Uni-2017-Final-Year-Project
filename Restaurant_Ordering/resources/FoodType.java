package resources;

/**
 * <h1> Food type</h1>
 * The class for representing the possible food type
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FoodType
{
    // The list of valid value
    private String[] types = {"starter", "main", "drink", "dessert"};

    public FoodType(String foodType)
    {
        boolean valid = false;
        if(foodType.equals(""))
        {
            valid = true;
        }
        for(int i = 0; i < types.length && !valid; i++)
        {
            if(types[i].equalsIgnoreCase(foodType) || types[i].equals(""))
            {
                valid = true;
            }
        }
        if (!valid)
        {
            throw new IllegalArgumentException("Invalid value: " + foodType);
        }
    }
}
