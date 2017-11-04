package frontOfHouse;

import resources.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Front Of House GUI</h1>
 * The front of house user interface
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FrontOfHouseGUI
{
    // The Front of House sub-system
    FrontOfHouse frontOfHouse = FrontOfHouse.instance();

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 0;
    private final int height = 20;

    private final int bookingIDWidth = 120;
    private final int bookerNameWidth = 80;
    private final int contactWidth = 150;
    private final int tableRequestedWidth = 60;
    private final int bookingTableIDWidth = 100;
    private final int dateWidth = 150;
    private final int commentWidth = 250;
    private final int statusWidth = 80;
    private final int statusButtonWidth = 50;
    private final int detailButtonWidth = 80;
    private final String[] bookingLabelTitles = {"Booking ID", "Booker Name", "Contact", "Tables", "Tables ID", "Date", "Comment", "Status"};
    private final int[] bookingLabelWidths = {bookingIDWidth, bookerNameWidth, contactWidth, tableRequestedWidth, bookingTableIDWidth, dateWidth, commentWidth, statusWidth};

    private final int tableIDWidth = 80;
    private final int tableSizeWidth = 40;
    private final int tableAvailabilityWidth = 80;
    private final int tableProblemWidth = 300;
    private final int tableAvailabilityButtonWidth = 100;
    private final int tableProblemTFWidth = 300;
    private final String[] tableLabelTitles = {"Table ID", "Size", "Availability", "Problem"};
    private final int[] tableLabelWidths = {tableIDWidth, tableSizeWidth, tableAvailabilityWidth, tableProblemWidth};

    // The options attribute
    private String searchText = "";
    private boolean bookingActive = true;
    private boolean bookingInactive = false;
    private boolean bookingAlert = false;
    private boolean tableAvailable = false;

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private final String bookingPage = "Booking";
    private final String tablePage = "Table";
    private String curPanel = bookingPage;
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    // result attribute
    private ArrayList<Booking> bookingResult = new ArrayList<Booking>();
    private ArrayList<Table> tableResult = new ArrayList<Table>();

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel buttonPanel;
    private JButton bookingListButton;
    private JButton tableListButton;
    private JButton menuListButton;
    private JButton refresh;
    private JButton createBookingButton;
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

    private JPanel middlePanelBooking;
    private JPanel bookingTopPanel;
    private JCheckBox bookingActiveCB;
    private JCheckBox bookingInactiveCB;
    private JCheckBox bookingAlertCB;
    private JPanel bookingPanel;
    private JPanel bookingLabels;
    private JPanel bookingInfo;

    private JPanel middlePanelTable;
    private JPanel tableTopPanel;
    private JCheckBox tableAvailableCB;
    private JPanel tablePanel;
    private JPanel tableLabels;
    private JPanel tableInfo;

    /**
     * Constructor
     */
    public FrontOfHouseGUI()
    {
        // Create the frame of the user interface
        mainFrame = new JFrame("Front Of House");
        mainFrame.setSize(1200, 560);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        mainFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                frontOfHouse.closeDown();
            }
        });

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
        bookingListButton = new JButton("Booking List");
        bookingListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPanel != bookingPage)
                {
                    searchText = "";
                    curPanel = bookingPage;

                    mainFrame.setSize(1200, 560);

                    getResult();
                    showResult();

                    mainPanel.remove(middlePanelTable);
                    mainPanel.add(middlePanelBooking);

                    searchTextField.setText(searchText);

                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });
        buttonPanel.add(bookingListButton);

        tableListButton = new JButton("Table List");
        tableListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(curPanel != tablePage)
                {
                    searchText = "";
                    curPanel = tablePage;

                    mainFrame.setSize(new Dimension(650, 530));

                    getResult();
                    showResult();

                    mainPanel.remove(middlePanelBooking);
                    mainPanel.add(middlePanelTable);

                    searchTextField.setText(searchText);

                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });
        buttonPanel.add(tableListButton);

        menuListButton = new JButton("Menu");
        menuListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MenuFrame menuFrame = new MenuFrame(frontOfHouse.getMenu().getMenuItemList(), MenuFrame.SHOW, "", "");
            }
        });
        buttonPanel.add(menuListButton);

        createBookingButton = new JButton("New Booking");
        createBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                NewBookingFrame newBookingFrame = new NewBookingFrame();
            }
        });
        buttonPanel.add(createBookingButton);

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

        // Create different middle panels
        createMiddlePanelBooking();
        createMiddlePanelTable();

        // set the current main panel
        mainPanel.add(middlePanelBooking, BorderLayout.CENTER);

        // Draw and show the frame
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);

        // Multi-thread for check for update and update the database
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable updateCheck = new Runnable() {
            @Override
            public void run()
            {
                while(true)
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
     * Method fr creating the booking middle panel
     */
    private void createMiddlePanelBooking()
    {
        // Create the middle panel
        middlePanelBooking = new JPanel(new BorderLayout());
        middlePanelBooking.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        // The top panel for setting the showing options
        bookingTopPanel = new JPanel(new FlowLayout());
        middlePanelBooking.add(bookingTopPanel, BorderLayout.NORTH);

        bookingActiveCB = new JCheckBox("Active", true);
        bookingTopPanel.add(bookingActiveCB);

        bookingInactiveCB = new JCheckBox("Inactive", false);
        bookingTopPanel.add(bookingInactiveCB);

        bookingAlertCB = new JCheckBox("<HTML>Alert(<font color='red'>" + String.valueOf(frontOfHouse.getAlertNum()) + "</font>)</HTML>", false);
        bookingTopPanel.add(bookingAlertCB);

        bookingActiveCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                bookingActive = bookingActiveCB.isSelected();
                if(bookingActiveCB.isSelected())
                {
                    bookingInactiveCB.setSelected(false);
                    bookingAlertCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        bookingInactiveCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                bookingInactive = bookingInactiveCB.isSelected();
                if(bookingInactiveCB.isSelected())
                {
                    bookingActiveCB.setSelected(false);
                    bookingAlertCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        bookingAlertCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                bookingAlert = bookingAlertCB.isSelected();
                if(bookingAlertCB.isSelected())
                {
                    bookingActiveCB.setSelected(false);
                    bookingInactiveCB.setSelected(false);
                }
                getResult();
                showResult();
            }
        });

        bookingPanel = new JPanel(new BorderLayout());
        middlePanelBooking.add(bookingPanel, BorderLayout.CENTER);

        // middle panels for showing the information
        bookingLabels = new JPanel();
        bookingLabels.setLayout(null);
        bookingLabels.setPreferredSize(new Dimension(bookingPanel.getWidth(), height));
        bookingPanel.add(bookingLabels, BorderLayout.NORTH);

        JLabelCreation.createLabels(bookingLabels, bookingLabelTitles, bookingLabelWidths, height, widthSpace);

        bookingInfo = new JPanel();
        bookingInfo.setLayout(null);
        bookingInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        bookingPanel.add(bookingInfo, BorderLayout.CENTER);

        getResult();
        showResult();
    }

    /**
     * Method for creating the table middle panel
     */
    private void createMiddlePanelTable()
    {
        // Create the middle panel
        middlePanelTable = new JPanel(new BorderLayout());
        middlePanelTable.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        // Top panel for showing options
        tableTopPanel = new JPanel(new FlowLayout());
        middlePanelTable.add(tableTopPanel, BorderLayout.NORTH);

        tableAvailableCB = new JCheckBox("Available", false);
        tableAvailableCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tableAvailable = tableAvailableCB.isSelected();
                getResult();
                showResult();
            }
        });
        tableTopPanel.add(tableAvailableCB);

        // middle panel for showing information
        tablePanel = new JPanel(new BorderLayout());
        middlePanelTable.add(tablePanel, BorderLayout.CENTER);

        tableLabels = new JPanel();
        tableLabels.setLayout(null);
        tableLabels.setPreferredSize(new Dimension(tablePanel.getWidth(), height));
        tablePanel.add(tableLabels, BorderLayout.NORTH);

        JLabelCreation.createLabels(tableLabels, tableLabelTitles, tableLabelWidths, height, widthSpace);

        tableInfo = new JPanel();
        tableInfo.setLayout(null);
        tableInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        tablePanel.add(tableInfo, BorderLayout.CENTER);

        getResult();
        showResult();
    }

    /**
     * Method for getting the information
     */
    private void getResult()
    {
        // Check which information to get and filtering the information
        if(curPanel == bookingPage)
        {
            bookingResult = new ArrayList<Booking>();

            ArrayList<Booking> tempList = frontOfHouse.getBookingList();

            ArrayList<Booking> bookingList = new ArrayList<Booking>();

            if(bookingActive)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    if(tempList.get(i).getStatus() == Booking.BOOKED || tempList.get(i).getStatus() == Booking.PLACED)
                    {
                        bookingList.add(tempList.get(i));
                    }
                }
            }
            else if(bookingInactive)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    if(tempList.get(i).getStatus() == Booking.CANCELLED || tempList.get(i).getStatus() == Booking.FINISHED || tempList.get(i).getStatus() == Booking.BILLED)
                    {
                        bookingList.add(tempList.get(i));
                    }
                }
            }
            else if(bookingAlert)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    Booking booking = tempList.get(i);
                    if(booking.getStatus() == Booking.BOOKED)
                    {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long timeDifference = Math.abs(timestamp.getTime() - booking.getDate().getTime());
                        if(timeDifference < 60 * 60 * 1000)
                        {
                            bookingList.add(booking);
                        }
                    }
                }
            }
            else
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    bookingList.add(tempList.get(i));
                }
            }

            // check the search criteria
            if(searchText.replaceAll(" ", "").equals(""))
            {
                for(int i = 0; i < bookingList.size();i++)
                {
                    bookingResult.add(bookingList.get(i));
                }
            }
            else
            {
                for(int i = 0; i < bookingList.size(); i++)
                {
                    if(bookingList.get(i).getBookingID().toLowerCase().contains(searchText.toLowerCase())
                            || bookingList.get(i).getBookerName().toLowerCase().contains(searchText.toLowerCase())
                            || bookingList.get(i).getContact().toLowerCase().contains(searchText.toLowerCase())
                            || new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(bookingList.get(i).getDate()).toLowerCase().contains(searchText.toLowerCase())
                            || Booking.status(bookingList.get(i).getStatus()).toLowerCase().contains(searchText.toLowerCase())
                            || bookingList.get(i).getComment().toLowerCase().contains(searchText.toLowerCase())
                            || StringConversion.arrayListToString(bookingList.get(i).getTableList()).toLowerCase().contains(searchText.toLowerCase()))
                    {
                        bookingResult.add(bookingList.get(i));
                    }
                }
            }

            // set max page
            maxPage = (bookingResult.size() - 1) / 10 + 1;
        }
        else
        {
            Table[] tempList = frontOfHouse.getTableList();
            ArrayList<Table> tableList = new ArrayList<Table>();
            tableResult = new ArrayList<Table>();

            if(tableAvailable)
            {
                for(int i = 0; i < tempList.length; i++)
                {
                    if(tempList[i].getAvailability())
                    {
                        tableList.add(tempList[i]);
                    }
                }
            }
            else
            {
                for(int i = 0; i < tempList.length; i++)
                {
                    tableList.add(tempList[i]);
                }
            }

            // check the search criteria
            if(searchText.replaceAll(" ", "").equals(""))
            {
                for(int i = 0; i < tableList.size();i++)
                {
                    tableResult.add(tableList.get(i));
                }
            }
            else
            {
                for (int i = 0; i < tableList.size(); i++)
                {
                    if(tableList.get(i).getTableID().toLowerCase().contains(searchText.toLowerCase()))
                    {
                        tableResult.add(tableList.get(i));
                    }
                }
            }

            // set max page
            maxPage = (tableResult.size() - 1) / 10 + 1;
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
        // Show the correct information
        if(curPanel == bookingPage)
        {
            bookingAlertCB.setText("<HTML>Alert(<font color='red'>" + String.valueOf(frontOfHouse.getAlertNum()) + "</font>)</HTML>");
            bookingTopPanel.revalidate();
            bookingTopPanel.repaint();

            bookingInfo.removeAll();

            for(int i = (curPage - 1) * 10; i < curPage * 10 && i < bookingResult.size(); i++)
            {
                // create each field for each order item
                createBookingField(bookingResult.get(i), i - (curPage - 1) * 10);
            }

            // Redraw the panel
            bookingInfo.revalidate();
            bookingInfo.repaint();
        }
        else
        {
            tableInfo.removeAll();

            for(int i = (curPage - 1) * 10; i < curPage * 10 && i < tableResult.size(); i++)
            {
                // create each field for each order item
                createTableField(tableResult.get(i), i - (curPage - 1) * 10);
            }

            // Redraw the panel
            tableInfo.revalidate();
            tableInfo.repaint();
        }

        pageButton.setText(String.valueOf(curPage));
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // updated time
        updateTime();
    }

    /**
     * Method for creating each line of information
     * @param booking The booking
     * @param location The location of the line
     */
    private void createBookingField(Booking booking, int location)
    {
        // Creating the components
        String bookingIDLabel = booking.getBookingID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long timeDifference = Math.abs(timestamp.getTime() - booking.getDate().getTime());
        if(timeDifference < 60 * 60 * 1000)
        {
            bookingIDLabel = "<HTML><font color='red'>" + booking.getBookingID() + "</font></HTML>";
        }

        JLabel bookingID = new JLabel(bookingIDLabel, SwingConstants.CENTER);
        bookingID.setPreferredSize(new Dimension(bookingIDWidth, height));
        bookingID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), bookingID.getPreferredSize()));
        bookingInfo.add(bookingID);

        JLabel bookerName = new JLabel(booking.getBookerName(), SwingConstants.CENTER);
        bookerName.setPreferredSize(new Dimension(bookerNameWidth, height));
        bookerName.setBounds(new Rectangle(new Point(bookingIDWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), bookerName.getPreferredSize()));
        bookingInfo.add(bookerName);

        JLabel contact = new JLabel(booking.getContact(), SwingConstants.CENTER);
        contact.setPreferredSize(new Dimension(contactWidth, height));
        contact.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), contact.getPreferredSize()));
        bookingInfo.add(contact);

        JLabel tableRequested = new JLabel(booking.getTablesRequested(), SwingConstants.CENTER);
        tableRequested.setPreferredSize(new Dimension(tableRequestedWidth, height));
        tableRequested.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), tableRequested.getPreferredSize()));
        bookingInfo.add(tableRequested);

        JLabel bookingTableID = new JLabel(StringConversion.arrayListToString(booking.getTableList()), SwingConstants.CENTER);
        bookingTableID.setPreferredSize(new Dimension(bookingTableIDWidth, height));
        bookingTableID.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + 4 * widthSpace, heightSpace/2 + (height + heightSpace) * location), bookingTableID.getPreferredSize()));
        bookingInfo.add(bookingTableID);

        JLabel date = new JLabel(new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(booking.getDate()), SwingConstants.CENTER);
        date.setPreferredSize(new Dimension(dateWidth, height));
        date.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + 5 * widthSpace, heightSpace/2 + (height + heightSpace) * location), date.getPreferredSize()));
        bookingInfo.add(date);

        JLabel comment = new JLabel(booking.getComment(), SwingConstants.CENTER);
        comment.setPreferredSize(new Dimension(commentWidth, height));
        comment.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + 6 * widthSpace, heightSpace/2 + (height + heightSpace) * location), comment.getPreferredSize()));
        bookingInfo.add(comment);

        JLabel status = new JLabel(Booking.status(booking.getStatus()), SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(statusWidth, height));
        status.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + 7 * widthSpace, heightSpace/2 + (height + heightSpace) * location), status.getPreferredSize()));
        bookingInfo.add(status);

        if(booking.getStatus() == Booking.BOOKED)
        {
            StatusButton cancel = new StatusButton("CANCEL");
            cancel.setPreferredSize(new Dimension(statusButtonWidth, height));
            cancel.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + statusWidth + 8 * widthSpace, heightSpace/2 + (height + heightSpace) * location), cancel.getPreferredSize()));
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // Send requests to make changes
                    String outputText = "ChangeBookingStatus/" + booking.getBookingID() + Client.infoSeperator + String.valueOf(Booking.CANCELLED);
                    String reply = Client.clientToFOH(outputText);
                    if(reply.contains("Booking Status Changed"))
                    {
                        getResult();
                        showResult();
                    }
                }
            });
            bookingInfo.add(cancel);

            StatusButton place = new StatusButton("PLACE");
            place.setPreferredSize(new Dimension(statusButtonWidth, height));
            place.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + statusWidth  + statusButtonWidth + 9 * widthSpace, heightSpace/2 + (height + heightSpace) * location), place.getPreferredSize()));
            place.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // Send requests to make changes
                    String outputText = "ChangeBookingStatus/" + booking.getBookingID() + Client.infoSeperator + String.valueOf(Booking.PLACED);
                    String reply = Client.clientToFOH(outputText);
                    if(reply.contains("Booking Status Changed"))
                    {
                        getResult();
                        showResult();
                    }
                }
            });
            bookingInfo.add(place);
        }

        if(booking.getStatus() == Booking.FINISHED)
        {
            StatusButton bill = new StatusButton("BILL");
            bill.setPreferredSize(new Dimension(statusButtonWidth, height));
            bill.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + statusWidth + 8 * widthSpace, heightSpace/2 + (height + heightSpace) * location), bill.getPreferredSize()));
            bill.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // Send requests to make changes
                    String outputText = "ChangeBookingStatus/" + booking.getBookingID() + Client.infoSeperator + String.valueOf(Booking.BILLED);
                    String reply = Client.clientToFOH(outputText);
                    if(reply.contains("Booking Status Changed"))
                    {
                        getResult();
                        showResult();
                        // need more code
                    }
                }
            });
            bookingInfo.add(bill);
        }

        if(booking.getStatus() == Booking.BILLED)
        {
            StatusButton getBill = new StatusButton("BILL");
            getBill.setPreferredSize(new Dimension(statusButtonWidth, height));
            getBill.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + statusWidth + 8 * widthSpace, heightSpace/2 + (height + heightSpace) * location), getBill.getPreferredSize()));
            getBill.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    BillFrame billFrame = new BillFrame(booking.getOrderList());
                }
            });
            bookingInfo.add(getBill);
        }

        JButton detail = new JButton("DETAIL");
        detail.setPreferredSize(new Dimension(detailButtonWidth, height));
        detail.setBounds(new Rectangle(new Point(bookingIDWidth + bookerNameWidth + contactWidth + tableRequestedWidth + bookingTableIDWidth + dateWidth + commentWidth + statusWidth  + 2 * statusButtonWidth + 10 * widthSpace, heightSpace/2 + (height + heightSpace) * location), detail.getPreferredSize()));
        detail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BookingDetailFrame bookingDetailFrame = new BookingDetailFrame(booking.getBookingID());
            }
        });
        bookingInfo.add(detail);
    }

    /**
     * Method for creating each line of information
     * @param table The table
     * @param location The location of the line
     */
    private void createTableField(Table table, int location)
    {
        // Creating components
        JLabel tableID = new JLabel(table.getTableID(), SwingConstants.CENTER);
        tableID.setPreferredSize(new Dimension(tableIDWidth, height));
        tableID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), tableID.getPreferredSize()));
        tableInfo.add(tableID);

        JLabel size = new JLabel(String.valueOf(table.getSize()), SwingConstants.CENTER);
        size.setPreferredSize(new Dimension(tableSizeWidth, height));
        size.setBounds(new Rectangle(new Point(tableIDWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), size.getPreferredSize()));
        tableInfo.add(size);

        String availabilityLabel = "";
        if(table.getAvailability())
        {
            availabilityLabel = "available";
        }
        else
        {
            availabilityLabel = "unavailable";
        }

        JLabel availability = new JLabel(availabilityLabel, SwingConstants.CENTER);
        availability.setPreferredSize(new Dimension(tableAvailabilityWidth, height));
        availability.setBounds(new Rectangle(new Point(tableIDWidth + tableSizeWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), availability.getPreferredSize()));
        tableInfo.add(availability);

        JTextField problem = new JTextField(table.getProblem());
        problem.setHorizontalAlignment(JTextField.CENTER);
        problem.setPreferredSize(new Dimension(tableProblemWidth, height));
        problem.setBounds(new Rectangle(new Point(tableIDWidth + tableSizeWidth + tableAvailabilityWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), problem.getPreferredSize()));
        if(table.getProblem().contains("occupied") && !table.getAvailability())
        {
            problem.setEditable(false);
        }
        else
        {
            problem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String outputText = "ChangeTableProblem/" + table.getTableID() + Client.infoSeperator + problem.getText();
                    Client.clientToFOH(outputText);
                }
            });

            problem.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    problem.setText(table.getProblem());
                }
            });
        }
        tableInfo.add(problem);

        String availabilityButtonText = "Available";
        if(table.getAvailability())
        {
            availabilityButtonText = "Unavailable";
        }
        JButton availabilityButton = new JButton(availabilityButtonText);
        availabilityButton.setPreferredSize(new Dimension(tableAvailabilityButtonWidth, height));
        availabilityButton.setBounds(new Rectangle(new Point(tableIDWidth + tableSizeWidth + tableAvailabilityWidth + tableProblemWidth + 4 * widthSpace, heightSpace/2 + (height + heightSpace) * location), availabilityButton.getPreferredSize()));
        if(!table.getProblem().contains("occupied"))
        {
            tableInfo.add(availabilityButton);
            if(table.getAvailability())
            {
                availabilityButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String outputText = "ChangeTableAvailability/" + table.getTableID() + Client.infoSeperator + "false";
                        Client.clientToFOH(outputText);
                    }
                });
            }
            else
            {
                availabilityButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String outputText = "ChangeTableAvailability/" + table.getTableID() + Client.infoSeperator + "true";
                        Client.clientToFOH(outputText);
                    }
                });
            }
        }

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
