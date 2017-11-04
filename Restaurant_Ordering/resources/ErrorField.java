package resources;

import javax.swing.*;
import java.awt.*;

/**
 * <h1> Error Field</h1>
 * Error field label
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class ErrorField extends JLabel
{
    /**
     * Constructor
     * @param ErrorMessage The message
     * @param component The components with the error
     * @param heightSpace The height space
     */
    public ErrorField(String ErrorMessage, JComponent component, int heightSpace)
    {
        super(ErrorMessage);
        setPreferredSize(new Dimension(150, 20));
        setForeground(Color.red);
        setBounds(new Rectangle(new Point(component.getX(), component.getY() + heightSpace), getPreferredSize()));
    }
}
