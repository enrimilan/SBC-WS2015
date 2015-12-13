package at.ac.tuwien.xvsm.listener;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Status;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.xvsm.XVSMServer;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.List;

public class TestedDronesListener implements NotificationListener {

    private XVSMServer server;
    private INotificationCallback notificationCallback;

    public TestedDronesListener(XVSMServer server, INotificationCallback notificationCallback){
        this.server = server;
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> list) {
        Entry entry = (Entry) list.get(0);
        Drone d = (Drone) entry.getValue();
        if(operation == Operation.WRITE) {
            notificationCallback.onDroneRemoved(d);
            if (d.getStatus() == Status.TESTED_GOOD) {
                notificationCallback.onGoodDroneTested(d);
            } else if (d.getStatus() == Status.TESTED_BAD) {
                notificationCallback.onBadDroneTested(d);
            }
        }
    }
}
