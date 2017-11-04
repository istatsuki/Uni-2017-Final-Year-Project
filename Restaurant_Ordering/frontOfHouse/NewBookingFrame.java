package frontOfHouse;

import resources.Client;
import resources.ErrorField;
import resources.JLabelCreation;
import resources.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * <h1> New Booking Frame</h1>
 * The frame for creating new booking
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class NewBookingFrame
{
    // The Front of House sub-system
    FrontOfHouse frontOfHouse = FrontOfHouse.instance();

    // The specifications of different components
    private final int heightSpace = 40;
    private final int widthSpace = 10;
    private final int height = 20;

    private final int bookerNameLabelWidth = 80;
    private final int bookerNameWidth = 200;
    private final int contactLabelWidth = 50;
    private final int contactWidth = 340;
    private final int dateLabelWidth = 40;
    private final int dateConWidth = 5;
    private final int yearWidth = 60;
    private final int monthWidth = 30;
    private final int dayWidth = 30;
    private final int timeLabelWidth = 40;
    private final int timeWidth = 100;
    private final int commentLabelWidth = 80;
    private final int commentWidth = 400;

    private final int tableSizeWidth = 80;
    private final int tableAmountAvailableWidth = 150;
    private final int tableAmountRequestWidth = 150;
    private final String[] tableTitles = {"Table Size", "Amount Available", "Amount Request"};
    private final int[] tableWidths = {tableSizeWidth, tableAmountAvailableWidth, tableAmountRequestWidth};

    // The list of time to book
    private final String[] timeList = {"10:00:00", "10:30:00", "11:00:00", "11:30:00", "12:00:00", "12:30:00", "13:00:00", "13:30:00", "14:00:00", "14:30:00", "15:00:00", "15:30:00", "16:00:00", "16:30:00", "17:00:00", "17:30:00", "18:00:00", "18:30:00", "19:00:00", "19:30:00", "20:00:00", "20:30:00", "21:00:00"};

    // The attribute objects for the booking
    private Timestamp date = null;
    private ArrayList<Pair<Integer, Integer>> allTableSize = new ArrayList<Pair<Integer, Integer>>();
    private ArrayList<Pair<Integer, Integer>> tablesRequested = new ArrayList<Pair<Integer, Integer>>();

    // The JComponents
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel middlePanel;
    private JPanel infoPanel;
    private JPanel tablePanel;
    private JPanel tablePanelLabel;
    private JPanel tablePanelInfo;

    private JPanel bottomPanel;
    private JPanel buttonPanel;
    private JLabel errorTable;

    /**
     * Constructor
     */
    public NewBookingFrame()
    {
        // Create the frame of the user interface
        mainFrame = new JFrame("New Booking");
        mainFrame.setSize(700, 430);
        mainFrame.setResizable(false);

        // Create the panel to hold all other components
        mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // create the middle panel
        middlePanel = new JPanel(new BorderLayout());
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        // Create the info
        infoPanel = new JPanel(null);
        middlePanel.add(infoPanel, BorderLayout.CENTER);

        JLabel bookerNameLabel = new JLabel("Booker Name:", SwingConstants.CENTER);
        bookerNameLabel.setPreferredSize(new Dimension(bookerNameLabelWidth, height));
        bookerNameLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2), bookerNameLabel.getPreferredSize()));
        infoPanel.add(bookerNameLabel);

        JTextField bookerName = new JTextField();
        bookerName.setPreferredSize(new Dimension(bookerNameWidth, height));
        bookerName.setBounds(new Rectangle(new Point(widthSpace/2 + bookerNameLabelWidth, heightSpace/2), bookerName.getPreferredSize()));
        infoPanel.add(bookerName);
        ErrorField bookerNameErrorField = new ErrorField("Can't be empty", bookerName, heightSpace/2);

        JLabel contactLabel = new JLabel("Contact:", SwingConstants.CENTER);
        contactLabel.setPreferredSize(new Dimension(contactLabelWidth, height));
        contactLabel.setBounds(new Rectangle(new Point(widthSpace/2 + bookerNameLabelWidth + bookerNameWidth + widthSpace, heightSpace/2), contactLabel.getPreferredSize()));
        infoPanel.add(contactLabel);

        JTextField contact = new JTextField();
        contact.setPreferredSize(new Dimension(contactWidth, height));
        contact.setBounds(new Rectangle(new Point(widthSpace/2 + bookerNameLabelWidth + bookerNameWidth + widthSpace + contactLabelWidth, heightSpace/2), contact.getPreferredSize()));
        infoPanel.add(contact);
        ErrorField contactErrorField = new ErrorField("Can't be empty", contact, heightSpace/2);

        JLabel dateLabel = new JLabel("Date:", SwingConstants.CENTER);
        dateLabel.setPreferredSize(new Dimension(dateLabelWidth, height));
        dateLabel.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace, heightSpace/2 + height + heightSpace), dateLabel.getPreferredSize()));
        infoPanel.add(dateLabel);

        JTextField year = new JTextField();
        year.setPreferredSize(new Dimension(yearWidth, height));
        year.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace + dateLabelWidth, heightSpace/2 + height + heightSpace), year.getPreferredSize()));
        infoPanel.add(year);
        ErrorField dateErrorField = new ErrorField("invalid YYYY-MM-DD", year, heightSpace/2);

        JLabel dateCon = new JLabel("-", SwingConstants.CENTER);
        dateCon.setPreferredSize(new Dimension(dateConWidth, height));
        dateCon.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace + dateLabelWidth + yearWidth, heightSpace/2 + height + heightSpace), dateCon.getPreferredSize()));
        infoPanel.add(dateCon);

        JTextField month = new JTextField();
        month.setPreferredSize(new Dimension(monthWidth, height));
        month.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace + dateLabelWidth + yearWidth + dateConWidth, heightSpace/2 + height + heightSpace), month.getPreferredSize()));
        infoPanel.add(month);

        JLabel dateCon1 = new JLabel("-", SwingConstants.CENTER);
        dateCon1.setPreferredSize(new Dimension(dateConWidth, height));
        dateCon1.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace + dateLabelWidth + yearWidth + dateConWidth + monthWidth, heightSpace/2 + height + heightSpace), dateCon1.getPreferredSize()));
        infoPanel.add(dateCon1);

        JTextField day = new JTextField();
        day.setPreferredSize(new Dimension(dayWidth, height));
        day.setBounds(new Rectangle(new Point(widthSpace/2 + 3 * widthSpace + dateLabelWidth + yearWidth + 2 * dateConWidth + monthWidth, heightSpace/2 + height + heightSpace), day.getPreferredSize()));
        infoPanel.add(day);

        JLabel timeLabel = new JLabel("Time", SwingConstants.CENTER);
        timeLabel.setPreferredSize(new Dimension(timeLabelWidth, height));
        timeLabel.setBounds(new Rectangle(new Point(widthSpace/2 + 12 * widthSpace + dateLabelWidth + yearWidth + 2 * dateConWidth + monthWidth + dayWidth, heightSpace/2 + height + heightSpace), timeLabel.getPreferredSize()));
        infoPanel.add(timeLabel);

        JComboBox<String> time = new JComboBox<String>(timeList);
        time.setSelectedIndex(0);
        time.setPreferredSize(new Dimension(timeWidth, height));
        time.setBounds(new Rectangle(new Point(widthSpace/2 + 12 * widthSpace + dateLabelWidth + yearWidth + 2 * dateConWidth + monthWidth + dayWidth + timeLabelWidth, heightSpace/2 + height + heightSpace), time.getPreferredSize()));
        infoPanel.add(time);
        ErrorField timeErrorField = new ErrorField("Can't be empty", time, heightSpace/2);

        JLabel commentLabel = new JLabel("Comment:", SwingConstants.CENTER);
        commentLabel.setPreferredSize(new Dimension(commentLabelWidth, height));
        commentLabel.setBounds(new Rectangle(new Point(widthSpace/2, heightSpace/2 + 2 * (height + heightSpace)), commentLabel.getPreferredSize()));
        infoPanel.add(commentLabel);

        JTextField comment = new JTextField();
        comment.setPreferredSize(new Dimension(commentWidth, height));
        comment.setBounds(new Rectangle(new Point(widthSpace/2 + commentLabelWidth, heightSpace/2 + 2 * (height + heightSpace)), comment.getPreferredSize()));
        infoPanel.add(comment);

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        tablePanel.setPreferredSize(new Dimension(middlePanel.getWidth(), 200));
        middlePanel.add(tablePanel, BorderLayout.SOUTH);

        tablePanelLabel = new JPanel(null);
        tablePanelLabel.setPreferredSize(new Dimension(tablePanel.getWidth(), height));
        tablePanel.add(tablePanelLabel, BorderLayout.NORTH);
        JLabelCreation.createLabels(tablePanelLabel, tableTitles, tableWidths, height, widthSpace);

        tablePanelInfo = new JPanel(null);
        tablePanelInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        tablePanel.add(tablePanelInfo, BorderLayout.CENTER);

        getAvailableTable();
        showAvailableTable();

        // Create the bottom panel
        bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        buttonPanel = new JPanel(new FlowLayout());
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        JButton createButton = new JButton("Create Booking");
        createButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean valid = true;
                if(bookerName.getText().equals(""))
                {
                    infoPanel.add(bookerNameErrorField);
                    valid = false;
                }
                else
                {
                    infoPanel.remove(bookerNameErrorField);
                }

                if(contact.getText().equals(""))
                {
                    infoPanel.add(contactErrorField);
                    valid = false;
                }
                else
                {
                    infoPanel.remove(contactErrorField);
                }

                if(year.getText().equals("") || month.getText().equals("") || day.getText().equals(""))
                {
                    infoPanel.add(dateErrorField);
                    valid = false;
                }
                else
                {
                        infoPanel.remove(dateErrorField);
                }

                if(time.getSelectedItem() == null)
                {
                    infoPanel.add(timeErrorField);
                    valid = false;
                }
                else
                {
                    infoPanel.remove(timeErrorField);
                }

                if(tablesRequested.isEmpty())
                {
                    bottomPanel.add(errorTable, BorderLayout.WEST);
                    valid = false;
                }
                else
                {
                    bottomPanel.remove(errorTable);
                }

                if(valid)
                {
                    String tableRequested = "";
                    for(int i = 0; i < tablesRequested.size(); i++)
                    {
                        String tableSize = String.valueOf(tablesRequested.get(i).getFirst());
                        String tableAmount = String.valueOf(tablesRequested.get(i).getSecond());
                        tableRequested = tableRequested + tableSize + ":" + tableAmount + ";";
                    }
                    tableRequested = tableRequested.substring(0, tableRequested.length() - 1);

                    String date = year.getText() + "-" + month.getText() + "-" + day.getText() + " " + time.getSelectedItem();

                    // send requests to make changes
                    String outputText = "NewBooking/" + bookerName.getText() + Client.infoSeperator + contact.getText() + Client.infoSeperator + tableRequested + Client.infoSeperator + date + Client.infoSeperator + comment.getText();
                    String reply = Client.clientToFOH(outputText);
                    if(reply.contains("Booking Created"))
                    {
                        mainFrame.setVisible(false);
                    }
                }

                infoPanel.revalidate();
                infoPanel.repaint();
            }
        });
        buttonPanel.add(createButton);

        JButton walkInButton = new JButton("Walk In");
        walkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

                // send requests to make changes
                String outputText = "NewBooking/" + "" + Client.infoSeperator + "" + Client.infoSeperator + "" + Client.infoSeperator + date + Client.infoSeperator + "";
                String reply = Client.clientToFOH(outputText);
                if(reply.contains("Booking Created"))
                {
                    mainFrame.setVisible(false);
                }
            }
        });
        buttonPanel.add(walkInButton);

        errorTable = new JLabel("None Table Requested");
        errorTable.setForeground(Color.RED);

        // Date fields constraints
        year.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                try
                {
                    // make the change and redraw the panel
                    int x = Integer.parseInt(year.getText());
                    if(!month.getText().equals("") && !day.getText().equals(""))
                    {
                        if(!dateValidation(year.getText(), month.getText(), day.getText()))
                        {
                            throw new IllegalArgumentException("Illegal date input");
                        }
                        else
                        {
                            changeDate(year.getText() + "-" + month.getText() + "-" + day.getText() + " " + String.valueOf(time.getSelectedItem()));
                        }
                    }
                    infoPanel.remove(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch(NumberFormatException ex)
                {
                    year.setText("");
                    if(month.getText().equals("") && day.getText().equals(""))
                    {
                        changeDate("now");
                    }
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch (IllegalArgumentException ex1)
                {
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
            }});

        month.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                try
                {
                    // make the change and redraw the panel
                    int x = Integer.parseInt(month.getText());
                    if(!year.getText().equals("") && !day.getText().equals(""))
                    {
                        if(!dateValidation(year.getText(), month.getText(), day.getText()))
                        {
                            throw new IllegalArgumentException("Illegal date input");
                        }
                        else
                        {
                            changeDate(year.getText() + "-" + month.getText() + "-" + day.getText() + " " + String.valueOf(time.getSelectedItem()));
                        }
                    }
                    infoPanel.remove(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch(NumberFormatException ex)
                {
                    month.setText("");
                    if(year.getText().equals("") && day.getText().equals(""))
                    {
                        changeDate("now");
                    }
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch (IllegalArgumentException ex1)
                {
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
            }});

        day.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                try
                {
                    // make the change and redraw the panel
                    int x = Integer.parseInt(day.getText());
                    if(!year.getText().equals("") && !month.getText().equals(""))
                    {
                        if(!dateValidation(year.getText(), month.getText(), day.getText()))
                        {
                            throw new IllegalArgumentException("Illegal date input");
                        }
                        else
                        {
                            changeDate(year.getText() + "-" + month.getText() + "-" + day.getText() + " " + String.valueOf(time.getSelectedItem()));
                        }
                    }
                    infoPanel.remove(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch(NumberFormatException ex)
                {
                    day.setText("");
                    if(year.getText().equals("") && month.getText().equals(""))
                    {
                        changeDate("now");
                    }
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
                catch (IllegalArgumentException ex1)
                {
                    infoPanel.add(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
            }});

        time.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(!year.getText().equals("") && !month.getText().equals("") && !day.getText().equals(""))
                {
                    changeDate(year.getText() + "-" + month.getText() + "-" + day.getText() + " " + String.valueOf(time.getSelectedItem()));
                    infoPanel.remove(dateErrorField);
                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
            }
        });

        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    /**
     * Method for validating the date (not in the past and exists in the real calendar)
     * @param year The year value
     * @param month The month value
     * @param day The day value
     * @return If the date is valid
     */
    private boolean dateValidation(String year, String month, String day)
    {
        // values have to be integers
        int x = Integer.valueOf(year);
        int y = Integer.valueOf(month);
        int z = Integer.valueOf(day);
        Calendar calendar = Calendar.getInstance();

        // year is not smaller than the current year, month and day is bigger than 0
        if(x < calendar.get(Calendar.YEAR) || y < 1 || z < 1)
        {
            return false;
        }

        // month is smaller than 13
        if(y > 12)
        {
            return false;
        }

        // current year?
        if(x == calendar.get(Calendar.YEAR))
        {
            // month has to be no smaller than the current month
            if(y < calendar.get(Calendar.MONTH))
            {
                return false;
            }

            // current month, then day is no smaller than the current day
            if(y == calendar.get(Calendar.MONTH) && x < calendar.get(Calendar.DAY_OF_MONTH))
            {
                return false;
            }
        }

        // February?
        if(y == 2)
        {
            // leap year?
            if(x % 4 == 0 && z > 29)
            {
                return false;
            }
            else if(z > 28)
            {
                return false;
            }
        }

        // 31 days-months?
        if(y == 1 || y == 3 || y == 5 || y == 7 || y == 8 || y == 10 || y == 12)
        {
            if( z > 31)
            {
                return false;
            }
        }

        // 30 days-months?
        if(y == 4 || y == 6 || y == 9 | y == 11)
        {
            if( z > 30)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Method to change the date
     * @param date The date
     */
    private void changeDate(String date)
    {
        if(date.equals("now"))
        {
            this.date = null;
        }
        else
        {
            this.date = Timestamp.valueOf(date);
        }

        getAvailableTable();
        showAvailableTable();
    }

    /**
     * Method to get the available tables (by checking the tables required at the booking dates)
     */
    private void getAvailableTable()
    {
        Table[] tableList = frontOfHouse.getTableList();

        allTableSize = new ArrayList<Pair<Integer, Integer>>();


        if(date != null)
        {
            ArrayList<Booking> bookingList = frontOfHouse.getBookingList();

            for(int i = 0; i < tableList.length; i++)
            {
                int x = tableList[i].getSize();
                boolean existed = false;

                for(int k = 0; k < allTableSize.size() && !existed; k++)
                {
                    if(allTableSize.get(k).getFirst().equals(x))
                    {
                        allTableSize.get(k).setSecond(allTableSize.get(k).getSecond() + 1);
                        existed = true;
                    }
                }

                if(!existed)
                {
                    allTableSize.add(new Pair<Integer, Integer>(x, 1));
                }
            }

            for(int i = 0; i < allTableSize.size(); i++)
            {
                allTableSize.get(i).setSecond(allTableSize.get(i).getSecond() - 2);
                if(allTableSize.get(i).getSecond() < 1)
                {
                    allTableSize.remove(i);
                    i--;
                }
            }

            for(int i = 0; i < bookingList.size(); i++)
            {
                if(bookingList.get(i).getStatus() != Booking.CANCELLED && bookingList.get(i).getStatus() != Booking.BILLED) {
                    long timeDifference = Math.abs(bookingList.get(i).getDate().getTime() - this.date.getTime());
                    if (timeDifference == 0L || timeDifference == 60 * 60 * 1000L || timeDifference == 30 * 60 * 1000L) {
                        String tableRequested = bookingList.get(i).getTablesRequested().replaceAll(" ", "");
                        while (!tableRequested.equals("")) {
                            String tableSize = tableRequested.substring(0, tableRequested.indexOf(":"));
                            String tableAmount = tableRequested.substring(tableRequested.indexOf(":") + 1);
                            if (tableRequested.contains(";")) {
                                tableAmount = tableRequested.substring(tableRequested.indexOf(":") + 1, tableRequested.indexOf(";"));
                            }
                            boolean contain = false;
                            for (int k = 0; k < allTableSize.size() & !contain; k++) {
                                if (allTableSize.get(k).getFirst() == Integer.parseInt(tableSize)) {
                                    allTableSize.get(k).setSecond(allTableSize.get(k).getSecond() - Integer.parseInt(tableAmount));
                                    if (allTableSize.get(k).getSecond() < 1) {
                                        allTableSize.remove(k);
                                        k--;
                                    }

                                    contain = true;
                                }
                            }

                            if (tableRequested.contains(";")) {
                                tableRequested = tableRequested.substring(tableRequested.indexOf(";") + 1);
                            } else {
                                tableRequested = "";
                            }
                        }
                    }
                }

            }
        }
        else
        {
            for(int i = 0; i < tableList.length; i++)
            {
                if(tableList[i].getAvailability())
                {
                    int x = tableList[i].getSize();
                    boolean existed = false;

                    for (int k = 0; k < allTableSize.size() && !existed; k++)
                    {
                        if (allTableSize.get(k).getFirst().equals(x))
                        {
                            allTableSize.get(k).setSecond(allTableSize.get(k).getSecond() + 1);
                            existed = true;
                        }
                    }

                    if (!existed)
                    {
                        allTableSize.add(new Pair<Integer, Integer>(x, 1));
                    }
                }
            }
        }
    }

    /**
     * Method to show all available table
     */
    private void showAvailableTable()
    {
        tablePanelInfo.removeAll();

        for(int i = 0; i < allTableSize.size(); i++)
        {
            // Create each line for each table
            createTableField(allTableSize.get(i), i);
        }

        tablePanelInfo.revalidate();
        tablePanelInfo.repaint();
    }

    /**
     * Method to show each line of the information
     * @param tableInfo The table information
     * @param location The location of the line
     */
    private void createTableField(Pair<Integer, Integer> tableInfo, int location)
    {
        // extracting information
        int x = tableInfo.getFirst();
        int y = tableInfo.getSecond();

        // Create the components
        JLabel size = new JLabel(String.valueOf(x), SwingConstants.CENTER);
        size.setPreferredSize(new Dimension(tableSizeWidth, height));
        size.setBounds(new Rectangle(new Point(0, heightSpace / 4 + (height + heightSpace/2) * location), size.getPreferredSize()));
        tablePanelInfo.add(size);

        JLabel amountAvailable = new JLabel(String.valueOf(y), SwingConstants.CENTER);
        amountAvailable.setPreferredSize(new Dimension(tableAmountAvailableWidth, height));
        amountAvailable.setBounds(new Rectangle(new Point(tableSizeWidth + widthSpace, heightSpace / 4 + (height + heightSpace/2) * location), amountAvailable.getPreferredSize()));
        tablePanelInfo.add(amountAvailable);

        JTextField amountRequested = new JTextField();
        amountRequested.setPreferredSize(new Dimension(tableAmountRequestWidth, height));
        amountRequested.setBounds(new Rectangle(new Point(tableSizeWidth + tableAmountAvailableWidth + 2 * widthSpace, heightSpace / 4 + (height + heightSpace/2) * location), amountRequested.getPreferredSize()));
        tablePanelInfo.add(amountRequested);

        ErrorField errorField = new ErrorField("invalid input", amountRequested, heightSpace);

        amountRequested.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                try
                {
                    // make the change and redraw the panel
                    if (amountRequested.getText().replaceAll(" ", "").equals("") || amountRequested.getText().equals("0"))
                    {
                        boolean contain = false;
                        for (int i = 0; i < tablesRequested.size() && !contain; i++)
                        {
                            if (tablesRequested.get(i).getFirst() == x)
                            {
                                tablesRequested.remove(i);
                                contain = true;
                            }
                        }
                    }
                    else
                    {
                        int z = Integer.parseInt(amountRequested.getText());
                        if(z < 0 || z > y)
                        {
                            throw new NumberFormatException("");
                        }
                        else if(z > 0)
                        {
                            boolean contain = false;
                            for (int i = 0; i < tablesRequested.size() && !contain; i++)
                            {
                                if (tablesRequested.get(i).getFirst() == x)
                                {
                                    tablesRequested.get(i).setSecond(z);
                                    contain = true;
                                }
                            }
                            if(!contain)
                            {
                                tablesRequested.add(new Pair<Integer, Integer>(x, z));
                            }
                        }
                    }
                }
                catch(NumberFormatException ex)
                {
                    // show error message
                    tablePanelInfo.add(errorField);
                    tablePanelInfo.revalidate();
                    tablePanelInfo.repaint();
                }

            }
        });
    }
}
