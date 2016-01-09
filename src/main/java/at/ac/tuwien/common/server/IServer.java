package at.ac.tuwien.common.server;

import at.ac.tuwien.common.entity.Order;
import at.ac.tuwien.common.view.INotificationCallback;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A common interface for different server types (e.g. {@link at.ac.tuwien.rmi.IRMIServer} , {@link at.ac.tuwien.xvsm.XVSMServer}
 */
public interface IServer {

    /**
     * Registers the callback which will be used to notify the GUI about the changes happening on the server side
     * @param notificationCallback
     */
    void registerGUINotificationCallback(INotificationCallback notificationCallback);

    /**
     * Starts the server by establishing the connection to <code>{@link at.ac.tuwien.common.connection.IConnection#setHost(String)}</code> and
     * <code>{@link at.ac.tuwien.common.connection.IConnection#setPort(int)}</code>
     */
    void start();
    void stop();

    /**
     * Returns the value of host to which the connection has been established
     * @return host in string format
     */
    String getHost();

    /**
     * Returns the  port number to which the connection has been ported
     * @return port number as integer
     */
    int getPort();

    /**
     * Notifies server if an <code>order</code> has been added through GUI, hence needs to be processed
     * @param order added on GUI
     */
    void addOrder(Order order);
}
