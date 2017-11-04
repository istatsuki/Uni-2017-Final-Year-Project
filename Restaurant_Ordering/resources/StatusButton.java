package resources;

import javax.swing.*;
import java.awt.*;

/**
 * <h1> Status button</h1>
 * The class for representing a status button
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StatusButton extends JButton
{
    /**
     * Constructor
     * @param text The text displayed on the button
     */
    public StatusButton(String text)
    {
        super(text);
        setFont(new Font("Arial", Font.BOLD, 9));
        setMargin(new Insets(0,0,0,0));
    }
}
