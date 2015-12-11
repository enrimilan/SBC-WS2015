package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Drone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITestedNotification extends Remote {

    void testDrone(Drone drone) throws RemoteException;
}
