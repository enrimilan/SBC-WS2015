package at.ac.tuwien.robot;

import at.ac.tuwien.entity.Drone;

import java.rmi.RemoteException;

public interface ILogisticRobotNotification {

    void testDrone(Drone drone) throws RemoteException;
}
