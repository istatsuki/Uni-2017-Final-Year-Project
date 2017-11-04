package resources;

import javax.swing.*;
import java.awt.*;

/**
 * <h1> Error Frame</h1>
 * The error frame
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class ErrorFrame
{
    /**
     * Constructor
     * @param message The error message
     */
    public ErrorFrame(String message)
    {
        // Creating the components
        JFrame frame = new JFrame("Error");
        frame.setSize(200, 100);

        JPanel panel = new JPanel(new FlowLayout());
        frame.add(panel);

        JLabel error = new JLabel(message);
        JLabel error1 = new JLabel("Please check server info");

        panel.add(error);
        panel.add(error1);

        frame.setVisible(true);
    }
}
