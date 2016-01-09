package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Module;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface to notify 'Calibrator' robot for the various available work it can conduct
 * Calibration is done through setting a randomly generated value (in the range -10 to 10) to a motor-rotor modules
 */
public interface ICalibratedNotification extends Remote {

    /**
     * Calibrates give motor-rotor module which is not yet assembled to a drone
     * @param module of type motor-rotor to be calibrated
     * @param job keeps the transaction informed about the progress of calibration conducted by this method
     * @throws RemoteException
     */
    void calibrateMotorRotorPair(Module module, Job job) throws RemoteException;

    /**
     * Calibrates motor-rotor modules already assembled in a drone
     * @param drone for which the motor-rotor modules will be calibrated
     * @param job keeps the transaction informed about the progress of calibration conducted by this method
     * @throws RemoteException
     */
    void calibrateModuleInDrone(Drone drone, Job job) throws RemoteException;
}
