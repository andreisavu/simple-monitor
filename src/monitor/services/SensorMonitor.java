/*
 * Stream monitor
 */

package monitor.services;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitor.MessagesQueue;

public class SensorMonitor implements Monitor {

    private Logger log = Logger.getLogger(SensorMonitor.class.getName());
    private MessagesQueue queue;
    private String fail;
    private String success;
    private int checkInterval;
    private int maxLevel;

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "StreamMonitor FAIL");
        success = c.getProperty("success_message", "StreamMonitor SUCCESS");
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "5000"));
        maxLevel = Integer.parseInt(c.getProperty("max_level", "10"));
    }

    public void run() {
        log.info("Starting stream monitor");
        int[] values = new int[]{1,2,5,5,1,1,1,2};
        int count = 0;
        boolean notified = false;
        while(true) {
            log.info("Current value: " + values[count]);
            if(values[count] > maxLevel) {
                if(!notified) {
                    queue.queueMessage(fail);
                    notified = true;
                }
            } else {
                if(notified) {
                    queue.queueMessage(success);
                    notified = false;
                }
            }
            count++;
            if(count >= values.length) {
                count = 0;
            }
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

}
