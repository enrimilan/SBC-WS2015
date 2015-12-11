package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Module;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICalibratedNotification extends Remote {

    void calibrateMotorRotorPair(Module module, Job job) throws RemoteException;
    void calibrateModuleInDrone(Drone drone, Job job) throws RemoteException;
}
