package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * An interface to notify 'Logistic' robot for the various available work it can conduct
 */
public interface ITestedNotification extends Remote {

    /**
     * Tests the drone depending on the value range given by the user when the robot was started
     * @param drone which will be tested
     * @param job keeps the transaction informed about the progress of calibration conducted by this method
     * @param orderId the ID of the order to which the drone belongs
     * @throws RemoteException
     */
    void testDrone(Drone drone, Job job, UUID orderId) throws RemoteException;
}
