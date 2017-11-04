package kitchenAndBar;

import resources.JLabelCreation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <h1> Station Order List Frame</h1>
 * The frame showing the list of order
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StationOrderListFrame
{
    // The attribute
    private final String holder;
    private final KABOrder[] orderItemList;

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 20;
    private final int height = 20;

    private final int orderIDWidth = 50;
    private final int tableIDWidth = 50;
    private final int menuItemWidth = 100;
    private final int commentWidth = 300;
    private final int eTPWidth = 150;
    private final int statusWidth = 80;
    private final String[] titles = {"Order ID", "Table ID", "Menu Item ID", "Comment", "ETP", "Status"};
    private final int[] widths = {orderIDWidth, tableIDWidth, menuItemWidth, commentWidth, eTPWidth, statusWidth};

    private String searchText = "";

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;

    // result attribute
    private ArrayList<KABOrder> orderItemResult = new ArrayList<KABOrder>();

    // The components
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel buttonPanel;
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchTextField;

    private JPanel middlePanel;
    private JPanel middlePanelLabels;
    private JPanel middlePanelInfo;

    private JPanel bottomPanel;
    private JPanel navigationBar;
    private JButton firstButton;
    private JButton previousButton;
    private JTextField pageButton;
    private JButton nextButton;
    private JButton lastButton;
    private JLabel bottomLabel;


    public StationOrderListFrame(String holder, ArrayList<KABOrder> orderItemList)
    {
        this.holder = holder;
        this.orderItemList = orderItemList.toArray(new KABOrder[orderItemList.size()]);

        // Create the frame of the user interface
        mainFrame = new JFrame(holder + "'s Order Item List");
        mainFrame.setSize(900, 520);
        mainFrame.setResizable(false);

        // Create the panel to hold all other components
        mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Create the top panel
        topPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Create the button panel
        buttonPanel = new JPanel(new FlowLayout());
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // Create the search panels to hold the searching components
        searchPanel = new JPanel(new FlowLayout());
        topPanel.add(searchPanel, BorderLayout.EAST);

        searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);

        searchTextField = new JTextField(searchText);
        searchTextField.setPreferredSize(new Dimension(100, height));
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                searchText = searchTextField.getText();
                getResult();
                showResult();
            }
        });
        searchPanel.add(searchTextField);

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
                getResult();
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
                getResult();
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
                    getResult();
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
                getResult();
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
                getResult();
                if(curPage != maxPage)
                {
                    curPage = maxPage;
                }
                showResult();
            }
        });
        navigationBar.add(lastButton);

        // Create the middle panel to show information about the stock
        middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        // The labels panel of the stock
        middlePanelLabels = new JPanel();
        middlePanelLabels.setLayout(null);
        middlePanelLabels.setPreferredSize(new Dimension(mainPanel.getWidth(), height));
        middlePanel.add(middlePanelLabels, BorderLayout.NORTH);

        // Create the labels
        JLabelCreation.createLabels(middlePanelLabels, titles, widths, height, widthSpace);

        // The panel that
        middlePanelInfo = new JPanel();
        middlePanelInfo.setLayout(null);
        middlePanelInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        middlePanel.add(middlePanelInfo, BorderLayout.CENTER);

        // Method call for getting the information
        getResult();
        showResult();

        // Draw and show the frame
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    /**
     * Method for getting the information
     */
    private void getResult()
    {
        // empty result set
        orderItemResult = new ArrayList<KABOrder>();

        // check the search criteria
        if (!searchText.replaceAll(" ", "").equals(""))
        {
            for (int i = 0; i < orderItemList.length; i++)
            {
                if (orderItemList[i].getOrderID().toLowerCase().contains(searchText.toLowerCase()) || orderItemList[i].getOrderID().toLowerCase().contains(searchText.toLowerCase()) || orderItemList[i].getMenuItemID().toLowerCase().contains(searchText.toLowerCase()))
                {
                    orderItemResult.add(orderItemList[i]);
                }
            }
        }
        else
        {
            for(int i = 0; i < orderItemList.length; i++)
            {
                orderItemResult.add(orderItemList[i]);
            }
        }

        // set max page
        maxPage = (orderItemResult.size() - 1) / 10 + 1;

        // set current page if necessary
        if (curPage > maxPage) {
            curPage = maxPage;
        }
    }

    /**
     * Method for showing the information
     */
    private void showResult()
    {
        // remove all previous object
        middlePanelInfo.removeAll();

        for(int i = (curPage - 1) * 10; i < curPage * 10 && i < orderItemResult.size(); i++)
        {
            createOrderInfo(orderItemResult.get(i), i - (curPage - 1) * 10);
        }

        // Redraw the panel
        middlePanelInfo.revalidate();
        middlePanelInfo.repaint();

        pageButton.setText(String.valueOf(curPage));
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // updated time
        updateTime();
    }

    /**
     * Method for creating each line of information
     * @param order The order
     * @param location The location of the line
     */
    private void createOrderInfo(KABOrder order, int location)
    {
        // Creating the components
        JLabel orderID = new JLabel(order.getOrderID(), SwingConstants.CENTER);
        orderID.setPreferredSize(new Dimension(widths[0], height));
        orderID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), orderID.getPreferredSize()));
        middlePanelInfo.add(orderID);

        JLabel tableID = new JLabel(order.getTableID(), SwingConstants.CENTER);
        tableID.setPreferredSize(new Dimension(widths[1], height));
        tableID.setBounds(new Rectangle(new Point(widths[0] + widthSpace, heightSpace/2 + (height + heightSpace) * location), tableID.getPreferredSize()));
        middlePanelInfo.add(tableID);

        JLabel menuItem = new JLabel(order.getMenuItemID() + ":" + KitchenAndBar.instance().getMenu().getMenuItem(order.getMenuItemID()).getName(), SwingConstants.CENTER);
        menuItem.setPreferredSize(new Dimension(widths[2], height));
        menuItem.setBounds(new Rectangle(new Point(widths[0] + widths[1] + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), menuItem.getPreferredSize()));
        middlePanelInfo.add(menuItem);

        JLabel comment = new JLabel(order.getComment(), SwingConstants.CENTER);
        comment.setPreferredSize(new Dimension(widths[3], height));
        comment.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), comment.getPreferredSize()));
        middlePanelInfo.add(comment);

        if(order.getETP() != null)
        {
            JLabel eTP = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getETP()), SwingConstants.CENTER);
            eTP.setPreferredSize(new Dimension(widths[4], height));
            eTP.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + 4 * widthSpace, heightSpace / 2 + (height + heightSpace) * location), eTP.getPreferredSize()));
            middlePanelInfo.add(eTP);
        }

        JLabel status = new JLabel(KABOrder.status(order.getStatus()), SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(widths[5], height));
        status.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 5 * widthSpace, heightSpace/2 + (height + heightSpace) * location), status.getPreferredSize()));
        middlePanelInfo.add(status);
    }

    /**
     * Method for showing the last updated time
     */
    private void updateTime()
    {
        bottomLabel.setText("Updated at " + new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date()));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
