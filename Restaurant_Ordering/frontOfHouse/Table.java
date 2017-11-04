package frontOfHouse;

/**
 * <h1> Table</h1>
 * The class for representing a table
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Table
{
    // The necessary attributes
    private final String tableID;
    private final int size;
    private boolean availability;
    private String problem;

    /**
     * Constructor
     * @param tableID The ID of the table
     * @param size For size value
     * @param availability For availability value
     */
    public Table(String tableID, int size, boolean availability, String problem)
    {
        this.tableID = tableID;
        this.size = size;
        this.availability = availability;
        this.problem = problem;
    }

    /**
     * Method for getting the ID of the table
     * @return String The ID of the table
     */
    public String getTableID()
    {
        return this.tableID;
    }

    /**
     * Method for getting the size of the table
     * @return String The size of the table
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Method for getting the availability of the table
     * @return boolean The availability of the table
     */
    public boolean getAvailability()
    {
        return this.availability;
    }

    /**
     * Method for getting the problem of the table
     * @return String The problem of the table
     */
    public String getProblem()
    {
        return this.problem;
    }

    /**
     * Method for setting the availability of the table
     * @param availability The availability of the table
     */
    public void setAvailability(boolean availability)
    {
        this.availability = availability;
    }

    /**
     * Method for setting the problem of the table
     * @param problem The problem
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
        String availability;
        if(this.availability == true)
        {
            availability = "available";
        }
        else
        {
            availability = "unavailable due to " + problem;
        }

        return "TableID:" + tableID + "; " + "size:" + size + "; " + availability;
    }

}
