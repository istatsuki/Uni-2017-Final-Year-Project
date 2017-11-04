package frontOfHouse;

import resources.MenuItem;
import resources.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <h1> New Order Frame</h1>
 * The frame for creating new order
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class NewOrderFrame
{
    // The specifications of different components
    private final int height = 40;
    private final int bookingIDLabelWidth = 100;
    private final int bookingIDValueWidth = 100;
    private final int tableIDLabelWidth = 100;
    private final int tableIDValueWidth = 100;
    private final int menuItemLabelWidth = 150;
    private final int menuItemValueWidth = 150;
    private final int amountLabelWidth = 70;
    private final int amountValueWidth = 70;
    private final int commentLabelWidth = 70;
    private final int commentValueWidth = 400;

    /**
     * Constructor
     * @param menuItem The menu item
     * @param bookingID The booking ID
     * @param tableID The table ID
     */
    public NewOrderFrame(MenuItem menuItem, String bookingID, String tableID)
    {
        // Creating the components
        JFrame mainFrame = new JFrame("New Order");
        mainFrame.setSize(800, 210);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainFrame.add(scrollPane);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 70));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JLabel bookingIDLabel = new JLabel("Booking ID:");
        bookingIDLabel.setPreferredSize(new Dimension(bookingIDLabelWidth, height));
        topPanel.add(bookingIDLabel);

        JTextField bookingIDValue = new JTextField(bookingID);
        bookingIDValue.setPreferredSize(new Dimension(bookingIDValueWidth, height));
        bookingIDValue.setEditable(false);
        topPanel.add(bookingIDValue);

        JLabel tableIDLabel = new JLabel("Table ID:");
        tableIDLabel.setPreferredSize(new Dimension(tableIDLabelWidth, height));
        topPanel.add(tableIDLabel);

        JTextField tableIDValue = new JTextField(tableID);
        tableIDValue.setPreferredSize(new Dimension(tableIDValueWidth, height));
        tableIDValue.setEditable(false);
        topPanel.add(tableIDValue);

        JLabel menuItemLabel = new JLabel("Menu Item:");
        menuItemLabel.setPreferredSize(new Dimension(menuItemLabelWidth, height));
        topPanel.add(menuItemLabel);

        JTextField menuItemValue = new JTextField(menuItem.getMenuItemID() + ":" + menuItem.getName());
        menuItemValue.setPreferredSize(new Dimension(menuItemValueWidth, height));
        menuItemValue.setEditable(false);
        topPanel.add(menuItemValue);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 70));
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JLabel error = new JLabel("Amount need to be a number");
        error.setForeground(Color.RED);

        JPanel middlePanel = new JPanel(new FlowLayout());
        middlePanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 70));
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setPreferredSize(new Dimension(amountLabelWidth, height));
        middlePanel.add(amountLabel);

        JTextField amountValue = new JTextField();
        amountValue.setPreferredSize(new Dimension(amountValueWidth, height));
        middlePanel.add(amountValue);

        JLabel commentLabel = new JLabel("Comment:");
        commentLabel.setPreferredSize(new Dimension(commentLabelWidth, height));
        middlePanel.add(commentLabel);

        JTextField commentValue = new JTextField();
        commentValue.setPreferredSize(new Dimension(commentValueWidth, height));
        middlePanel.add(commentValue);

        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    int amount = Integer.parseInt(amountValue.getText());

                    for(int i = 0; i < amount; i++)
                    {
                        // send requests to make changes
                        String outputText = "NewOrder/" + bookingID + Client.infoSeperator + tableID + Client.infoSeperator + menuItem.getMenuItemID() + Client.infoSeperator + commentValue.getText();
                        String reply = Client.clientToFOH(outputText);
                        if (reply.contains("Order Created"))
                        {
                            mainFrame.setVisible(false);
                        }
                    }
                }
                catch(NumberFormatException ex)
                {
                    bottomPanel.add(error);
                }
            }
        });
        middlePanel.add(add);

        // Redraw the frame
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }
}
