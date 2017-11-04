package frontOfHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * <h1> Front Of House Server</h1>
 * The server for communicating with the other sub-system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
class FrontOfHouseServer
{
    // The server object
    private ServerSocket myServerSocket;

    // The actual sub-system
    private FrontOfHouse frontOfHouse = FrontOfHouse.instance();

    // The online status of the server
    private boolean ServerOn = true;

    /**
     * Constructor
     * @param portNum The port number where the server should run on
     */
    FrontOfHouseServer(int portNum)
    {
        try
        {
            // Try to open the server in the port number
            myServerSocket = new ServerSocket(portNum);
            System.out.println("Server created on " + InetAddress.getLocalHost().getHostAddress() + ":" + myServerSocket.getLocalPort());
        }
        catch (IOException ioe)
        {
            System.out.println("Could not create server socket on port " + portNum + ". Quitting.");
            System.exit(-1);
        }

        // Successfully created Server Socket. Now wait for connections.
        while (ServerOn)
        {
            try
            {
                // Accept incoming connections.
                Socket clientSocket = myServerSocket.accept();

                // The multi-thread service for connections
                ClientServiceThread thread = new ClientServiceThread(clientSocket);
                thread.start();

            }
            catch (IOException ioe)
            {
                System.out.println("Exception encountered on accept. Ignoring. Stack Trace :");
                ioe.printStackTrace();
            }

        }

        // close the server
        try
        {
            myServerSocket.close();
            System.out.println("Server Stopped");
        }
        catch (Exception ioe)
        {
            System.out.println("Problem stopping server socket");
            System.exit(-1);
        }
    }

    /**
     * Class representing each thread
     */
    class ClientServiceThread extends Thread
    {
        // The socket for accepting client
        Socket myClientSocket;

        // The online status of the thread
        boolean threadOn = true;

        /**
         * Constructor
         * @param s The socket
         */
        ClientServiceThread(Socket s)
        {
            super();
            myClientSocket = s;
        }

        /**
         * Running operation
         */
        public void run()
        {
            // Initiate the streams
            DataInputStream in = null;
            DataOutputStream out = null;

            // Print out details of this connection
            System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostAddress());

            try
            {
                // Getting the streams from the socket
                in = new DataInputStream(myClientSocket.getInputStream());
                out = new DataOutputStream(myClientSocket.getOutputStream());

                // read single incoming stream
                String clientCommand = in.readUTF();
                System.out.println("Client Says :" + clientCommand);

                // Thread behaviour for different commands
                Callable<String> workerThread = new FOHWorkerThreadCallable(clientCommand);
                out.writeUTF(frontOfHouse.getExecutor().submit(workerThread).get());
                out.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                // Clean up
                try
                {
                    in.close();
                    out.close();
                    myClientSocket.close();
                    System.out.println("...Stopped");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
}

