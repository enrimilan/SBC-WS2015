package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICalibrationRobotNotification extends Remote {

    void calibrateMotorRotorPair(Module module) throws RemoteException;
    void calibrateModuleInDrone(Drone drone) throws RemoteException;
}
