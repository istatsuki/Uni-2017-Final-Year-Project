package frontOfHouse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Front Of House Launcher</h1>
 * The class for launching the front of house sub system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class FrontOfHouseLauncher
{
    public static void main(String[] args)
    {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable guiCreation = new Runnable() {
            @Override
            public void run()
            {
                FrontOfHouseGUI fOHGUI = new FrontOfHouseGUI();
            }
        };

        Runnable serverCreation = new Runnable() {
            @Override
            public void run()
            {
                FrontOfHouseServer frontOfHouseServer = new FrontOfHouseServer(8100);
            }
        };

        executor.execute(guiCreation);
        executor.execute(serverCreation);
    }
}
