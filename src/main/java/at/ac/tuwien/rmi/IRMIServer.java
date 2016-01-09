package at.ac.tuwien.rmi;

import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.IPaintedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the server of type {@link RMIServer} (analogous as {@link at.ac.tuwien.common.connection.IConnection)}
 */
public interface IRMIServer extends Remote {

    /**
     * Gives the sum of all stored parts which have same features as the <code>part</code> given in the parameter
     * @param part the part, features of which will be looked up
     * @return number of parts stored as int
     * @throws RemoteException if there are communication-related issues
     */
    int getAmount(Part part) throws RemoteException;

    /**
     * Will be used by {@link at.ac.tuwien.common.robot.SupplierRobot} to supply parts
     * @param part to be supplied
     * @throws RemoteException if there are communication-related issues
     */
    void supply(Part part) throws RemoteException;

    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.PaintingRobot} and immediately
     * checks if there is work that can be done by the robot (e.g. cases that can be painted)
     * @param paintedNotification callback notification for painting robot
     * @throws RemoteException if there are communication-related issues
     */
    void registerPaintingRobot(IPaintedNotification paintedNotification) throws RemoteException;

    /**
     * Checks if the given <code>part</code> is being painted, otherwise it will be set to be painted.
     * Calls {@link IRMIServer#supply(Part)}
     * @param part to be painted
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void partPainted(Part part, Job job) throws RemoteException;

    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.AssemblyRobot} and immediately
     * checks if there is work that can be done by the robot (i.e. parts and modules waiting to be assembled)
     * @param assemblyRobotNotification callback notification for {@link at.ac.tuwien.common.robot.AssemblyRobot}
     * @throws RemoteException if there are communication-related issues
     */
    void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws RemoteException;

    /**
     * Checks if  the given <code>module</code> is being assembled, otherwise it will proceed with assembling.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onModuleAdded(Module)} to notify GUI about the changes
     * @param module which has been assembled will be added to the modules depending on its type
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void moduleAssembled(Module module, Job job) throws RemoteException;

    /**
     * Checks if  the given <code>drone</code> is fully assembled and proceeds with calibration, otherwise it will proceed with assembling.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onDroneAdded(Drone)} to notify GUI about the changes
     * @param drone to be checked if uts fully assembled
     * @param job  from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void droneAssembled(Drone drone, Job job) throws RemoteException;

    /**
     * Adds (i.e. registers) a new  {@link at.ac.tuwien.common.robot.CalibrationRobot} and immediately
     * checks if there is work that can be done by the robot (i.e. drones and modules waiting to be calibrated)
     * @param calibrationRobotNotification callback notification for {@link at.ac.tuwien.common.robot.CalibrationRobot}
     * @throws RemoteException if there are communication-related issues
     */
    void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws RemoteException;

    /**
     * Checks if  the given <code>module</code>  has been calibrated, otherwise it will proceed with calibration.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onModuleAdded(Module)}
     * @param module to be calibrated
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void motorRotorPairCalibrated(Module module, Job job) throws RemoteException;

    /**
     * Checks if  the given <code>drone</code>  has been fully calibrated (i.e. all its containing modules), otherwise it will proceed with calibration.
     * @param drone to be calibrated
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void droneCalibrated(Drone drone, Job job) throws RemoteException;

    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.CalibrationRobot} and immediately checks if there is work that
     * can be done by the robot (i.e. already fully calibrated drones waiting to be tested)
     * @param logisticRobotNotification callback notification for {@link at.ac.tuwien.common.robot.LogisticRobot}
     * @throws RemoteException  if there are communication-related issues
     */
    void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws RemoteException;

    /**
     * Checks if  the given <code>drone</code> has been tested (i.e. sum of calibration values of all modules must not exceed the given range by user),
     * otherwise it will proceed with testing.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onDroneRemoved(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onGoodDroneTested(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onBadDroneTested(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onOrderModified(Order)}
     * @param drone to be tested
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws RemoteException if there are communication-related issues
     */
    void droneTested(Drone drone, Job job) throws RemoteException;

}
