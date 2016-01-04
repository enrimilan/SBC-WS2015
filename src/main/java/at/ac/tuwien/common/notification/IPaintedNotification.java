package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Color;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Part;

import java.rmi.RemoteException;

public interface IPaintedNotification {

    void paintPart(Part part, Color color, Job job) throws RemoteException;
}
