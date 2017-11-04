package resources;

import java.io.File;

/**
 * <h1> File reader</h1>
 * The class for giving the string representation of an image file location
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FileReader
{
    /**
     * Constructor
     */
    public FileReader()
    {
    }

    /**
     * method to read a file
     * @param file The image file
     * @return String the file location
     */
    public String read(String file)
    {
        String currentProcess = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        return currentProcess + "image" + File.separator + file;
    }
}
