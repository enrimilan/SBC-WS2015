package at.ac.tuwien.rmi;

import at.ac.tuwien.common.entity.Job;
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
    void moduleAssembled(Module module, Job job) throws RemoteException;
    void droneAssembled(Drone drone, Job job) throws RemoteException;

    void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws RemoteException;
    void motorRotorPairCalibrated(Module module, Job job) throws RemoteException;
    void droneCalibrated(Drone drone, Job job) throws RemoteException;

    void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws RemoteException;
    void droneTested(Drone drone, Job job) throws RemoteException;

}
