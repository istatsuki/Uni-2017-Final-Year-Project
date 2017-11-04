package kABStation;

import resources.Client;
import resources.JLabelCreation;
import resources.MenuItemFrame;
import resources.StatusButton;

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
 * <h1> Kitchen or Bar Station GUI</h1>
 * The user interface of a kitchen or bar station
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABStationGUI
{
    // The station system
    private KABStationInstance kABStationInstance;

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 5;
    private final int height = 20;

    private final int orderIDWidth = 50;
    private final int menuItemWidth = 100;
    private final int commentWidth = 300;
    private final int eTPWidth = 150;
    private final int statusWidth = 80;
    private final int statusButtonWidth = 120;
    private final String[] titles = {"Order ID", "Menu Item ID", "Comment", "ETP", "Status"};
    private final int[] widths = {orderIDWidth, menuItemWidth, commentWidth, eTPWidth, statusWidth};

    // The options attribute
    private String searchText = "";

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    // result attribute
    private ArrayList<KABStationOrder> orderItemResult = new ArrayList<KABStationOrder>();

    // The components
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel buttonPanel;
    private JButton refresh;
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

    /**
     * The constructor
     * @param kabStationInstance The station system
     */
    public KABStationGUI(KABStationInstance kabStationInstance)
    {
        this.kABStationInstance = kabStationInstance;
        String stationID = kabStationInstance.getStation().getStationID();

        // Create the frame of the user interface
        mainFrame = new JFrame(stationID);
        mainFrame.setSize(1040, 550);
        mainFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                mainFrame.setVisible(false);
                Client.clientToKAB("StationOffline/" + stationID);
                System.exit(0);
            }
        });
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

        // Create the refresh button
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

        ExecutorService executor = Executors.newFixedThreadPool(1);

        Runnable updateCheck = new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    if(timeUpdated.getTime() < Timestamp.valueOf(kABStationInstance.getTimeUpdated()).getTime())
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
        ArrayList<KABStationOrder> tempList = kABStationInstance.getStation().getOrderList();

        // empty result set
        orderItemResult = new ArrayList<KABStationOrder>();

        // check the search criteria
        if (!searchText.replaceAll(" ", "").equals(""))
        {
            for (int i = 0; i < tempList.size(); i++)
            {
                if (tempList.get(i).getOrderID().toLowerCase().contains(searchText.toLowerCase()) || tempList.get(i).getOrderID().toLowerCase().contains(searchText.toLowerCase()) || tempList.get(i).getMenuItemID().toLowerCase().contains(searchText.toLowerCase()))
                {
                    orderItemResult.add(tempList.get(i));
                }
            }
        }
        else
        {
            for(int i = 0; i < tempList.size(); i++)
            {
                orderItemResult.add(tempList.get(i));
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
     * Create each line of information
     * @param order The order
     * @param location The location of the line
     */
    private void createOrderInfo(KABStationOrder order, int location)
    {
        JLabel orderID = new JLabel(order.getOrderID(), SwingConstants.CENTER);
        orderID.setPreferredSize(new Dimension(widths[0], height));
        orderID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), orderID.getPreferredSize()));
        middlePanelInfo.add(orderID);

        if(order.getStatus() == KABStationOrder.HIGHPRIORITY)
        {
            orderID.setForeground(Color.RED);
        }

        JLabel menuItem = new JLabel(order.getMenuItemID(), SwingConstants.CENTER);
        menuItem.setPreferredSize(new Dimension(widths[1], height));
        menuItem.setBounds(new Rectangle(new Point(widths[0] + widthSpace, heightSpace/2 + (height + heightSpace) * location), menuItem.getPreferredSize()));
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MenuItemFrame menuItemFrame = new MenuItemFrame(kABStationInstance.getMenu().getMenuItem(order.getMenuItemID()), menuItem);
                menuItemFrame.show();
            }
        });
        middlePanelInfo.add(menuItem);

        JLabel comment = new JLabel(order.getComment(), SwingConstants.CENTER);
        comment.setPreferredSize(new Dimension(widths[2], height));
        comment.setBounds(new Rectangle(new Point(widths[0] + widths[1] + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), comment.getPreferredSize()));
        middlePanelInfo.add(comment);

        if(order.getETP() != null)
        {
            JLabel eTP = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getETP()), SwingConstants.CENTER);
            eTP.setPreferredSize(new Dimension(widths[3], height));
            eTP.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + 3 * widthSpace, heightSpace / 2 + (height + heightSpace) * location), eTP.getPreferredSize()));
            middlePanelInfo.add(eTP);
        }

        JLabel status = new JLabel(KABStationOrder.status(order.getStatus()), SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(widths[4], height));
        status.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + 4 * widthSpace, heightSpace/2 + (height + heightSpace) * location), status.getPreferredSize()));
        middlePanelInfo.add(status);

        if(order.getStatus() == KABStationOrder.PLACED || order.getStatus() == KABStationOrder.HIGHPRIORITY)
        {
            StatusButton prepare = new StatusButton("PREPARE");
            prepare.setPreferredSize(new Dimension(statusButtonWidth, height));
            prepare.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 5 * widthSpace, heightSpace/2 + (height + heightSpace) * location), prepare.getPreferredSize()));
            prepare.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order.getStatus() == KABStationOrder.PLACED || order.getStatus() == KABStationOrder.HIGHPRIORITY)
                    {
                        // Send requests to make changes
                        Client.clientToStation("ChangeOrderStatus/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + String.valueOf(KABStationOrder.PREPARING) + Client.infoSeperator + "true", kABStationInstance.getStation().getStationID(), kABStationInstance.getStation().getPortNum());
                        getResult();
                        showResult();
                    }
                }
            });
            middlePanelInfo.add(prepare);
        }

        if(order.getStatus() == KABStationOrder.PREPARING)
        {
            StatusButton done = new StatusButton("DONE");
            done.setPreferredSize(new Dimension(statusButtonWidth, height));
            done.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 5 * widthSpace, heightSpace/2 + (height + heightSpace) * location), done.getPreferredSize()));
            done.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order.getStatus() == KABStationOrder.PREPARING)
                    {
                        // Send requests to make changes
                        Client.clientToStation("ChangeOrderStatus/" + order.getOrderID() + Client.infoSeperator + order.getBookingID() + Client.infoSeperator + String.valueOf(KABStationOrder.PREPARED) + Client.infoSeperator + "true", kABStationInstance.getStation().getStationID(), kABStationInstance.getStation().getPortNum());
                        getResult();
                        showResult();
                    }
                }
            });
            middlePanelInfo.add(done);

            StatusButton failure = new StatusButton("FAIL");
            failure.setPreferredSize(new Dimension(statusButtonWidth, height));
            failure.setBounds(new Rectangle(new Point(widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + statusButtonWidth + 6 * widthSpace, heightSpace/2 + (height + heightSpace) * location), failure.getPreferredSize()));
            failure.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(order.getStatus() == KABStationOrder.PREPARING)
                    {
                        // send requests to make changes
                        Client.clientToKAB("Failure/" + order.getOrderID() + Client.infoSeperator + order.getBookingID());
                        getResult();
                        showResult();
                    }
                }
            });
            middlePanelInfo.add(failure);
        }
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
        bottomLabel.setText("Updated at " + new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date()));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
