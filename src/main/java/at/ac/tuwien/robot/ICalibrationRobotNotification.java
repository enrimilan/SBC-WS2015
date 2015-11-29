package at.ac.tuwien.robot;

import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;

import java.rmi.RemoteException;

public interface ICalibrationRobotNotification {

    void calibrateMotorRotorPair(Module module) throws RemoteException;
    void calibrateModuleInDrone(Drone drone) throws RemoteException;
}
