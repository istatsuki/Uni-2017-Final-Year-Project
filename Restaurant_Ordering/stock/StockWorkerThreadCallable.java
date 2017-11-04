package stock;

import resources.Client;
import resources.Pair;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * <h1> Stock Worker Thread Callable</h1>
 * The worker of each requests (with return value)
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StockWorkerThreadCallable implements Callable<String> {

    // The command of the request
    private String command;

    // The stock manager sub-system
    private StockManager stockManager = StockManager.instance();

    /**
     * The constructor
     * @param command The command that need to be executed
     */
    StockWorkerThreadCallable(String command)
    {
        this.command = command;
    }

    /**
     * The execution code
     * @return String The response from the execution
     */
    @Override
    public String call() throws Exception
    {
        return processCommand(command);
    }

    /**
     * Process the command accordingly to the command
     * @return String the response
     */
    private String processCommand(String command)
    {
        // Initiate empty response
        String response = "";

        // Extract the information and tell the system to act accordingly
        if(command.indexOf("CheckIngredient/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);
            ArrayList<Pair<String, Integer>> ingredientList = createIngredientList(content);

            boolean enough = true;

            for(int i = 0; i < ingredientList.size() && enough; i++)
            {
                if(!stockManager.checkIngredientAmount(ingredientList.get(i).getFirst(), ingredientList.get(i).getSecond()))
                {
                    enough = false;
                }
            }

            if(enough)
            {
                for(int i = 0; i < ingredientList.size() && enough; i++)
                {
                    stockManager.changeIngredientAmount(ingredientList.get(i).getFirst(), -ingredientList.get(i).getSecond());
                }

                response = "Enough Ingredient" + Client.infoSeperator + stockManager.getTimeUpdated();
            }
            else
            {
                response = "Not Enough Ingredient" + Client.infoSeperator + stockManager.getTimeUpdated();
            }
        }
        else if(command.indexOf("MoreIngredient/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);
            ArrayList<Pair<String, Integer>> ingredientList = createIngredientList(content);

            for(int i = 0; i < ingredientList.size(); i++)
            {
                stockManager.changeIngredientAmount(ingredientList.get(i).getFirst(), ingredientList.get(i).getSecond());
            }
        }
        else if(command.indexOf("CheckMenu/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);
            ArrayList<Pair<String, Integer>> ingredientList = createIngredientList(content);

            boolean enough = true;

            for(int i = 0; i < ingredientList.size() && enough; i++)
            {
                if(!stockManager.checkIngredientAmount(ingredientList.get(i).getFirst(), ingredientList.get(i).getSecond()))
                {
                    enough = false;
                }
            }

            if(enough)
            {
                response = "Enough Ingredient" + Client.infoSeperator + stockManager.getTimeUpdated();
            }
            else
            {
                response = "Not Enough Ingredient" + Client.infoSeperator + stockManager.getTimeUpdated();
            }
        }
        return response;
    }

    private ArrayList<Pair<String, Integer>> createIngredientList(String content)
    {
        ArrayList<Pair<String, Integer>> ingredientList = new ArrayList<Pair<String, Integer>>();

        while(content.contains(","))
        {
            String ingredientID = content.substring(0, content.indexOf(":"));
            content = content.substring(content.indexOf(":") + 1);

            int amount = Integer.parseInt(content.substring(0, content.indexOf(",")));
            content = content.substring(content.indexOf(",") + 1);

            ingredientList.add(new Pair<String, Integer>(ingredientID, amount));
        }

        String ingredientID = content.substring(0, content.indexOf(":"));
        content = content.substring(content.indexOf(":") + 1);

        int amount = Integer.parseInt(content);

        ingredientList.add(new Pair<String, Integer>(ingredientID, amount));
        return ingredientList;
    }
}


