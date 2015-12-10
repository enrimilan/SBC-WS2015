package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.entity.Drone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILogisticRobotNotification extends Remote {

    void testDrone(Drone drone) throws RemoteException;
}
