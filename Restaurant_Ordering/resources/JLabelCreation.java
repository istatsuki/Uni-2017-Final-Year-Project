package resources;

import javax.swing.*;
import java.awt.*;

/**
 * <h1> Label Creation</h1>
 * The class for creating label
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class JLabelCreation
{
    /**
     * Method to create the labels
     * @param panel The panel holding the label
     * @param titles The lists of the titles
     * @param widths The lists of the widths
     * @param height The height
     * @param spaceWidth The width of the spaces between the labels
     */
    public static void createLabels(JPanel panel,String[] titles, int[] widths, int height, int spaceWidth)
    {
        // The lists need to be the same size
        if(titles.length !=  widths.length)
        {
                throw new IllegalArgumentException("titles and sizes has to be the same length");

        }
        for(int i = 0; i < titles.length; i++)
        {
            // Create each labels
            JLabel label = new JLabel("<HTML><U>" + titles[i] + "<HTML><U>", SwingConstants.CENTER);

            int boundWidth = 0;
            for(int k = 1; k <= i; k++)
            {
                boundWidth += ( widths[k-1] + spaceWidth);
            }

            label.setPreferredSize(new Dimension(widths[i], height));
            label.setBounds(new Rectangle(new Point(boundWidth, 0), label.getPreferredSize()));
            panel.add(label);
        }
    }
}
