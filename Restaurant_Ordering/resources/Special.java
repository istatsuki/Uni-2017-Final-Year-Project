package resources;

/**
 * <h1> Food type</h1>
 * The class for representing the possible special features
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Special
{
    // The list of valid value
    private String[] types = {"gluten", "vegan"};

    /**
     * Constructor
     * @param special The special type
     */
    public Special(String special)
    {
        boolean valid = false;
        if(special.equals(""))
        {
            valid = true;
        }
        for(int i = 0; i < types.length && !valid; i++)
        {
            if(types[i].equalsIgnoreCase(special))
            {
                valid = true;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid value: " + special);
        }
    }
}
