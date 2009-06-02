/*
 * Monitor file size
 */
package monitor.services;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitor.MessagesQueue;

public class FileSizeMonitor implements Monitor {

    private Logger log = Logger.getLogger(FileSizeMonitor.class.getName());
    private MessagesQueue queue;
    private String fail;
    private String success;
    private int checkInterval,  maxFileSize;
    private String path;

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "PortMonitor FAIL");
        success = c.getProperty("success_message", "PortMonitor SUCCESS");
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "2000"));
        maxFileSize = Integer.parseInt(c.getProperty("max_size", "2000"));
        path = c.getProperty("path", "access.log");
    }

    public void run() {
        log.info("File size monitor: " + path);
        boolean notified = false;
        while (true) {
            File f = new File(path);
            if (f.length() > maxFileSize) {
                if (!notified) {
                    queue.queueMessage(fail);
                    notified = true;
                }
            } else {
                if (notified) {
                    queue.queueMessage(success);
                    notified = false;
                }

            }
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
