package frontOfHouse;

import resources.Client;

import java.util.concurrent.Callable;

/**
 * <h1> Front Of House Worker Thread Callable</h1>
 * The worker of each requests (with return value)
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FOHWorkerThreadCallable implements Callable<String> {

    // The command of the request
    private String command;

    // The front of house sub-system
    private FrontOfHouse frontOfHouse = FrontOfHouse.instance();

    /**
     * The constructor
     * @param command The command that need to be executed
     */
    public FOHWorkerThreadCallable(String command)
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

        // For each of the request, extract the data, make change to local system and respond
        if(command.indexOf("NewBooking/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookerName = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String contact = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tablesRequested = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String date = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            frontOfHouse.newBooking(bookerName, contact, tablesRequested, date, comment);

            response = "Booking Created" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeBookingStatus/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String status = content;

            frontOfHouse.changeBookingStatus(bookingID, Integer.valueOf(status));

            response = "Booking Status Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("AddBookingTable/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID = content;

            boolean addBookingTable = frontOfHouse.addBookingTable(bookingID, tableID);

            if(addBookingTable)
            {
                response = "Booking Table Added" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
            }
            else
            {
                response = "Couldn't Add Booking Table";
            }
        }
        else if(command.indexOf("NewOrder/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String menuItemID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            frontOfHouse.newOrder(bookingID, tableID, menuItemID, comment);

            response = "Order Created" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
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

            frontOfHouse.changeOrderStatus(orderID, bookingID, status, notifyKAB);

            response = "Order Status Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeBookingTable/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID1 = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID2 = content;

            boolean changeBookingTable = frontOfHouse.changeBookingTable(bookingID, tableID1, tableID2);

            if(changeBookingTable)
            {
                response = "Booking Table Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
            }
            else
            {
                response = "Couldn't Change Booking Table";
            }

            response = "Booking Table Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("RemoveBookingTable/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID1 = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID2 = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            boolean keepAll = content.equalsIgnoreCase("true");

            frontOfHouse.removeBookingTable(bookingID, tableID1, tableID2, keepAll);

            response = "Table Removed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderTable/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String tableID = content;

            frontOfHouse.changeOrderTable(orderID, bookingID, tableID);

            response = "Order Table Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderComment/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String comment = content;

            frontOfHouse.changeOrderComment(orderID, bookingID, comment);

            response = "Order Comment Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeOrderETP/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String orderID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String bookingID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String eTP = content;

            frontOfHouse.changeOrderETP(orderID, bookingID, eTP);

            response = "Order ETP Changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("MenuItemAvailable/") == 0)
        {
            String menuItemID = command.substring(command.indexOf("/") + 1);

            frontOfHouse.changeMenuItemAvailable(menuItemID,true);

            response = "Menu Item availability changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeTableAvailability/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String tableID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            boolean availability = Boolean.valueOf(content);

            frontOfHouse.changeTableAvailable(tableID, availability, frontOfHouse.getTable(tableID).getProblem());

            response = "Table availability changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        else if(command.indexOf("ChangeTableProblem/") == 0)
        {
            String content = command.substring(command.indexOf("/") + 1);

            String tableID = content.substring(0, content.indexOf(Client.infoSeperator));
            content = content.substring(content.indexOf(Client.infoSeperator) + Client.infoSeperator.length());

            String problem = content;

            frontOfHouse.changeTableAvailable(tableID, frontOfHouse.getTable(tableID).getAvailability(), problem);

            response = "Table Problem changed" + Client.infoSeperator + frontOfHouse.getTimeUpdated();
        }
        return response;
    }
}


