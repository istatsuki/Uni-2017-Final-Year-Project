package kABStation;

import resources.Client;
import resources.ErrorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Kitchen or Bar Station Launcher</h1>
 * The class for launching the sub-system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KABStationLauncher
{
    // The database setting
    private static String databaseURL = "jdbc:mysql://127.0.0.1:5000/resttest?useSSL=false";
    private static String userName = "root";
    private static String password = "looping";

    public static void main(String[] args)
    {
        // Creating the components
        JFrame newFrame = new JFrame("Station Launcher");
        newFrame.setSize(300, 150);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setVisible(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        newFrame.add(mainPanel);

        JLabel userNameLabel = new JLabel("UserName:");
        JTextField userNameTF = new JTextField();
        userNameTF.setPreferredSize(new Dimension(150, 20));
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(userNameLabel);
        topPanel.add(userNameTF);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordTF = new JPasswordField();
        passwordTF.setPreferredSize(new Dimension(150, 20));
        JPanel middlePanel = new JPanel(new FlowLayout());
        middlePanel.add(passwordLabel);
        middlePanel.add(passwordTF);
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Initiating the resources for the database connection
                Connection conn = null;
                PreparedStatement preparedStmt = null;

                try {
                    // Register JDBC driver
                    Class.forName("com.mysql.jdbc.Driver");

                    // Open a connection
                    System.out.println("Connecting to database...");
                    conn = DriverManager.getConnection(databaseURL, userName, password);

                    // Initiating more resources
                    System.out.println("Creating statement...");
                    String sql;
                    ResultSet rs;

                    // getting all the combinations of the stations identification and check the info given
                    sql = "SELECT * FROM stations WHERE username = ?";
                    preparedStmt = conn.prepareStatement(sql);
                    preparedStmt.setString(1, userNameTF.getText());

                    rs = preparedStmt.executeQuery();

                    if(!rs.next())
                    {
                        ErrorFrame errorFrame = new ErrorFrame("No existing username");
                    }
                    else
                    {
                        if(!passwordTF.getText().equals(rs.getString("password")))
                        {
                            ErrorFrame errorFrame = new ErrorFrame("Wrong password");
                        }
                        else
                        {
                            String stationID = rs.getString("stationID");
                            int portNum = rs.getInt("portNum");

                            String reply = Client.clientToKAB("StationOnline/" + stationID);

                            if(reply.contains("Station Online"))
                            {
                                newFrame.setVisible(false);
                                ExecutorService executor = Executors.newFixedThreadPool(2);

                                KABStationInstance kabStationInstance = new KABStationInstance(stationID);

                                Runnable guiCreation = new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        KABStationGUI gui = new KABStationGUI(kabStationInstance);
                                    }
                                };

                                Runnable serverCreation = new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        KABStationServer server = new KABStationServer(portNum, kabStationInstance);
                                    }
                                };

                                executor.execute(guiCreation);
                                executor.execute(serverCreation);
                            }
                            else
                            {
                                ErrorFrame errorFrame = new ErrorFrame(reply);
                            }
                        }
                    }

                    // Closing the resources
                    rs.close();
                    preparedStmt.close();
                    conn.close();
                }
                catch(Exception e1)
                {
                    // Handle errors for Class.forName
                    e1.printStackTrace();
                    System.exit(0);
                }
                finally
                {
                    // Block used to close resources
                    try
                    {
                        if(preparedStmt!=null)
                        {
                            preparedStmt.close();
                        }
                    }
                    catch(SQLException se2)
                    {
                    }// Nothing we can do

                    try
                    {
                        if(conn!=null)
                        {
                            conn.close();
                        }
                    }
                    catch(SQLException se)
                    {
                        se.printStackTrace();
                    }
                }
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(connectButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        newFrame.revalidate();
        newFrame.repaint();
    }
}
