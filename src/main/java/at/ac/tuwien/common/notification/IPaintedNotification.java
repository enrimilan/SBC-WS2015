package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Color;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * An interface to notify 'Painter' robot for the various available work it can conduct
 */
public interface IPaintedNotification extends Remote {

    /**
     * Paint the given part of type Case with color (RED | GREEN | BLUE)
     * @param part of type Case to be painted
     * @param color with which the part will be painted
     * @param job keeps the transaction informed about the progress of painting conducted by this method
     * @param orderId the ID of the order for which the case will be painted
     * @throws RemoteException
     */
    void paintPart(Part part, Color color, Job job, UUID orderId) throws RemoteException;
}
