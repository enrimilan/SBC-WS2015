package at.ac.tuwien.common.server;

import at.ac.tuwien.common.entity.Order;
import at.ac.tuwien.common.view.INotificationCallback;

import java.util.concurrent.CopyOnWriteArrayList;

public interface IServer {

    void registerGUINotificationCallback(INotificationCallback notificationCallback);
    void start();
    void stop();
    String getHost();
    int getPort();
    void addOrder(Order order);
}
