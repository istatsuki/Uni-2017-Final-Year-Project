package resources;

import javax.swing.*;
import java.awt.*;

/**
 * <h1> Menu Item Frame</h1>
 * The Frame of the Menu Item
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class MenuItemFrame
{
    // The specifications of different components
    private final int labelWidth = 100;
    private final int infoWidth = 300;
    private final int height =20;
    private final int heightSpace = 20;
    private final int widthSpace = 0;

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    /**
     * Constructors
     * @param menuItem The menu Item
     * @param label The label which connect with the frame
     */
    public MenuItemFrame(MenuItem menuItem, JLabel label)
    {
        // Creating the components
        mainFrame = new JFrame(menuItem.getMenuItemID() + ":" + menuItem.getName());
        mainFrame.setSize(500, 200);
        mainFrame.setResizable(false);
        mainFrame.setLocation(label.getX() + label.getWidth(), label.getY() + label.getHeight());

        mainPanel = new JPanel(null);
        mainFrame.add(mainPanel);

        JLabel menuItemIDLabel = new JLabel("Menu Item ID:", SwingConstants.CENTER);
        menuItemIDLabel.setPreferredSize(new Dimension(labelWidth, height));
        menuItemIDLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2), menuItemIDLabel.getPreferredSize()));
        mainPanel.add(menuItemIDLabel);

        JLabel menuItemID = new JLabel(menuItem.getMenuItemID());
        menuItemID.setPreferredSize(new Dimension(infoWidth, height));
        menuItemID.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2), menuItemID.getPreferredSize()));
        mainPanel.add(menuItemID);

        JLabel nameLabel = new JLabel("Name:", SwingConstants.CENTER);
        nameLabel.setPreferredSize(new Dimension(labelWidth, height));
        nameLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + heightSpace), nameLabel.getPreferredSize()));
        mainPanel.add(nameLabel);

        JLabel name = new JLabel(menuItem.getName());
        name.setPreferredSize(new Dimension(infoWidth, height));
        name.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + heightSpace), name.getPreferredSize()));
        mainPanel.add(name);

        JLabel typeLabel = new JLabel("Type:", SwingConstants.CENTER);
        typeLabel.setPreferredSize(new Dimension(labelWidth, height));
        typeLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 2 * heightSpace), typeLabel.getPreferredSize()));
        mainPanel.add(typeLabel);

        JLabel type = new JLabel(menuItem.getType());
        type.setPreferredSize(new Dimension(infoWidth, height));
        type.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + 2 * heightSpace), type.getPreferredSize()));
        mainPanel.add(type);

        JLabel ingredientListLabel = new JLabel("Ingredient List:", SwingConstants.CENTER);
        ingredientListLabel.setPreferredSize(new Dimension(labelWidth, height));
        ingredientListLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 3 * heightSpace), ingredientListLabel.getPreferredSize()));
        mainPanel.add(ingredientListLabel);

        JLabel ingredientList = new JLabel(menuItem.getIngredientList());
        ingredientList.setPreferredSize(new Dimension(infoWidth, height));
        ingredientList.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + 3 * heightSpace), ingredientList.getPreferredSize()));
        mainPanel.add(ingredientList);

        JLabel timeLabel = new JLabel("Time:", SwingConstants.CENTER);
        timeLabel.setPreferredSize(new Dimension(labelWidth, height));
        timeLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 4 * heightSpace), timeLabel.getPreferredSize()));
        mainPanel.add(timeLabel);

        JLabel time = new JLabel(String.valueOf(menuItem.getTime()) + "s");
        time.setPreferredSize(new Dimension(infoWidth, height));
        time.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + 4 * heightSpace), time.getPreferredSize()));
        mainPanel.add(time);

        JLabel priceLabel = new JLabel("Price:", SwingConstants.CENTER);
        priceLabel.setPreferredSize(new Dimension(labelWidth, height));
        priceLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 5 * heightSpace), priceLabel.getPreferredSize()));
        mainPanel.add(priceLabel);

        JLabel price = new JLabel(String.valueOf(menuItem.getPrice()) + "£");
        price.setPreferredSize(new Dimension(infoWidth, height));
        price.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + 5 * heightSpace), price.getPreferredSize()));
        mainPanel.add(price);

        JLabel specialLabel = new JLabel("Special:", SwingConstants.CENTER);
        specialLabel.setPreferredSize(new Dimension(labelWidth, height));
        specialLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 6 * heightSpace), specialLabel.getPreferredSize()));
        mainPanel.add(specialLabel);

        JLabel special = new JLabel(String.valueOf(menuItem.getPrice()) + "£");
        special.setPreferredSize(new Dimension(infoWidth, height));
        special.setBounds(new Rectangle(new Point(widthSpace/2 + labelWidth + widthSpace, heightSpace/2 + 6 * heightSpace), special.getPreferredSize()));
        mainPanel.add(special);

        // Draw panel
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * Method to show the frame
     */
    public void show()
    {
        mainFrame.setVisible(true);
    }

}
