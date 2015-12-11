package at.ac.tuwien.rmi;

import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRMIServer extends Remote {

    void supply(Part part) throws RemoteException;

    void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws RemoteException;
    void moduleAssembled(Module module) throws RemoteException;
    void droneAssembled(Drone drone) throws RemoteException;

    void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws RemoteException;
    void motorRotorPairCalibrated(Module module) throws RemoteException;
    void droneCalibrated(Drone drone) throws RemoteException;

    void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws RemoteException;
    void droneTested(Drone drone) throws RemoteException;

}
