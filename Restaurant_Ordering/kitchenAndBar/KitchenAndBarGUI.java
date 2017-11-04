package kitchenAndBar;

import resources.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Kitchen And Bar GUI</h1>
 * The user interface of the kitchen and bar sub-system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KitchenAndBarGUI
{
    // The kitchen and bar sub-system
    private KitchenAndBar kitchenAndBar = KitchenAndBar.instance();

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 5;
    private final int height = 20;

    private final int orderIDWidth = 50;
    private final int tableIDWidth = 50;
    private final int menuItemWidth = 200;
    private final int orderStationWidth = 80;
    private final int stationListWidth = 200;
    private final int changeStationButtonWidth = 80;
    private final int commentWidth = 300;
    private final int eTPWidth = 150;
    private final int statusWidth = 80;
    private final int statusButtonWidth = 80;
    private final String[] orderLabelTitles = {"Order ID", "Table ID", "Menu Item", "Station", "Comment", "ETP", "Status"};
    private final int[] orderLabelWidths = {orderIDWidth, tableIDWidth, menuItemWidth, orderStationWidth, commentWidth, eTPWidth, statusWidth};

    private final int stationIDWidth = 80;
    private final int stationNameWidth = 80;
    private final int stationOnlineStatusWidth = 80;
    private final int specialtyWidth = 200;
    private final int orderListButtonWidth = 100;
    private final String[] stationLabelTitles = {"Station ID", "Name", "Online Status", "Specialty"};
    private final int[] stationLabelWidths = {stationIDWidth, stationNameWidth, stationOnlineStatusWidth, specialtyWidth};

    // The options attributes
    private String searchText = "";
    private boolean orderShortage = false;
    private boolean orderHighPriority = false;
    private boolean orderLate = false;

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private final String orderPage = "Order";
    private final String stationPage = "Station";
    private String curPanel = orderPage;
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    // result attribute
    private ArrayList<Pair<KABOrder, String>> orderResult = new ArrayList<Pair<KABOrder, String>>();
    private ArrayList<Station> stationResult = new ArrayList<Station>();

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel buttonPanel;
    private JButton orderListButton;
    private JButton stationListButton;
    private JButton menuButton;
    private JButton refresh;
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

    private JPanel middlePanelOrder;
    private JPanel orderTopPanel;
    private JCheckBox orderShortageCB;
    private JCheckBox orderHighPriorityCB;
    private JCheckBox orderLateCB;
    private JPanel orderPanel;
    private JPanel orderLabels;
    private JPanel orderInfo;

    private JPanel middlePanelStation;
    private JPanel stationPanel;
    private JPanel stationLabels;
    private JPanel stationInfo;

    /**
     * The constructor
     */
    public KitchenAndBarGUI()
    {
        // Create the frame of the user interface
        mainFrame = new JFrame("Kitchen And Bar");
        mainFrame.setSize(1150, 560);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Create the buttons
        orderListButton = new JButton("Order");
        orderListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPanel != orderPage)
                {
                    searchText = "";
                    curPanel = orderPage;

                    mainFrame.setSize(new Dimension(1050, 560));

                    getResult();
                    showResult();

                    mainPanel.remove(middlePanelStation);
                    mainPanel.add(middlePanelOrder);

                    searchTextField.setText(searchText);

                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });
        buttonPanel.add(orderListButton);

        stationListButton = new JButton("Station");
        stationListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPanel != stationPage)
                {
                    searchText = "";
                    curPanel = stationPage;

                    mainFrame.setSize(new Dimension(850, 530));

                    getResult();
                    showResult();

                    mainPanel.remove(middlePanelOrder);
                    mainPanel.add(middlePanelStation);

                    searchTextField.setText(searchText);

                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });
        buttonPanel.add(stationListButton);

        menuButton = new JButton("Menu");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MenuFrame menuFrame = new MenuFrame(kitchenAndBar.getMenu().getMenuItemList(), MenuFrame.SHOW, "", "");
            }
        });
        buttonPanel.add(menuButton);

        refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAll();
            }
        });
        buttonPanel.add(refresh);

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

        // Create the middle panel sections
        createMiddlePanelOrder();
        createMiddlePanelStation();

        mainPanel.add(middlePanelOrder, BorderLayout.CENTER);

        // Draw and show the frame
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);

        // Thread for checking if there are updates
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Runnable updateCheck = new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    if(timeUpdated.getTime() < Timestamp.valueOf(kitchenAndBar.getTimeUpdated()).getTime())
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
     * Method for creating the order sections
     */
    private void createMiddlePanelOrder()
    {
        // Create the panel holding the section
        middlePanelOrder = new JPanel(new BorderLayout());
        middlePanelOrder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        // The top panel for viewing options
        orderTopPanel = new JPanel(new FlowLayout());
        middlePanelOrder.add(orderTopPanel, BorderLayout.NORTH);

        orderShortageCB = new JCheckBox("Shortage", false);
        orderTopPanel.add(orderShortageCB);

        orderHighPriorityCB = new JCheckBox("HighPriority", false);
        orderTopPanel.add(orderHighPriorityCB);

        orderLateCB = new JCheckBox("Late", false);
        orderTopPanel.add(orderLateCB);

        orderShortageCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                orderShortage = orderShortageCB.isSelected();
                if(orderShortageCB.isSelected())
                {
                    orderHighPriorityCB.setSelected(false);
                    orderLateCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        orderHighPriorityCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                orderHighPriority = orderHighPriorityCB.isSelected();
                if(orderHighPriorityCB.isSelected())
                {
                    orderShortageCB.setSelected(false);
                    orderLateCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        orderLateCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                orderLate = orderLateCB.isSelected();
                if(orderLateCB.isSelected())
                {
                    orderShortageCB.setSelected(false);
                    orderHighPriorityCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        // The middle panel for showing the information
        orderPanel = new JPanel(new BorderLayout());
        middlePanelOrder.add(orderPanel, BorderLayout.CENTER);

        orderLabels = new JPanel();
        orderLabels.setLayout(null);
        orderLabels.setPreferredSize(new Dimension(orderPanel.getWidth(), height));
        orderPanel.add(orderLabels, BorderLayout.NORTH);

        JLabelCreation.createLabels(orderLabels, orderLabelTitles, orderLabelWidths, height, widthSpace);

        orderInfo = new JPanel();
        orderInfo.setLayout(null);
        orderInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        orderPanel.add(orderInfo, BorderLayout.CENTER);

        getResult();
        showResult();
    }

    /**
     * Create the section for stations
     */
    private void createMiddlePanelStation()
    {
        // The panels holding the section
        middlePanelStation = new JPanel(new BorderLayout());
        middlePanelStation.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        stationPanel = new JPanel(new BorderLayout());
        middlePanelStation.add(stationPanel, BorderLayout.CENTER);

        stationLabels = new JPanel(null);
        stationLabels.setPreferredSize(new Dimension(stationPanel.getWidth(), height));
        stationPanel.add(stationLabels, BorderLayout.NORTH);

        JLabelCreation.createLabels(stationLabels, stationLabelTitles, stationLabelWidths, height, widthSpace);

        stationInfo = new JPanel(null);
        stationInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        stationPanel.add(stationInfo, BorderLayout.CENTER);

        getResult();
        showResult();
    }

    /**
     * Method for getting the information
     */
    private void getResult()
    {
        // Getting the correct information and filter with options
        if(curPanel == orderPage)
        {
            orderResult = new ArrayList<Pair<KABOrder, String>>();

            ArrayList<Pair<KABOrder, String>> tempList = kitchenAndBar.getOrderList();

            ArrayList<Pair<KABOrder, String>> orderList = new ArrayList<Pair<KABOrder, String>>();

            if(orderShortage)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    if(tempList.get(i).getFirst().getStatus() == KABOrder.SHORTAGE)
                    {
                        orderList.add(tempList.get(i));
                    }
                }
            }
            else if(orderHighPriority)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    if(tempList.get(i).getFirst().getStatus() == KABOrder.HIGHPRIORITY)
                    {
                        orderList.add(tempList.get(i));
                    }
                }
            }
            else if(orderLate)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    if(tempList.get(i).getFirst().getETP().getTime() < System.currentTimeMillis())
                    {
                        orderList.add(tempList.get(i));
                    }
                }
            }
            else
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    orderList.add(tempList.get(i));
                }
            }

            // check the search criteria
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
                    KABOrder order = orderList.get(i).getFirst();
                    if(order.getOrderID().toLowerCase().contains(searchText.toLowerCase())
                            || order.getMenuItemID().toLowerCase().contains(searchText.toLowerCase())
                            || kitchenAndBar.getMenu().getMenuItem(order.getMenuItemID()).getName().toLowerCase().contains(searchText.toLowerCase())
                            || KABOrder.status(order.getStatus()).toLowerCase().contains(searchText.toLowerCase())
                            || orderList.get(i).getSecond().toLowerCase().contains(searchText.toLowerCase())
                            || order.getComment().toLowerCase().contains(searchText.toLowerCase()))
                    {
                        orderResult.add(orderList.get(i));
                    }
                }
            }

            // set max page
            maxPage = (orderResult.size() - 1) / 10 + 1;
        }
        else
        {
            Station[] stationList = kitchenAndBar.getStationList();
            stationResult = new ArrayList<Station>();

            // check the search criteria
            if(searchText.replaceAll(" ", "").equals(""))
            {
                for(int i = 0; i < stationList.length;i++)
                {
                    stationResult.add(stationList[i]);
                }
            }
            else
            {
                for(int i = 0; i < stationList.length; i++)
                {
                    if(stationList[i].getStationID().toLowerCase().equals(searchText.toLowerCase()) || stationList[i].getName().toLowerCase().equals(searchText.toLowerCase()) || stationList[i].getSpecialtyList().toLowerCase().contains(searchText.toLowerCase()))
                    {
                        stationResult.add(stationList[i]);
                    }
                }
            }

            // set max page
            maxPage = (stationResult.size() - 1) / 10 + 1;
        }

        // set current page if necessary
        if (curPage > maxPage)
        {
            curPage = maxPage;
        }
    }

    /**
     * Method for showing the information
     */
    private void showResult()
    {
        // Showing the correct information
        if(curPanel == orderPage)
        {
            orderInfo.removeAll();

            for(int i = (curPage - 1) * 10; i < curPage * 10 && i < orderResult.size(); i++)
            {
                // create each field for each order item
                createOrderField(orderResult.get(i), i - (curPage - 1) * 10);
            }

            // Redraw the panel
            orderInfo.revalidate();
            orderInfo.repaint();
        }
        else
        {
            stationInfo.removeAll();

            for(int i = (curPage - 1) * 10; i < curPage * 10 && i < stationResult.size(); i++)
            {
                // create each field for each order item
                createStationField(stationResult.get(i), i - (curPage - 1) * 10);
            }

            // Redraw the panel
            stationInfo.revalidate();
            stationInfo.repaint();
        }

        pageButton.setText(String.valueOf(curPage));
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // updated time
        updateTime();
    }

    /**
     * Method for creating line of information
     * @param order The order
     * @param location The location of the line
     */
    private void createOrderField(Pair<KABOrder, String> order, int location)
    {
        // Getting the info
        KABOrder order1 = order.getFirst();
        String station = order.getSecond();

        // Creating the components
        JLabel orderID = new JLabel(order1.getOrderID(), SwingConstants.CENTER);
        orderID.setPreferredSize(new Dimension(orderIDWidth, height));
        orderID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), orderID.getPreferredSize()));
        orderInfo.add(orderID);

        if(order1.getStatus() == KABOrder.HIGHPRIORITY)
        {
            orderID.setForeground(Color.RED);
        }

        JLabel tableID = new JLabel(order1.getTableID(), SwingConstants.CENTER);
        tableID.setPreferredSize(new Dimension(tableIDWidth, height));
        tableID.setBounds(new Rectangle(new Point(orderIDWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), tableID.getPreferredSize()));
        orderInfo.add(tableID);

        JLabel menuItem = new JLabel(order1.getMenuItemID() + ":" + kitchenAndBar.getMenu().getMenuItem(order1.getMenuItemID()).getName(), SwingConstants.CENTER);
        menuItem.setPreferredSize(new Dimension(menuItemWidth, height));
        menuItem.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), menuItem.getPreferredSize()));
        orderInfo.add(menuItem);

        JLabel orderStation = new JLabel(station, SwingConstants.CENTER);
        orderStation.setPreferredSize(new Dimension(orderStationWidth, height));
        orderStation.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), orderStation.getPreferredSize()));
        orderInfo.add(orderStation);

        if(order1.getStatus() == KABOrder.PLACED || order1.getStatus() == KABOrder.PREPARING || order1.getStatus() == KABOrder.HIGHPRIORITY)
        {
            JComboBox<String> stationList = new JComboBox<String>();
            Station[] temporaryList = kitchenAndBar.getStationList();
            String foodTypeList = kitchenAndBar.getMenu().getMenuItem(order1.getMenuItemID()).getType().replaceAll(" ", "");
            for (int i = 0; i < temporaryList.length; i++)
            {
                if (temporaryList[i].getOnlineStatus() && !temporaryList[i].getStationID().equals(order.getSecond()))
                {
                    boolean contain = false;
                    String temp = foodTypeList;
                    while (temp.contains(";") && !contain)
                    {
                        String check = temp.substring(0, temp.indexOf(";"));
                        if (temporaryList[i].getSpecialtyList().contains(check))
                        {
                            stationList.addItem(temporaryList[i].getStationID() + ":" + temporaryList[i].getName());
                            contain = true;
                        }
                        temp = temp.substring(temp.indexOf(";") + 1);
                    }

                    if (temporaryList[i].getSpecialtyList().contains(temp) )
                    {
                        stationList.addItem(temporaryList[i].getStationID() + ":" + temporaryList[i].getName());
                    }
                }
            }
            stationList.setPreferredSize(new Dimension(stationListWidth, height));
            stationList.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + 3 * widthSpace, heightSpace / 2 + height + (height + heightSpace) * location), stationList.getPreferredSize()));
            orderInfo.add(stationList);

            JButton changeStationButton = new JButton("Change");
            changeStationButton.setPreferredSize(new Dimension(changeStationButtonWidth, height));
            changeStationButton.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + stationListWidth + 4 * widthSpace, heightSpace / 2 + height + (height + heightSpace) * location), changeStationButton.getPreferredSize()));
            changeStationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order1.getStatus() == KABOrder.PLACED || order1.getStatus() == KABOrder.PREPARING || order1.getStatus() == KABOrder.HIGHPRIORITY)
                    {
                        // Send requests to make changes
                        String stationID = String.valueOf(stationList.getSelectedItem()).substring(0, String.valueOf(stationList.getSelectedItem()).indexOf(":"));
                        Client.clientToKAB("ChangeOrderStation/" + order1.getOrderID() + Client.infoSeperator + order1.getBookingID() + Client.infoSeperator + stationID);
                        getResult();
                        showResult();
                    }
                }
            });
            orderInfo.add(changeStationButton);
        }

        JLabel comment = new JLabel(order1.getComment(), SwingConstants.CENTER);
        comment.setPreferredSize(new Dimension(commentWidth, height));
        comment.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + 4 * widthSpace, heightSpace/2 + (height + heightSpace) * location), comment.getPreferredSize()));
        orderInfo.add(comment);

        if(order1.getETP() != null)
        {
            JLabel eTP = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order1.getETP()), SwingConstants.CENTER);
            eTP.setPreferredSize(new Dimension(eTPWidth, height));
            eTP.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + commentWidth + 5 * widthSpace, heightSpace / 2 + (height + heightSpace) * location), eTP.getPreferredSize()));
            orderInfo.add(eTP);
        }

        JLabel status = new JLabel(KABOrder.status(order1.getStatus()), SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(statusWidth, height));
        status.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + commentWidth + eTPWidth + 6 * widthSpace, heightSpace/2 + (height + heightSpace) * location), status.getPreferredSize()));
        orderInfo.add(status);

        if(order1.getStatus() == KABOrder.PLACED || order1.getStatus() == KABOrder.HIGHPRIORITY || order1.getStatus() == KABOrder.SHORTAGE)
        {
            StatusButton refuse = new StatusButton("REFUSE");
            refuse.setPreferredSize(new Dimension(statusButtonWidth, height));
            refuse.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + commentWidth + eTPWidth + statusWidth + 7 * widthSpace, heightSpace/2 + (height + heightSpace) * location), refuse.getPreferredSize()));
            refuse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order1.getStatus() == KABOrder.PLACED || order1.getStatus() == KABOrder.HIGHPRIORITY || order1.getStatus() == KABOrder.SHORTAGE)
                    {
                        // Send requests to make changes
                        Client.clientToKAB("ChangeOrderStatus/" + order1.getOrderID() + Client.infoSeperator + order1.getBookingID() + Client.infoSeperator + String.valueOf(KABOrder.REFUSED) + Client.infoSeperator + "true" + Client.infoSeperator + "true");
                        getResult();
                        showResult();
                    }
                }
            });
            orderInfo.add(refuse);
        }

        if(order1.getStatus() == KABOrder.SHORTAGE)
        {
            StatusButton recheck = new StatusButton("RECHECK");
            recheck.setPreferredSize(new Dimension(statusButtonWidth, height));
            recheck.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + commentWidth + eTPWidth + statusWidth + statusButtonWidth + 8 * widthSpace, heightSpace/2 + (height + heightSpace) * location), recheck.getPreferredSize()));
            recheck.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order1.getStatus() == KABOrder.SHORTAGE)
                    {
                        // Send requests to make changes
                        Client.clientToKAB("CheckIngredient/" + order1.getOrderID() + Client.infoSeperator + order1.getBookingID());
                        getResult();
                        showResult();
                    }
                }
            });
            orderInfo.add(recheck);
        }

        if(order1.getStatus() == KABOrder.PREPARED)
        {
            StatusButton presented = new StatusButton("PRESENT");
            presented.setPreferredSize(new Dimension(statusButtonWidth, height));
            presented.setBounds(new Rectangle(new Point(orderIDWidth + tableIDWidth + menuItemWidth + orderStationWidth + commentWidth + eTPWidth + statusWidth + 7 * widthSpace, heightSpace/2 + (height + heightSpace) * location), presented.getPreferredSize()));
            presented.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order1.getStatus() == KABOrder.PREPARED)
                    {
                        // send requests to make changes
                        Client.clientToKAB("ChangeOrderStatus/" + order1.getOrderID() + Client.infoSeperator + order1.getBookingID() + Client.infoSeperator + String.valueOf(KABOrder.PRESENTED) + Client.infoSeperator + "true" + Client.infoSeperator + "false");
                        getResult();
                        showResult();
                    }
                }
            });
            orderInfo.add(presented);
        }
    }

    /**
     * Create each line of information
     * @param station The station
     * @param location The location of the line
     */
    private void createStationField(Station station, int location)
    {
        JLabel stationID = new JLabel(station.getStationID(), SwingConstants.CENTER);
        stationID.setPreferredSize(new Dimension(stationIDWidth, height));
        stationID.setBounds(new Rectangle(new Point(0,heightSpace/2 + (height + heightSpace) * location), stationID.getPreferredSize()));
        stationInfo.add(stationID);

        JLabel stationName = new JLabel(station.getName(), SwingConstants.CENTER);
        stationName.setPreferredSize(new Dimension(stationNameWidth, height));
        stationName.setBounds(new Rectangle(new Point(stationIDWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), stationName.getPreferredSize()));
        stationInfo.add(stationName);

        JLabel stationOnline = new JLabel(Station.online(station.getOnlineStatus()), SwingConstants.CENTER);
        stationOnline.setPreferredSize(new Dimension(stationOnlineStatusWidth, height));
        stationOnline.setBounds(new Rectangle(new Point(stationIDWidth + stationNameWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), stationOnline.getPreferredSize()));
        stationInfo.add(stationOnline);

        JLabel stationSpecialty = new JLabel(station.getSpecialtyList(), SwingConstants.CENTER);
        stationSpecialty.setPreferredSize(new Dimension(specialtyWidth, height));
        stationSpecialty.setBounds(new Rectangle(new Point(stationIDWidth + stationNameWidth + + stationOnlineStatusWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), stationSpecialty.getPreferredSize()));
        stationInfo.add(stationSpecialty);

        JButton orderListButton = new JButton("Order List");
        orderListButton.setPreferredSize(new Dimension(orderListButtonWidth, height));
        orderListButton.setBounds(new Rectangle(new Point(stationIDWidth + stationNameWidth + stationOnlineStatusWidth + specialtyWidth + 4 * widthSpace,heightSpace/2 + (height + heightSpace) * location), orderListButton.getPreferredSize()));
        orderListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ArrayList<Pair<String, String>> orderIDList = station.getOrderList();
                ArrayList<KABOrder> orderList = new ArrayList<KABOrder>();

                for(int i = 0; i < orderIDList.size(); i++)
                {
                    orderList.add(kitchenAndBar.getOrder(orderIDList.get(i).getFirst(), orderIDList.get(i).getSecond()).getFirst());
                }

                StationOrderListFrame stationOrderListFrame = new StationOrderListFrame(station.getStationID(), orderList);
            }
        });
        stationInfo.add(orderListButton);
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
}
