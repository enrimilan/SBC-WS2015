package at.ac.tuwien.robot;

import at.ac.tuwien.entity.Drone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILogisticRobotNotification extends Remote {

    void testDrone(Drone drone) throws RemoteException;
}
