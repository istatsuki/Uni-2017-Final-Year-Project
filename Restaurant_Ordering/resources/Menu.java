package resources;

import java.sql.Timestamp;

/**
 * <h1> Menu</h1>
 * The class for representing the menu which holds all the menu items
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Menu
{
    // The list of the menu item
    private final MenuItem[] menuItemList;

    // The last updated date of the menu
    private Timestamp lastUpdated;

    /**
     * Constructor
     * @param menuItemList The list of the menu item
     * @param lastUpdated The last updated Date of the menu
     */
    public Menu(MenuItem[] menuItemList, Timestamp lastUpdated)
    {
        this.menuItemList = menuItemList;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Method for getting the list of menu items
     * @return MenuItem[] The list of menu items
     */
    public MenuItem[] getMenuItemList()
    {
        return this.menuItemList;
    }

    /**
     * Method for getting a menu item
     * @param menuItemID The ID of the menu item
     * @return MenuItem the menu item, null if not found
     */
    public MenuItem getMenuItem(String menuItemID)
    {
        for(int i = 0; i < menuItemList.length; i++)
        {
            if(menuItemList[i].getMenuItemID().equals(menuItemID))
            {
                return this.menuItemList[i];
            }
        }

        return null;
    }

    /**
     * Method for getting the last updated date
     * @return Date The last updated date
     */
    public Timestamp getLastUpdated()
    {
        return lastUpdated;
    }

    /**
     * Method for setting the last updated date
     * @param lastUpdated The last updated date
     */
    public void setLastUpdated(Timestamp lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
}
