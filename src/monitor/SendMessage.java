// SendMessage.java - Sample application.
//
// This application shows you the basic procedure for sending messages.
// You will find how to send synchronous and asynchronous messages.
//
// For asynchronous dispatch, the example application sets a callback
// notification, to see what's happened with messages.
package monitor;

import java.io.InputStream;
import java.util.Properties;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class SendMessage {

    private String message = "";
    private String outgoingNo = "";
    private String appType = "";
    Service srv;
    private Properties props;
    private String port = "";
    private String pin = "";
    SerialModemGateway gateway;
    OutboundNotification outboundNotification;

    public void init() throws Exception{
        initProps();
        initGateway();
        initOutboundNotification();
        initService();
    }

    public void initProps() throws Exception {
        props = new Properties();
        String fileName = "config.properties";
        Class clazz = getClass();
        InputStream propStream = clazz.getResourceAsStream(fileName);
        props.load(propStream);
        appType = props.getProperty("app_type");
        port = props.getProperty("port");
        pin = props.getProperty("sim_pin");
    }

    public void initService() throws Exception {
        srv = new Service();
        srv.setOutboundNotification(outboundNotification);
        srv.addGateway(gateway);
        srv.startService();
    }

    public void initGateway() {
        gateway = new SerialModemGateway("modem.com1", port, 115200, "Nokia", "6310i");
        gateway.setInbound(true);
        gateway.setOutbound(true);
        gateway.setSimPin(pin);
    }

    public void initOutboundNotification() {
        outboundNotification = new OutboundNotification();
    }

    public void doIt() throws Exception {
        init();
        OutboundMessage msg;
        msg = new OutboundMessage("0788109468","Test");
        srv.sendMessage(msg);
        System.out.println(msg);
        if (appType.equalsIgnoreCase("CLIENT")) {
            srv.stopService();
        } else {
            Thread.sleep(60*1000);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutgoingNo() {
        return outgoingNo;
    }

    public void setOutgoingNo(String outgoingNo) {
        this.outgoingNo = outgoingNo;
    }

    public class OutboundNotification implements IOutboundMessageNotification {

        public void process(String gatewayId, OutboundMessage msg) {
            System.out.println("Outbound handler called from Gateway: " + gatewayId);
            System.out.println(msg);
        }
    }

    public static void main(String args[]) {
        SendMessage app = new SendMessage();
        try {
            app.doIt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
