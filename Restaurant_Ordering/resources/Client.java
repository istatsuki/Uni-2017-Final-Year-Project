package resources;

import java.io.*;
import java.net.Socket;

/**
 * <h1> Client Tool</h1>
 * The class for making a request to a server
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Client
{
    // Information seperator
    public static String infoSeperator = "%#";

    /**
     * Constructor to make the connection to front of house
     * @param output The content of the request
     */
    public static String clientToFOH(String output)
    {
        String host = "127.0.0.1";
        int portNum = 8100;
        return connect(output, host, portNum);
    }

    /**
     * Constructor to make the connection to kitchen and bar
     * @param output The content of the request
     */
    public static String clientToKAB(String output)
    {
        String host = "127.0.0.1";
        int portNum = 8200;
        return connect(output, host, portNum);
    }

    /**
     * Constructor to make the connection to stock manager
     * @param output The content of the request
     */
    public static String clientToStock(String output)
    {
        String host = "127.0.0.1";
        int portNum = 8000;
        return connect(output, host, portNum);
    }

    /**
     * Constructor to make the connection to a station
     * @param output The content of the request
     */
    public static String clientToStation(String output, String stationID, int portNum)
    {
        String host = "127.0.0.1";
        return connect(output, host, portNum);
    }

    /**
     * Method for making the connection
     * @param output The ouput line
     * @param host The hose name
     * @param portNum The port number
     * @return String the response from the server
     */
    private static String connect(String output, String host, int portNum)
    {
        String outputLine = "";
        try
        {
            // Making the conncetion
            System.out.println("Connecting to " + host + " on port " + portNum);
            Socket client = new Socket(host, portNum);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            // Getting the request sender to send the request
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(output);

            // Getting the response receiver to receive the response
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            outputLine = in.readUTF();

            // close the connection after finish
            client.close();

            return outputLine;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return outputLine;
    }
}
