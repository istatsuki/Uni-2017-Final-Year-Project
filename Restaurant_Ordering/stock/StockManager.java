package stock;

import resources.Client;

import javax.xml.transform.Result;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Stock Manager</h1>
 * The class for managing the stock
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StockManager
{
    // Singleton settings
    private static StockManager stockManager = new StockManager();
    public static StockManager instance()
    {
        return stockManager;
    }

    // The database setting
    private String databaseURL = "jdbc:mysql://127.0.0.1:5000/resttest?useSSL=false";
    private String userName = "root";
    private String password = "looping";
    private Connection conn;

    // The list of the ingredient
    private Ingredient[] ingredientList;

    // The WorkerThread
    private ExecutorService executor;

    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    /**
     * Constructor
     */
    private StockManager()
    {
        try
        {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(databaseURL, userName, password);
        }
        catch(Exception e)
        {
            // Handle errors for Class.forName
            e.printStackTrace();
            System.exit(0);
        }

        // Getting the ingredient information from the database
        this.ingredientList = createIngredientList();

        // Creating ThreadPool for handling requests from the other sub-systems
        executor = Executors.newFixedThreadPool(5);

    }

    /**
     * Method for getting the ingredient list from the database
     * @return Ingredient[] The ingredient list
     */
    private Ingredient[] createIngredientList()
    {
        // Initiating the list of the ingredient
        Ingredient[] list = null;

        // Try statement for the connection
        try
        {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;
            ResultSet rs;

            // Firstly, get the number of the ingredient
            sql = "SELECT COUNT(ingredientID) FROM stock";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            rs.next();
            list = new Ingredient[rs.getInt(1)];

            // get the information
            sql = "SELECT * FROM stock";
            preparedStmt = conn.prepareStatement(sql);
            rs = preparedStmt.executeQuery();
            int count = 0;

            // Creating each of the ingredient object
            while (rs.next())
            {
                // getting information by the column name
                String ingredientID = rs.getString("ingredientID");
                String name = rs.getString("name");
                String unit = rs.getString("unit");
                long amount = rs.getLong("amount");

                list[count] = new Ingredient(ingredientID, name, unit, amount);
                // go to the next ingredient
                count++;
            }

            rs.close();
            preparedStmt.close();

            return list;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return list;
    }

    /**
     * Method for checking an ingredient with the ID an amount
     * @param ingredientID The ID of the ingredient
     * @param amount The amount needed to be checked
     * @return boolean Is there enough of the specified ingredient
     */
    public boolean checkIngredientAmount(String ingredientID, int amount)
    {
        Ingredient ingredient = this.getIngredient(ingredientID);
        if(ingredient.getAmount() >= amount)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Method for increase amount of an ingredient
     * @param ingredientID The ID of the ingredient
     * @param amount The amount needed to be increased
     */
    public void changeIngredientAmount(String ingredientID, int amount)
    {
        this.getIngredient(ingredientID).changeAmount(amount);

        // Try statement for the connection to update the information
        try {
            // Initiating more resources
            System.out.println("Creating statement...");
            String sql;
            PreparedStatement preparedStmt;

            sql = "UPDATE stock SET amount = ? WHERE ingredientID = ?";
            preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setLong(1, this.getIngredient(ingredientID).getAmount());
            preparedStmt.setString(2, this.getIngredient(ingredientID).getIngredientID());

            preparedStmt.executeUpdate();

            preparedStmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(amount > 0)
        {
            Client.clientToKAB("MoreIngredient/" + ingredientID);
        }

        timeUpdated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method for getting the ingredient list
     * @return Ingredient[] the list of the ingredient
     */
    public Ingredient[] getIngredientList()
    {
        return this.ingredientList;
    }

    /**
     * Method for getting an ingredient
     * @param ingredientID the ID of the ingredient
     * @return Ingredient the ingredient object, null if not found
     */
    public Ingredient getIngredient(String ingredientID)
    {
        for(int i = 0; i < ingredientList.length; i++)
        {
            if(ingredientList[i].getIngredientID().equals(ingredientID))
            {
                return this.ingredientList[i];
            }
        }

        return null;
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
     * Method for getting the time updated
     * @return String the time updated
     */
    public String getTimeUpdated()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.timeUpdated);
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