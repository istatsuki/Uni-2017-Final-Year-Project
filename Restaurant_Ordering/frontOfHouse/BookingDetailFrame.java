package frontOfHouse;

import resources.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * <h1> Booking detail frame</h1>
 * The class for showing the deatil of a booking
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class BookingDetailFrame
{
    // The attributes
    private FrontOfHouse frontOfHouse = FrontOfHouse.instance();
    private String bookingID;
    private boolean bookingActive;

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 5;
    private final int height = 20;

    private final int orderIDWidth = 50;
    private final int menuItemWidth = 100;
    private final int commentWidth = 300;
    private final int eTPWidth = 150;
    private final int statusWidth = 80;
    private final int statusButtonWidth = 80;
    private final String[] titles = {"Order ID", "Menu Item ID", "Comment", "ETP", "Status"};
    private final int[] widths = {orderIDWidth, menuItemWidth, commentWidth, eTPWidth, statusWidth};

    // The options value
    private String searchText = "";
    private String curTable = "";

    // result attribute
    private ArrayList<FOHOrder> orderResult = new ArrayList<FOHOrder>();

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private final String orderPage = "Order";
    private String curPanel = orderPage;
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel buttonPanel;
    private JButton refresh;
    private JComboBox<String> addTableList;
    private JButton addTable;
    private JComboBox<String> tableList;
    private JButton goTable;
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchTextField;

    private JPanel bottomPanel;
    private JPanel navigationBar;
    private JButton firstButton;
    private JButton previousButton;
    private JTextField pageButton;
    private JButton nextButton;
    private JButton lastButton;
    private JLabel bottomLabel;

    private JPanel middlePanel;
    private JPanel middleButtonPanel;
    private JComboBox<String> tableListCB;
    private JButton changeTable;
    private JButton removeTable;
    private JButton addOrder;
    private JPanel orderPanel;
    private JPanel orderLabel;
    private JPanel orderInfo;

    /**
     * Constructor
     * @param bookingID The booking ID of the booking
     */
    public BookingDetailFrame(String bookingID)
    {
        // Set the attributes
        this.bookingID = bookingID;
        Booking booking = frontOfHouse.getBooking(bookingID);

        // Check if the booking is active or not
        if(booking.getStatus() == Booking.PLACED || booking.getStatus() == Booking.FINISHED)
        {
            this.bookingActive = true;
        }

        // Create the main frame
        mainFrame = new JFrame(bookingID);
        mainFrame.setSize(1050, 560);
        mainFrame.setResizable(false);

        // Create the panel to hold all other components
        mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Create the top panel
        topPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Create the button panel
        buttonPanel = new JPanel(new FlowLayout());
        topPanel.add(buttonPanel, BorderLayout.WEST);

        refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAll();
            }
        });
        buttonPanel.add(refresh);

        addTableList = new JComboBox<String>();
        addTableList.setPrototypeDisplayValue("TableID here");
        buttonPanel.add(addTableList);

        addTable = new JButton("Add Table");
        addTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    // send request to make changes
                    String outputText = "AddBookingTable/" + bookingID + Client.infoSeperator + addTableList.getSelectedItem();
                    String reply = Client.clientToFOH(outputText);
                    if (reply.contains("Booking Table Added"))
                    {
                        getResult();
                        showResult();
                    }
                    else
                    {
                        ErrorFrame errorFrame = new ErrorFrame("Table is occupied");
                    }
                }
            }
        });
        buttonPanel.add(addTable);

        tableList = new JComboBox<String>();
        tableList.setPrototypeDisplayValue("TableID here");
        buttonPanel.add(tableList);

        goTable = new JButton("Go");
        goTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    if (tableList.getSelectedItem() != null)
                    {
                        if (!curTable.equals(String.valueOf(tableList.getSelectedItem())))
                        {
                            curTable = String.valueOf(tableList.getSelectedItem());
                            getResult();
                            showResult();
                        }
                    }
                }
            }
        });
        buttonPanel.add(goTable);

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

            public void act()
            {
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

        // Create the middle panel
        middlePanel = new JPanel(new BorderLayout());
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        middleButtonPanel = new JPanel(new FlowLayout());
        middlePanel.add(middleButtonPanel, BorderLayout.NORTH);

        tableListCB = new JComboBox<String>();
        tableListCB.setPrototypeDisplayValue("TableID here");
        middleButtonPanel.add(tableListCB);

        changeTable = new JButton("Change");
        changeTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    if (curTable != null && !curTable.equals(""))
                    {
                        // send requests to make changes
                        String outputText = "ChangeBookingTable/" + bookingID + Client.infoSeperator + curTable + Client.infoSeperator + tableListCB.getSelectedItem();
                        String reply = Client.clientToFOH(outputText);
                        if (reply.contains("Booking Table Changed"))
                        {
                            curTable = String.valueOf(tableListCB.getSelectedItem());
                            getResult();
                            showResult();
                        }
                        else
                        {
                            ErrorFrame errorFrame = new ErrorFrame("Table is occupied");
                        }
                    }
                }
            }
        });
        middleButtonPanel.add(changeTable);

        removeTable = new JButton("Remove");
        removeTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    if (frontOfHouse.getBooking(bookingID).getTableList().size() > 1)
                    {
                        JFrame newFrame = new JFrame("Remove Table");
                        newFrame.setSize(300, 200);
                        newFrame.setVisible(true);

                        JPanel mainPanel = new JPanel(new BorderLayout());
                        newFrame.add(mainPanel);

                        JPanel topPanel = new JPanel(new FlowLayout());
                        mainPanel.add(topPanel, BorderLayout.NORTH);

                        JLabel label1 = new JLabel("Send Order to :");
                        topPanel.add(label1);

                        ArrayList<String> tableList = frontOfHouse.getBooking(bookingID).getTableList();
                        JComboBox<String> tableCB = new JComboBox<String>();
                        for (int i = 0; i < tableList.size(); i++)
                        {
                            if (!tableList.get(i).equals(curTable))
                            {
                                tableCB.addItem(tableList.get(i));
                            }
                        }
                        tableCB.setPrototypeDisplayValue("tableID here");
                        topPanel.add(tableCB);

                        JPanel middlePanel = new JPanel(new FlowLayout());
                        mainPanel.add(middlePanel, BorderLayout.CENTER);

                        JCheckBox keepAll = new JCheckBox("Keep all Order");
                        JCheckBox removePossibleOrders = new JCheckBox("Remove Possible Orders");

                        keepAll.setSelected(true);
                        keepAll.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (keepAll.isSelected()) {
                                    removePossibleOrders.setSelected(false);
                                }
                                else if (!removePossibleOrders.isSelected())
                                {
                                    keepAll.setSelected(true);
                                }
                            }
                        });

                        removePossibleOrders.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (removePossibleOrders.isSelected())
                                {
                                    keepAll.setSelected(false);
                                }
                                else if (!keepAll.isSelected())
                                {
                                    removePossibleOrders.setSelected(true);
                                }
                            }
                        });

                        middlePanel.add(keepAll);
                        middlePanel.add(removePossibleOrders);

                        JPanel bottomPanel = new JPanel(new FlowLayout());
                        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                        JButton removeTable = new JButton("Remove");
                        removeTable.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                boolean keepAll = true;
                                if (removePossibleOrders.isSelected())
                                {
                                    keepAll = false;
                                }
                                // send requests to make changes
                                Client.clientToFOH("RemoveBookingTable/" + bookingID + Client.infoSeperator + curTable + Client.infoSeperator + tableCB.getSelectedItem() + Client.infoSeperator + String.valueOf(keepAll));
                                curTable = String.valueOf(tableCB.getSelectedItem());
                                getResult();
                                showResult();
                                newFrame.setVisible(false);
                            }
                        });
                        bottomPanel.add(removeTable);
                    }
                }
            }
        });
        middleButtonPanel.add(removeTable);

        addOrder = new JButton("Add Order");
        addOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    if (!curTable.equals("") && curTable != null)
                    {
                        MenuFrame menuFrame = new MenuFrame(frontOfHouse.getMenu().getMenuItemList(), MenuFrame.NEWORDER, bookingID, curTable);
                    }
                }
            }
        });
        middleButtonPanel.add(addOrder);

        orderPanel = new JPanel(new BorderLayout());
        middlePanel.add(orderPanel, BorderLayout.CENTER);

        orderLabel = new JPanel(null);
        orderLabel.setPreferredSize(new Dimension(orderPanel.getWidth(), height));
        orderPanel.add(orderLabel, BorderLayout.NORTH);

        JLabelCreation.createLabels(orderLabel, titles, widths, height, widthSpace);

        orderInfo = new JPanel(null);
        orderInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        orderPanel.add(orderInfo, BorderLayout.CENTER);

        getResult();
        showResult();

        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);

        // Constant checking if there are changes
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Runnable updateCheck = new Runnable() {
            @Override
            public void run()
            {
                while(bookingActive)
                {
                    if(timeUpdated.getTime() < Timestamp.valueOf(frontOfHouse.getTimeUpdated()).getTime())
                    {
                        refresh.setForeground(Color.RED);
                    }
                    else
                    {
                        refresh.setForeground(Color.BLACK);
                    }
                }
            }
        };

        executor.execute(updateCheck);
    }

    /**
     * Method for getting the information
     */
    private void getResult()
    {
        // Get the information
        Booking booking = frontOfHouse.getBooking(this.bookingID);
        ArrayList<String> bookingTableList = booking.getTableList();
        orderResult = new ArrayList<FOHOrder>();

        // Filter the information
        if(!bookingTableList.isEmpty())
        {
            if(curTable.equals(""))
            {
                curTable = bookingTableList.get(0);
            }

            ArrayList<FOHOrder> tempList = booking.getOrderList();
            ArrayList<FOHOrder> orderList = new ArrayList<FOHOrder>();

            for(int i = 0; i < tempList.size(); i++)
            {
                if(tempList.get(i).getTableID().equals(curTable))
                {
                    orderList.add(tempList.get(i));
                }
            }

            if(searchText.replaceAll(" ", "").equals(""))
            {
                for(int i = orderList.size() - 1; i >= 0; i--)
                {
                    // create each field for each order item
                    orderResult.add(orderList.get(i));
                }
            }
            else
            {
                for(int i = orderList.size() - 1; i >= 0; i--)
                {
                    FOHOrder order = orderList.get(i);
                    if(order.getOrderID().toLowerCase().contains(searchText.toLowerCase())
                            || order.getMenuItemID().toLowerCase().contains(searchText.toLowerCase())
                            || frontOfHouse.getMenu().getMenuItem(order.getMenuItemID()).getName().toLowerCase().contains(searchText.toLowerCase())
                            || FOHOrder.status(order.getStatus()).toLowerCase().contains(searchText.toLowerCase())
                            || order.getComment().toLowerCase().contains(searchText.toLowerCase()))
                    {
                        orderResult.add(orderList.get(i));
                    }
                }

            }

            // set max page
            maxPage = (orderResult.size() - 1) / 10 + 1;

            // set current page if necessary
            if (curPage > maxPage)
            {
                curPage = maxPage;
            }
        }
    }

    /**
     * Method for drawing the information
     */
    private void showResult()
    {
        // Clear the panels
        addTableList.removeAllItems();
        tableList.removeAllItems();
        orderInfo.removeAll();
        tableListCB.removeAllItems();

        int index = -1;

        // drawing the information
        ArrayList<String> bookingTableList = frontOfHouse.getBooking(this.bookingID).getTableList();
        for(int i = 0; i< bookingTableList.size(); i++)
        {
            tableList.addItem(bookingTableList.get(i));
            if(bookingTableList.get(i).equals(curTable))
            {
                index = i;
            }
        }
        if(index != -1)
        {
            tableList.setSelectedIndex(index);
        }
        tableList.revalidate();
        tableList.repaint();

        Table[] tableList = frontOfHouse.getTableList();
        for(int i = 0; i < tableList.length; i++)
        {
            if(tableList[i].getAvailability())
            {
                tableListCB.addItem(tableList[i].getTableID());
                addTableList.addItem(tableList[i].getTableID());
            }
        }

        tableListCB.revalidate();
        tableListCB.repaint();
        addTableList.revalidate();
        addTableList.repaint();

        for(int i = (curPage - 1) * 10; i < curPage * 10 && i < orderResult.size(); i++)
        {
            // create each field for each order item
            createOrderField(orderResult.get(i), i - (curPage - 1) * 10);
        }

        // Redraw the panel
        orderInfo.revalidate();
        orderInfo.repaint();

        pageButton.setText(String.valueOf(curPage));
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // updated time
        updateTime();
    }

    /**
     * Method for making each line of information
     * @param order The order
     * @param location The location of the order
     */
    private void createOrderField(FOHOrder order, int location)
    {
        // Creating each components of the line
        JLabel orderID = new JLabel(order.getOrderID(), SwingConstants.CENTER);
        orderID.setPreferredSize(new Dimension(widths[0], height));
        orderID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), orderID.getPreferredSize()));
        orderInfo.add(orderID);

        if(order.getStatus() == FOHOrder.PREPARED)
        {
            orderID.setForeground(Color.RED);
        }

        JLabel menuItem = new JLabel(order.getMenuItemID() + ":" + frontOfHouse.getMenu().getMenuItem(order.getMenuItemID()).getName(), SwingConstants.CENTER);
        menuItem.setPreferredSize(new Dimension(widths[1], height));
        menuItem.setBounds(new Rectangle(new Point(widths[0]  +  widthSpace, heightSpace/2 + (height + heightSpace) * location), menuItem.getPreferredSize()));
        orderInfo.add(menuItem);

        JLabel comment = new JLabel(order.getComment(), SwingConstants.CENTER);
        comment.setPreferredSize(new Dimension(widths[2], height));
        comment.setBounds(new Rectangle(new Point(widths[0] + widths[1]  + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), comment.getPreferredSize()));
        orderInfo.add(comment);

        if(order.getETP() != null)
        {
            JLabel eTP = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getETP()), SwingConstants.CENTER);
            eTP.setPreferredSize(new Dimension(widths[3], height));
            eTP.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + 3 * widthSpace, heightSpace / 2 + (height + heightSpace) * location), eTP.getPreferredSize()));
            orderInfo.add(eTP);
        }

        JLabel status = new JLabel(FOHOrder.status(order.getStatus()), SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(widths[4], height));
        status.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + 4 * widthSpace, heightSpace/2 + (height + heightSpace) * location), status.getPreferredSize()));
        orderInfo.add(status);

        if(order.getStatus() == FOHOrder.SHORTAGE || order.getStatus() == FOHOrder.PLACED || order.getStatus() == FOHOrder.HIGHPRIORITY)
        {
            StatusButton cancel = new StatusButton("CANCEL");
            cancel.setPreferredSize(new Dimension(statusButtonWidth, height));
            cancel.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 5 * widthSpace, heightSpace/2 + (height + heightSpace) * location), cancel.getPreferredSize()));
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(bookingActive)
                    {
                        if(order.getStatus() == FOHOrder.SHORTAGE || order.getStatus() == FOHOrder.PLACED || order.getStatus() == FOHOrder.HIGHPRIORITY)
                        {
                            // send requests to make changes
                            Client.clientToFOH("ChangeOrderStatus/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + String.valueOf(FOHOrder.CANCELLED) + Client.infoSeperator + "true");
                            getResult();
                            showResult();
                        }
                    }

                }
            });
            orderInfo.add(cancel);
        }

        if(order.getStatus() == FOHOrder.PLACED)
        {
            StatusButton error = new StatusButton("HIGH");
            error.setPreferredSize(new Dimension(statusButtonWidth, height));
            error.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + statusButtonWidth + 6 * widthSpace, heightSpace/2 + (height + heightSpace) * location), error.getPreferredSize()));
            error.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(bookingActive)
                    {
                        if(order.getStatus() == FOHOrder.PLACED)
                        {
                            // send requests to make changes
                            Client.clientToFOH("ChangeOrderStatus/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + String.valueOf(FOHOrder.HIGHPRIORITY) + Client.infoSeperator + "true");
                            getResult();
                            showResult();
                        }
                    }

                }
            });
            orderInfo.add(error);
        }

        if(order.getStatus() == FOHOrder.PREPARING || order.getStatus() == FOHOrder.PREPARED || order.getStatus() == FOHOrder.PRESENTED)
        {
            StatusButton error = new StatusButton("ERROR");
            error.setPreferredSize(new Dimension(statusButtonWidth, height));
            error.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + statusButtonWidth + 6 * widthSpace, heightSpace/2 + (height + heightSpace) * location), error.getPreferredSize()));
            error.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(bookingActive)
                    {
                        if(order.getStatus() == FOHOrder.PREPARING || order.getStatus() == FOHOrder.PREPARED || order.getStatus() == FOHOrder.PRESENTED)
                        {
                            // send requests to make changes
                            Client.clientToFOH("ChangeOrderStatus/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + String.valueOf(FOHOrder.ERROR) + Client.infoSeperator + "true");
                            getResult();
                            showResult();
                        }
                    }

                }
            });
            orderInfo.add(error);
        }

        JButton changeOrderDetail = new JButton("Change");
        changeOrderDetail.setPreferredSize(new Dimension(statusButtonWidth, height));
        changeOrderDetail.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 2 * statusButtonWidth + 7 * widthSpace, heightSpace/2 + (height + heightSpace) * location), changeOrderDetail.getPreferredSize()));
        changeOrderDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(bookingActive)
                {
                    JFrame newFrame = new JFrame(order.getOrderID());
                    newFrame.setSize(500, 200);
                    newFrame.setVisible(true);

                    JPanel panel = new JPanel(new BorderLayout());
                    JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    newFrame.add(scrollPane);

                    JPanel topPanel = new JPanel(new FlowLayout());
                    topPanel.setPreferredSize(new Dimension(panel.getWidth(), 60));
                    panel.add(topPanel, BorderLayout.NORTH);

                    JLabel tableIDLabel = new JLabel("Table ID:");
                    tableIDLabel.setPreferredSize(new Dimension(100, 30));
                    topPanel.add(tableIDLabel);

                    JTextField tableIDValue = new JTextField(order.getTableID());
                    tableIDValue.setPreferredSize(new Dimension(100, 30));
                    tableIDValue.setEditable(false);
                    topPanel.add(tableIDValue);

                    JLabel menuItemIDLabel = new JLabel("Menu Item ID:");
                    menuItemIDLabel.setPreferredSize(new Dimension(100, 30));
                    topPanel.add(menuItemIDLabel);

                    JTextField menuItemIDValue = new JTextField(order.getMenuItemID());
                    menuItemIDValue.setPreferredSize(new Dimension(100, 30));
                    menuItemIDValue.setEditable(false);
                    topPanel.add(menuItemIDValue);

                    JPanel middlePanel = new JPanel(new FlowLayout());
                    middlePanel.setPreferredSize(new Dimension(panel.getWidth(), 60));
                    panel.add(middlePanel, BorderLayout.CENTER);

                    JLabel commentLabel = new JLabel("Comment:");
                    commentLabel.setPreferredSize(new Dimension(100, 30));
                    middlePanel.add(commentLabel);

                    JTextField commentValue = new JTextField(order.getComment());
                    commentValue.setPreferredSize(new Dimension(300, 30));
                    middlePanel.add(commentValue);

                    JPanel bottomPanel = new JPanel(new FlowLayout());
                    bottomPanel.setPreferredSize(new Dimension(panel.getWidth(), 60));
                    panel.add(bottomPanel, BorderLayout.SOUTH);

                    JButton change = new JButton("Change");
                    change.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            String newTableIDValue = tableIDValue.getText();
                            String newCommentValue = commentValue.getText();

                            if(!newTableIDValue.equalsIgnoreCase(order.getTableID()))
                            {
                                // send requests to make changes
                                Client.clientToFOH("ChangeOrderTable/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + newTableIDValue);
                            }
                            if(!newCommentValue.equalsIgnoreCase(order.getComment()))
                            {
                                // send requests to make changes
                                Client.clientToFOH("ChangeOrderComment/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + newCommentValue);
                            }
                            newFrame.setVisible(false);
                        }
                    });
                    bottomPanel.add(change);
                }

            }
        });
        orderInfo.add(changeOrderDetail);
    }

    /**
     * Method for refreshing the user interface
     */
    private void refreshAll()
    {
        searchText = "";

        getResult();
        showResult();
        searchTextField.setText(searchText);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * Method for showing the last updated time
     */
    private void updateTime()
    {
        timeUpdated = new Timestamp(System.currentTimeMillis());
        bottomLabel.setText("Updated at " + new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(timeUpdated));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
