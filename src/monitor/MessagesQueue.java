/*
 * Notification Messages Queue
 *
 * Multi thread safe notification messages queue
 */
package monitor;

import java.util.Properties;
import java.util.logging.Logger;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class MessagesQueue {

    private Logger log = Logger.getLogger(MessagesQueue.class.getName());
    
    private String outgoingNo = "";
    private String id = "",  port = "",  manufacturer = "",  model = "";
    private int gwSpeed = 0;
    
    private Service srv;
    private SerialModemGateway gateway;
    private OutboundNotification outboundNotification;

    MessagesQueue(Properties config) {

        outgoingNo = config.getProperty("notification_number", "");
        port = config.getProperty("gateway.port", "COM3");
        gwSpeed = Integer.parseInt(config.getProperty("gateway.speed"));
        manufacturer = config.getProperty("gateway.manufacturer");
        model = config.getProperty("gateway.model");

        initGateway();
        initOutboundNotification();
        initService();
    }

    public void initService() {
        srv = new Service();
        srv.setOutboundNotification(outboundNotification);
        try {
            srv.addGateway(gateway);
            srv.startService();
        } catch (Exception ex) {
            log.severe(ex.toString());
            try {
                srv.stopService();
            } catch (Exception ex1) {}
        }
    }

    public void initGateway() {
        gateway = new SerialModemGateway(id, port, gwSpeed, manufacturer, model);
        gateway.setInbound(true);
        gateway.setOutbound(true);
    }

    public void initOutboundNotification() {
        outboundNotification = new OutboundNotification();
    }

    public boolean queueMessage(String text) {
        log.info("Sending notification: " + text);
        OutboundMessage msg = new OutboundMessage(outgoingNo, text);
        try {
            return srv.queueMessage(msg);
        } catch (Exception ex) {
            log.severe(ex.toString());
            return false;
        }
    }

    public class OutboundNotification implements IOutboundMessageNotification {

        private Logger log = Logger.getLogger(OutboundNotification.class.getName());

        public void process(String gatewayId, OutboundMessage msg) {
            log.info("Outbound notification from gateway: " + gatewayId);
            log.info("Notification message: " + msg.toString());
        }
    }
}
