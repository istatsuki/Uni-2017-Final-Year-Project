package kitchenAndBar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Kitchen and Bar Launcher</h1>
 * The class for launching the kitchen and bar sub system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class KitchenAndBarLauncher {
    public static void main(String[] args)
    {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable guiCreation = new Runnable() {
            @Override
            public void run()
            {

                KitchenAndBarGUI gui = new KitchenAndBarGUI();
            }
        };

        Runnable serverCreation = new Runnable() {
            @Override
            public void run()
            {
                KitchenAndBarServer kitchenAndBarServer = new KitchenAndBarServer(8200);
            }
        };

        executor.execute(guiCreation);
        executor.execute(serverCreation);
    }
}
