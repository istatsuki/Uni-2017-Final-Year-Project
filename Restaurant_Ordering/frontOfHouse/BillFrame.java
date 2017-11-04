package frontOfHouse;

import resources.MenuItem;
import resources.JLabelCreation;
import resources.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * <h1> Billing Frame</h1>
 * The class for representing the billing information of a booking
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class BillFrame
{
    // The attributes
    FrontOfHouse frontOfHouse = FrontOfHouse.instance();
    private ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;

    // The specifications of different components
    private final static  int heightSpace = 20;
    private final static  int widthSpace = 0;
    private final static int height = 20;
    private final static  int menuItemWidth = 120;
    private final static  int amountWidth = 80;
    private final static  int unitPriceWidth = 80;
    private final static  int priceWidth = 80;
    private final static  String[] titles = {"Menu Item", "Amount", "Unit Price", "Price"};
    private final static  int[] widths = {menuItemWidth, amountWidth, unitPriceWidth, priceWidth};

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel middlePanel;
    private JPanel labelPanel;
    private JPanel infoPanel;

    private JPanel bottomPanel;
    private JPanel navigationBar;
    private JButton firstButton;
    private JButton previousButton;
    private JTextField pageButton;
    private JButton nextButton;
    private JButton lastButton;
    private JLabel bottomLabel;

    /**
     * Constructor
     * @param orderList the list of the orders of thebooking
     */
    public BillFrame(ArrayList<FOHOrder> orderList)
    {
        // The list of orders that need to be paid
        ArrayList<FOHOrder> validOrder = new ArrayList<FOHOrder>();

        // Get all the valid orders
        for(int i = 0; i< orderList.size(); i++)
        {
            if(orderList.get(i).getStatus() == FOHOrder.PRESENTED)
            {
                validOrder.add(orderList.get(i));
            }
        }

        // Rearrange the information to show in the bill
        for(int i = 0; i < validOrder.size(); i++)
        {
            String menuItemID = validOrder.get(i).getMenuItemID();
            boolean contain = false;
            for(int k = 0; k < result.size() && !contain; k++)
            {
                if(result.get(k).getFirst().equals(menuItemID))
                {
                    result.get(k).setSecond(result.get(k).getSecond() + 1);
                    contain = true;
                }
            }

            if(!contain)
            {
                result.add(new Pair<String, Integer>(menuItemID, 1));
            }
        }

        // Create the main frame
        mainFrame = new JFrame("Bill");
        mainFrame.setSize(530, 500);
        mainFrame.setVisible(true);

        mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Create the bottom panel
        bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // The label to show time updated
        bottomLabel = new JLabel();
        bottomPanel.add(bottomLabel, BorderLayout.EAST);

        // The navigation bar
        navigationBar = new JPanel(new FlowLayout());
        bottomPanel.add(navigationBar, BorderLayout.CENTER);

        // The navigation buttons
        firstButton = new JButton("|<");
        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPage != 1)
                {
                    curPage = 1;
                }
                showResult();
            }
        });
        navigationBar.add(firstButton);

        previousButton = new JButton(("<"));
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPage > 1)
                {
                    curPage--;
                    if(curPage > maxPage)
                    {
                        curPage = maxPage;
                    }
                }
                showResult();
            }
        });
        navigationBar.add(previousButton);

        pageButton = new JTextField("   ");
        pageButton.setHorizontalAlignment(JTextField.CENTER);
        pageButton.setPreferredSize(new Dimension(50, height));
        pageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    int pageNum = Integer.parseInt(pageButton.getText().replaceAll(" ", ""));
                    if(pageNum >= 1)
                    {
                        if(pageNum < maxPage)
                        {
                            curPage = pageNum;
                        }
                        else
                        {
                            curPage = maxPage;
                        }
                    }
                    showResult();
                }
                catch(NumberFormatException ex)
                {
                }
            }
        });
        navigationBar.add(pageButton);
        pageButton.setText(String.valueOf(curPage));

        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPage < maxPage)
                {
                    curPage++;
                }
                else
                {
                    curPage = maxPage;
                }
                showResult();
            }
        });
        navigationBar.add(nextButton);

        lastButton = new JButton(">|");
        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPage != maxPage)
                {
                    curPage = maxPage;
                }
                showResult();
            }
        });
        navigationBar.add(lastButton);

        // Create the middle panel
        middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        labelPanel = new JPanel(null);
        labelPanel.setPreferredSize(new Dimension(middlePanel.getWidth(), height));
        middlePanel.add(labelPanel, BorderLayout.NORTH);

        JLabelCreation.createLabels(labelPanel, titles, widths, height, widthSpace);

        infoPanel = new JPanel(null);
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        middlePanel.add(infoPanel, BorderLayout.CENTER);

        showResult();

        mainFrame.revalidate();;
        mainFrame.repaint();
    }

    /**
     * Draw the information on the panel
     */
    private void showResult()
    {
        // Clear the panel
        infoPanel.removeAll();

        // Draw each line of information
        for(int i = (curPage - 1) * 10; i < curPage * 10 && i < result.size(); i++)
        {
            // create each field for each order item
            createMenuItemField(result.get(i), i - (curPage - 1) * 10);
        }

        // Redraw the panel
        infoPanel.revalidate();
        infoPanel.repaint();

        // Get the total price and draw it in the bottom panel
        double totalPrice = 0.0;
        for(int i = 0; i < result.size(); i++)
        {
            totalPrice += result.get(i).getSecond() * frontOfHouse.getMenu().getMenuItem(result.get(i).getFirst()).getPrice();
        }
        bottomLabel.setText("Total Price: £" + String.valueOf(totalPrice));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    /**
     * Method for creating each line of information
     * @param menuItem The menu item
     * @param location The line location
     */
    private void createMenuItemField(Pair<String, Integer> menuItem, int location)
    {
        MenuItem menuItem1 = frontOfHouse.getMenu().getMenuItem(menuItem.getFirst());

        JLabel menuItemName = new JLabel(menuItem1.getName(), SwingConstants.CENTER);
        menuItemName.setPreferredSize(new Dimension(menuItemWidth, height));
        menuItemName.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), menuItemName.getPreferredSize()));
        infoPanel.add(menuItemName);

        JLabel amount = new JLabel(String.valueOf(menuItem.getSecond()), SwingConstants.CENTER);
        amount.setPreferredSize(new Dimension(amountWidth, height));
        amount.setBounds(new Rectangle(new Point(menuItemWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), amount.getPreferredSize()));
        infoPanel.add(amount);

        JLabel unitPrice = new JLabel("£" + String.valueOf(menuItem1.getPrice()), SwingConstants.CENTER);
        unitPrice.setPreferredSize(new Dimension(unitPriceWidth, height));
        unitPrice.setBounds(new Rectangle(new Point(menuItemWidth + amountWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), unitPrice.getPreferredSize()));
        infoPanel.add(unitPrice);

        JLabel price = new JLabel("£" + String.valueOf(menuItem.getSecond() * menuItem1.getPrice()), SwingConstants.CENTER);
        price.setPreferredSize(new Dimension(priceWidth, height));
        price.setBounds(new Rectangle(new Point(menuItemWidth + amountWidth + unitPriceWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), price.getPreferredSize()));
        infoPanel.add(price);
    }
}
