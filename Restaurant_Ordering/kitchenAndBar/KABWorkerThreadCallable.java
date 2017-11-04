package kitchenAndBar;

import resources.Client;

import java.util.concurrent.Callable;

/**
 * <h1> Kitchen And Bar Worker Thread Callable</h1>
 * The worker of each requests (with return value)
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABWorkerThreadCallable implements Callable<String> {

    // The command of the request
    private String command;

    // The kitchen and bar sub-system
    private KitchenAndBar kitchenAndBar = KitchenAndBar.instance();

    /**
     * The constructor
     * @param command The command that need to be executed
     */
    public KABWorkerThreadCallable(String command)
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

        // Extracting information and tell the system to act accordingly
        if(command.indexOf("NewOrder/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String menuItemID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            kitchenAndBar.newOrder(orderID, bookingID, tableID, menuItemID, comment);

            response = "Order Created" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderTable/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID = content;

            kitchenAndBar.changeOrderTable(orderID, bookingID, tableID);

            response = "Order Table Changed" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderStatus/") == 0) {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            int status = Integer.parseInt(content.substring(0, content.indexOf(Client.infoSeperator)));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            boolean notifyFOH = content.substring(0, content.indexOf(Client.infoSeperator)).equals("true");
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            boolean notifyStation = content.equals("true");

            kitchenAndBar.changeOrderStatus(orderID, bookingID, status, notifyFOH, notifyStation);

            response = "Order Status Changed" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderComment/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            kitchenAndBar.changeOrderComment(orderID, bookingID, comment);

            response = "Order Comment Changed" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderStation/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String stationID = content;

            kitchenAndBar.changeOrderStation(orderID, bookingID, stationID);

            response = "Order Station Changed" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderETP/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String eTP = content;

            kitchenAndBar.changeOrderETP(orderID, bookingID, eTP);

            response = "Order ETP Changed" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("StationOnline/") == 0)
        {
            String stationID = command.substring(command.indexOf("/") + 1);

            boolean changeStationOnlinestatus = kitchenAndBar.changeStationOnlineStatus(stationID, true);

            if(changeStationOnlinestatus)
            {
                response = "Station Online" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
            }
            else
            {
                response = "Station already Logged In";
            }
        }
        else if(command.indexOf("StationOffline/") == 0)
        {
            String stationID = command.substring(command.indexOf("/") + 1);

            kitchenAndBar.changeStationOnlineStatus(stationID, false);

            response = "Station Offline" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("CheckIngredient/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content;

            kitchenAndBar.checkIngredient(orderID, bookingID);

            response = "Ingredient Checked" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        else if(command.indexOf("MoreIngredient/") == 0)
        {
            String ingredientID = command.substring(command.indexOf("/") + 1);

            kitchenAndBar.checkMenu(ingredientID);
        }
        else if(command.indexOf("Failure/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content;

            kitchenAndBar.failure(orderID, bookingID);

            response = "Failure Added" + Client.infoSeperator + kitchenAndBar.getTimeUpdated();
        }
        return response;
    }
}


