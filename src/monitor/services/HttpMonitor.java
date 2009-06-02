/*
 * Simple HTTP Server monitor
 */
package monitor.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Logger;
import monitor.MessagesQueue;

public class HttpMonitor implements Monitor {

    Logger log = Logger.getLogger(HttpMonitor.class.getName());
    String fail, success, url;
    int checkInterval;
    MessagesQueue queue;

    public void registerQueue(MessagesQueue m) {
        queue = m;
    }

    @SuppressWarnings("empty-statement")
    public void run() {
        log.info("Start http monitor: " + url);
        boolean notified = false;
        while (true) {
            try {
                URL u = new URL(url);
                URLConnection c = u.openConnection();
                c.setUseCaches(false);
                BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream()));

                while (rd.readLine() != null);
                rd.close();
                
                if(notified) {
                    queue.queueMessage(success);
                }
                notified = false;
            } catch (MalformedURLException ex) {
                log.severe(ex.getMessage());
                break;
            } catch (IOException ex) {
                if(!notified) {
                    queue.queueMessage(fail);
                    notified = true;
                }
            }

            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    public void config(Properties c) {
        fail = c.getProperty("fail_message", "HttpMonitor FAIL");
        success = c.getProperty("success_message", "HttpMonitor SUCCESS");
        checkInterval = Integer.parseInt(c.getProperty("check_interval", "2000"));
        url = c.getProperty("url", "http://127.0.0.1/");
    }
}
