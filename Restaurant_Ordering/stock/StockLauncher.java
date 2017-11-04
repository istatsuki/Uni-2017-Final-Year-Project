package stock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1> Stock Launcher</h1>
 * The class for launching the stock sub system
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class StockLauncher {
    public static void main(String[] args)
    {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable guiCreation = new Runnable() {
            @Override
            public void run()
            {

                StockGUI gui = new StockGUI();
            }
        };

        Runnable serverCreation = new Runnable() {
            @Override
            public void run()
            {
                StockServer server = new StockServer(8000);
            }
        };

        executor.execute(guiCreation);
        executor.execute(serverCreation);
    }
}
