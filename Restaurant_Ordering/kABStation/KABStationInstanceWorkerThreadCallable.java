package kABStation;

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
public class KABStationInstanceWorkerThreadCallable implements Callable<String> {

    // The command of the request
    private String command;

    // The kitchen and bar sub-system
    private KABStationInstance kABStationInstance;

    /**
     * The constructor
     * @param command The command that need to be executed
     */
    public KABStationInstanceWorkerThreadCallable(String command, KABStationInstance kabStationInstance)
    {
        this.kABStationInstance = kabStationInstance;
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

        // Extract information and tell the system to act accordingly
        if(command.indexOf("NewOrder/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String menuItemID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String status = content;

            kABStationInstance.newOrder(orderID, bookingID, menuItemID, comment, status);

            response = "Order added" + Client.infoSeperator + kABStationInstance.getTimeUpdated();
        }
        else if(command.indexOf("RemoveOrder/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content;

            kABStationInstance.removeOrder(orderID, bookingID);

            response = "Order Removed" + Client.infoSeperator + kABStationInstance.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderStatus/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            int status = Integer.parseInt(content.substring(0, content.indexOf(Client.infoSeperator)));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            boolean notifyKAB = content.equals("true");

            kABStationInstance.changeOrderStatus(orderID, bookingID, status, notifyKAB);

            response = "Order Status Changed" + Client.infoSeperator + kABStationInstance.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderComment/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            kABStationInstance.changeOrderComment(orderID, bookingID, comment);

            response = "Order Comment Changed" + Client.infoSeperator + kABStationInstance.getTimeUpdated();
        }
        return response;
    }
}


