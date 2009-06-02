/*
 * Simple HTTP Server monitor
 */

package monitor.services;

import java.util.Properties;
import monitor.MessagesQueue;

public class HttpMonitor implements Monitor {

    String fail, success, host;
    int port, checkInterval;
    MessagesQueue queue;

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "HttpMonitor FAIL");
        success = c.getProperty("success_message", "HttpMonitor SUCCESS");
        port = Integer.parseInt(c.getProperty("port", "80"));
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "2000"));
        host = c.getProperty("host", "127.0.0.1");
    }
}
