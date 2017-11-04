package stock;

import resources.ErrorField;
import resources.JLabelCreation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Stock GUI</h1>
 * The user interface of the stock sub-system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StockGUI
{
    // The stock manager sub-system
    private StockManager stockManager = StockManager.instance();

    // The specifications of different components
    private final int heightSpace = 20;
    private final int widthSpace = 0;
    private final int height = 20;
    private final int ingredientIDWidth = 100;
    private final int nameWidth  = 100;
    private final int amountWidth = 200;
    String[] titles = {"IngredientID", "Name", "Amount"};
    int[] widths = {ingredientIDWidth, nameWidth, amountWidth};

    // Searching text
    private String searchText = "";

    // Navigation attribute
    private int curPage = 1;
    private int maxPage = 1;
    private Timestamp timeUpdated = new Timestamp(System.currentTimeMillis());

    // result attribute
    private ArrayList<Ingredient> ingredientResult = new ArrayList<Ingredient>();

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
     */
    public StockGUI()
    {
        // Create the frame of the user interface
        mainFrame = new JFrame("Stock Manager");
        mainFrame.setSize(700, 520);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                stockManager.closeDown();
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

        // The panel that holds the information of the stock
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

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable updateCheck = new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    if(timeUpdated.getTime() < Timestamp.valueOf(stockManager.getTimeUpdated()).getTime())
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
     * The method for getting the information of the stock
     */
    private void getResult() {

        // get the ingredient list
        Ingredient[] ingredientList = stockManager.getIngredientList();

        // empty result set
        ingredientResult = new ArrayList<Ingredient>();

        // check the search criteria
        if (!searchText.replaceAll(" ", "").equals(""))
        {
            for (int i = 0; i < ingredientList.length; i++)
            {
                if (ingredientList[i].getIngredientID().toLowerCase().contains(searchText.toLowerCase()) || ingredientList[i].getName().toLowerCase().contains(searchText.toLowerCase()))
                {
                    ingredientResult.add(ingredientList[i]);
                }
            }
        }
        else
        {
            for(int i = 0; i < ingredientList.length; i++)
            {
                ingredientResult.add(ingredientList[i]);
            }
        }

        // set max page
        maxPage = (ingredientResult.size() - 1) / 10 + 1;

        // set current page if necessary
        if (curPage > maxPage)
        {
            curPage = maxPage;
        }
    }

    /**
     * The method for showing the information of the stock
     */
    private void showResult()
    {
        // remove all previous object
        middlePanelInfo.removeAll();

        for(int i = (curPage - 1) * 10; i < curPage * 10 && i < ingredientResult.size(); i++)
        {
            createIngredientField(ingredientResult.get(i), i - (curPage - 1) * 10);
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
     * The method to create each field of the ingredients
     * @param ingredient The ingredient
     * @param location The location parameter
     */
    private void createIngredientField(Ingredient ingredient, int location)
    {
        // Create each label and set them to places
        JLabel ingredientID = new JLabel(ingredient.getIngredientID(),  SwingConstants.CENTER);
        ingredientID.setPreferredSize(new Dimension(ingredientIDWidth, height));
        ingredientID.setBounds(new Rectangle(new Point(0, heightSpace/2 + (height + heightSpace) * location), ingredientID.getPreferredSize()));
        middlePanelInfo.add(ingredientID);

        JLabel name = new JLabel(ingredient.getName(),  SwingConstants.CENTER);
        name.setPreferredSize(new Dimension(nameWidth, height));
        name.setBounds(new Rectangle(new Point(ingredientIDWidth + widthSpace, heightSpace/2 + (height + heightSpace) * location), name.getPreferredSize()));
        middlePanelInfo.add(name);

        JLabel amount = new JLabel(ingredient.getAmount() + " " + ingredient.getUnit(), SwingConstants.CENTER);
        amount.setPreferredSize(new Dimension(amountWidth, height));
        amount.setBounds(new Rectangle(new Point(ingredientIDWidth + nameWidth + 2 * widthSpace, heightSpace/2 + (height + heightSpace) * location), amount.getPreferredSize()));
        middlePanelInfo.add(amount);

        // Create the fields for altering the amount of the ingredients
        JLabel alterAmountLabel = new JLabel("Alter Amount: ");
        alterAmountLabel.setPreferredSize(new Dimension(100, height));
        alterAmountLabel.setBounds(new Rectangle(new Point(ingredientIDWidth + nameWidth + amountWidth + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), alterAmountLabel.getPreferredSize()));
        middlePanelInfo.add(alterAmountLabel);

        JTextField alterAmount = new JTextField("");
        alterAmount.setPreferredSize(new Dimension(100, height));
        alterAmount.setBounds(new Rectangle(new Point(ingredientIDWidth + nameWidth + amountWidth + alterAmountLabel.getWidth() + 3 * widthSpace, heightSpace/2 + (height + heightSpace) * location), alterAmount.getPreferredSize()));
        middlePanelInfo.add(alterAmount);

        ErrorField errorField = new ErrorField("invalid input", alterAmount, heightSpace);

        alterAmount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if the input is a valid integer
                try
                {
                    // make the change and redraw the panel
                    int x = Integer.parseInt(alterAmount.getText());
                    stockManager.changeIngredientAmount(ingredient.getIngredientID(), x);
                    getResult();
                    showResult();
                    middlePanelInfo.remove(errorField);
                }
                catch(NumberFormatException ex)
                {
                    // show error message
                    middlePanelInfo.add(errorField);
                    middlePanelInfo.revalidate();
                    middlePanelInfo.repaint();
                }

            }
        });
    }

    /**
     * Method for showing the last updated time
     */
    private void updateTime()
    {
        timeUpdated = new Timestamp(System.currentTimeMillis());
        bottomLabel.setText("Updated at " + new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date()));
        bottomPanel.revalidate();
        bottomLabel.repaint();
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
