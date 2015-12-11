package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITestedNotification extends Remote {

    void testDrone(Drone drone, Job job) throws RemoteException;
}
