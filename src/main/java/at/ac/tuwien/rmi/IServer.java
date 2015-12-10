package at.ac.tuwien.rmi;

import at.ac.tuwien.common.robot.IAssemblyRobotNotification;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.robot.ICalibrationRobotNotification;
import at.ac.tuwien.common.robot.ILogisticRobotNotification;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

    void supply(Part part) throws RemoteException;

    void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws RemoteException;
    void moduleAssembled(Module module) throws RemoteException;
    void droneAssembled(Drone drone) throws RemoteException;

    void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws RemoteException;
    void motorRotorPairCalibrated(Module module) throws RemoteException;
    void droneCalibrated(Drone drone) throws RemoteException;

    void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws RemoteException;
    void droneTested(Drone drone) throws RemoteException;

}
