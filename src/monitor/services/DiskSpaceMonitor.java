/*
 * Monitor disk usage and warn if free space is bellow a given limit
 */

package monitor.services;

import java.util.Properties;
import java.util.logging.Logger;
import monitor.MessagesQueue;

public class DiskSpaceMonitor implements Monitor {

    private Logger log = Logger.getLogger(DiskSpaceMonitor.class.getName());
    private MessagesQueue queue;
    private String fail;
    private String success;
    private int checkInterval;

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "DiskSpaceMonitor FAIL");
        success = c.getProperty("success_message", "DiskSpaceMonitor SUCCESS");
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "2000"));
    }

    public void run() {
        log.info("Start disk space monitor");
        while(true) {
            /**
             * @todo: get disk usage and check limit
             *
             * http://stackoverflow.com/questions/47177/how-to-monitor-the-computers-cpu-memory-and-disk-usage-in-java
             */
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

}
