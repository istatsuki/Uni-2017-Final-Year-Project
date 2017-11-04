package resources;

import frontOfHouse.NewOrderFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <h1> Menu Frame</h1>
 * The Frame of the Menu
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class MenuFrame
{
    // The options of the frame
    public static final int SHOW = 0;
    public static final int NEWORDER = 1;

    // The attributes
    private final MenuItem[] menuItemList;
    private int command;
    private String bookingID;
    private String tableID;

    // The specifications of different components
    private final int heightSpace = 10;
    private final int widthSpace = 20;
    private final int picHeight = 50;
    private final int picWidth = 50;
    private final int labelWidth = 250;
    private final int itemPerLine = 3;
    private final int linePerPage = 8;

    // The options attribute
    private String searchText = "";

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private String curType = "starter";

    // result attribute
    private ArrayList<MenuItem> menuItemResult = new ArrayList<MenuItem>();

    // The components
    private JFrame mainFrame;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel typePanel;
    private JLabel typeLabel;
    private JComboBox<String> typeCB;
    private JButton typeButton;
    private JPanel searchPanel;
    private JLabel searchLabel;
    private JTextField searchTextField;

    private JPanel middlePanel;

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
     * @param menuItemList The menu list
     * @param command The options with the menu
     * @param bookingID The booking ID
     * @param tableID The table ID
     */
    public MenuFrame(MenuItem[] menuItemList, int command, String bookingID, String tableID)
    {
        this.menuItemList = menuItemList;
        this.command = command;
        this.bookingID = bookingID;
        this.tableID = tableID;

        // Create the frame of the user interface
        mainFrame = new JFrame("Menu");
        mainFrame.setSize(1050, 630);
        mainFrame.setResizable(false);

        // Create the panel to hold all other components
        mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Create the top panel
        topPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        typePanel = new JPanel(new FlowLayout());
        topPanel.add(typePanel, BorderLayout.WEST);

        typeLabel = new JLabel("Type:");
        typePanel.add(typeLabel);

        String[] typeList = {"starter", "main", "dessert", "drink"};
        typeCB = new JComboBox<String>(typeList);
        typeCB.setSelectedIndex(0);
        typePanel.add(typeCB);

        typeButton = new JButton("Go");
        typeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!curType.equals(typeCB.getSelectedItem()))
                {
                    curType = String.valueOf(typeCB.getSelectedItem());
                    getResult();
                    showResult();
                }
            }
        });
        typePanel.add(typeButton);

        // Create the search panels to hold the searching components
        searchPanel = new JPanel(new FlowLayout());
        topPanel.add(searchPanel, BorderLayout.EAST);

        searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);

        searchTextField = new JTextField(searchText);
        searchTextField.setPreferredSize(new Dimension(100, 20));
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
        pageButton.setPreferredSize(new Dimension(50, 20));
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
        middlePanel = new JPanel(null);
        middlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        // Method call for getting the information
        getResult();
        showResult();

        // Draw and show the frame
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    /**
     * Method for getting the infomration
     */
    private void getResult()
    {
        // empty result set
        ArrayList<MenuItem> tempList = new ArrayList<MenuItem>();
        menuItemResult = new ArrayList<MenuItem>();

        // check the search criteria
        if (!searchText.replaceAll(" ", "").equals(""))
        {
            for (int i = 0; i < menuItemList.length; i++)
            {
                if (menuItemList[i].getMenuItemID().toLowerCase().contains(searchText.toLowerCase()) || menuItemList[i].getName().toLowerCase().contains(searchText.toLowerCase()) || menuItemList[i].getType().toLowerCase().contains(searchText.toLowerCase()) || menuItemList[i].getSpecialList().toLowerCase().contains(searchText.toLowerCase()))
                {
                    tempList.add(menuItemList[i]);
                }
            }
        }
        else
        {
            for(int i = 0; i < menuItemList.length; i++)
            {
                tempList.add(menuItemList[i]);
            }
        }

        for(int i = 0; i < tempList.size(); i++)
        {
            if(tempList.get(i).getType().equals(curType))
            {
                menuItemResult.add(tempList.get(i));
            }
        }

        // set max page
        maxPage = (menuItemResult.size() - 1) / (itemPerLine * linePerPage) + 1;

        // set current page if necessary
        if (curPage > maxPage) {
            curPage = maxPage;
        }
    }

    /**
     * method for showing the information
     */
    private void showResult()
    {
        // remove all previous object
        middlePanel.removeAll();

        for(int i = 0; i < linePerPage; i++)
        {
            for(int k = (curPage - 1) * (itemPerLine * linePerPage) + itemPerLine * i; k < (curPage * (itemPerLine * linePerPage) + itemPerLine * (i - (linePerPage - 1))) && k < menuItemResult.size(); k++)
            {
                createMenuItemField(menuItemResult.get(k), k - (curPage - 1) * (itemPerLine * linePerPage));
            }
        }

        // Redraw the panel
        middlePanel.revalidate();
        middlePanel.repaint();

        pageButton.setText(String.valueOf(curPage));
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // updated time
        updateTime();
    }

    /**
     * Create each menu item on the panel
     * @param menuItem The menu item
     * @param location The location of the menu item on the panel
     */
    private void createMenuItemField(MenuItem menuItem, int location)
    {
        int position = location;
        int line = 0;

        if(location >= itemPerLine)
        {
            position = location%itemPerLine;
            line = location/itemPerLine;
        }

        BufferedImage image = null;

        try
        {
            FileReader imageReader = new FileReader();
            image = ImageIO.read(new File(imageReader.read(menuItem.getImage())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        JLabel picLabel = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
        picLabel.setPreferredSize(new Dimension(picWidth, picHeight));
        picLabel.setBounds(new Rectangle(new Point(widthSpace/2 + position * (picWidth + labelWidth + widthSpace), heightSpace/2 + line * (picHeight + heightSpace)), picLabel.getPreferredSize()));

        picLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if(command == SHOW)
                {
                    MenuItemFrame menuItemFrame = new MenuItemFrame(menuItem, picLabel);
                    menuItemFrame.show();
                }
                else if(command == NEWORDER)
                {
                    if(menuItem.getAvailability())
                    {
                        NewOrderFrame newOrderFrame = new NewOrderFrame(menuItem, bookingID, tableID);
                    }
                    else
                    {
                        JFrame frame = new JFrame("Error");
                        frame.setSize(200, 200);

                        JPanel panel = new JPanel(new BorderLayout());
                        frame.add(panel);

                        JLabel label = new JLabel("<HTML><font color='red'>Menu Item not available</font></HTML>", SwingConstants.CENTER);
                        panel.add(label, BorderLayout.CENTER);

                        frame.revalidate();
                        frame.repaint();
                        frame.setVisible(true);
                    }
                }
            }
        });
        middlePanel.add(picLabel);

        String menuItemAvailable = "";
        if(!menuItem.getAvailability())
        {
            menuItemAvailable = "<font color='red'>(not available)</font>";
        }

        String special = "";
        if(menuItem.getSpecialList().contains("gluten"))
        {
            special = special + "<font color='yellow'>(gluten)</font>";
        }
        if(menuItem.getIngredientList().contains("vegan"))
        {
            special = special + "<font color='green'>(vegan)</font>";
        }

        JLabel label = new JLabel("<HTML>" + menuItem.getName() + special + menuItemAvailable + "<HTML>", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(labelWidth, picHeight));
        label.setBounds(new Rectangle(new Point(widthSpace/2 + position * (picWidth + labelWidth + widthSpace) + picWidth, heightSpace/2 + line * (picHeight + heightSpace)), label.getPreferredSize()));
        middlePanel.add(label);
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
        bottomLabel.setText("Updated at " + new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date()));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
