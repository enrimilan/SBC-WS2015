package at.ac.tuwien.common.server;

import at.ac.tuwien.common.view.INotificationCallback;

public interface IServer {

    void registerGUINotificationCallback(INotificationCallback notificationCallback);
    void start();
    void stop();
    String getHost();
    int getPort();

}
