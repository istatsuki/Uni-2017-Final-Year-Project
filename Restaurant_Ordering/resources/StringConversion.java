package resources;

import java.util.ArrayList;

/**
 * <h1> String conversion</h1>
 * The class for convert a string to array list and vice versa
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StringConversion
{
    /**
     * Method to convert an array list to a string
     * @param stringArrayList The array list
     * @return String The String
     */
    public static String arrayListToString(ArrayList<String> stringArrayList)
    {
        String string = "";
        if(!stringArrayList.isEmpty())
        {
            for (int i = 0; i < stringArrayList.size(); i++) {
                string = string + stringArrayList.get(i) + ";";
            }
            return string.substring(0, string.length() - 1);
        }
        else
        {
            return string;
        }

    }

    /**
     * Method to convert a string to an array list
     * @param string The String
     * @return ArrayList<String> The array list
     */
    public static ArrayList<String> stringToArrayList(String string)
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        while(string.contains(";"))
        {
            arrayList.add(string.substring(0, string.indexOf(";")));
            string = string.substring(string.indexOf(";") + 1);
        }
        arrayList.add(string);
        return arrayList;
    }
}
