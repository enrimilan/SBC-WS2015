package at.ac.tuwien.xvsm.listener;

import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.xvsm.XVSMServer;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.List;

public class PartsListener implements NotificationListener {

    private XVSMServer server;
    private INotificationCallback notificationCallback;

    public PartsListener(XVSMServer server, INotificationCallback notificationCallback){
        this.server = server;
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> list) {

        if(operation == Operation.WRITE){
            for(Object o : list){
                Entry entry = (Entry) o;
                Part p = (Part) entry.getValue();
                notificationCallback.onPartAdded(p);
            }
            server.checkForWorkWithPartsForAssemblyRobot();
        }
        if(operation == Operation.TAKE){
            for(Object o : list){
                notificationCallback.onPartRemoved((Part) o);
            }
        }
    }
}
