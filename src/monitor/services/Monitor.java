/*
 * Basic service monitor interface
 *
 * Each monitor is implemented as a thread that will run inside a pool
 * 
 */

package monitor.services;

import java.util.Properties;
import monitor.MessagesQueue;

public interface Monitor extends Runnable {

    void registerQueue(MessagesQueue m);

    void config(Properties c);

}
