/*
 * Application entry point
 *
 * Load configuration, start ui and the messages queue
 *
 */
package monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;
import monitor.services.Monitor;

/**
 * Application entry point
 */
public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());
    private static Properties config = new Properties();
    private static MessagesQueue queue;
    private static ThreadPoolExecutor pool;

    /**
     * Application entry point function
     * 
     * @param args
     */
    public static void main(String[] args) {
        log.info("Starting service monitor application.");
        try {
            loadConfigurations();
            queue = new MessagesQueue(config);
            startMonitors();
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } catch (Exception ex) {
            log.severe(ex.getMessage());
        }
    }

    private static void loadConfigurations() throws IOException {
        String configFile = "config.properties";
        InputStream s = Main.class.getResourceAsStream(configFile);
        config.load(s);
        log.info("Configuration: " + config.toString());
    }

    private static void startMonitors() {
        String[] checks = config.getProperty("checks").split(",");
        List<Monitor> monitors = new LinkedList<Monitor>();
       
        for(String check : checks) {
            Properties checkConfig = new Properties();
            try {
                checkConfig.load(Main.class.getResourceAsStream(check + ".properties"));
                log.info("Monitor Config:" + checkConfig.toString());

                Class c = Class.forName(checkConfig.getProperty("class"));
                Monitor m = (Monitor) c.newInstance();

                m.registerQueue(queue);
                m.config(checkConfig);
                monitors.add(m);

            } catch (Exception ex) {
                log.severe(ex.toString());
                continue;
            }
        }

        log.info("Starting all monitors ....");
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(monitors.size());
        for(Monitor m : monitors) {
            pool.execute(m);
        }
        log.info("Done starting all monitors.");
    }

}
