/*
 * Simple TCP Port Monitor
 *
 */

package monitor.services;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;
import monitor.MessagesQueue;

public class PortMonitor implements Monitor {

    private String fail, success, host;
    private int port, checkInterval;
    private MessagesQueue queue;
    private Logger log = Logger.getLogger(PortMonitor.class.getName());

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    public void run() {
        log.info("Starting port monitor loop: " + host + ":" + port );
        boolean notified = false;
        while(true) {
            try {
                Socket s = new Socket(host, port);
                if(notified) {
                    queue.queueMessage(success);
                    notified = true;
                }
            } catch (IOException ex) {
                if(!notified) {
                    queue.queueMessage(fail);
                }
                notified = true;
            }
            
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "PortMonitor FAIL");
        success = c.getProperty("success_message", "PortMonitor SUCCESS");
        port = Integer.parseInt(c.getProperty("port", "80"));
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "2000"));
        host = c.getProperty("host", "127.0.0.1");
    }
    
}
