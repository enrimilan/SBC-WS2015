package at.ac.tuwien.common.connection;

import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.*;
import at.ac.tuwien.common.robot.SupplierRobot;

/**
 * A common interface for different connection types (e.g {@link at.ac.tuwien.rmi.RmiConnection} , {@link at.ac.tuwien.xvsm.XVSMConnection})
 */
public interface IConnection {

    /**
     * Sets the <code>host</code> to which the connection will be established
     * @param host to which the connection will be established
     */
    void setHost(String host);

    /**
     * Sets the <code>port</code> to which the connection will be established
     * @param port numberto which the host will bind
     */
    void setPort(int port);

    /**
     * Establishes a connection to the given <code>host</code> and <code>port</code>
     * @param host to which the connection will be relied on
     * @param port to which the connection will bind
     * @throws ConnectionException if there are issues with the connection
     */
    void establish(String host, int port) throws ConnectionException;

    /**
     * Gives the sum of all stored parts which have same features as the <code>part</code> given in the parameter
     * @param part the part, features of which will be looked up
     * @return number of parts stored as int
     * @throws ConnectionException if there are issues with the connection
     */
    int getAmount(Part part) throws ConnectionException;

    /**
     * Will be used by {@link at.ac.tuwien.common.robot.SupplierRobot} to supply parts
     * @param part to be supplied
     * @throws ConnectionException if there are issues with the connection
     */
    void supply(Part part) throws ConnectionException;


    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.PaintingRobot} and immediately
     * checks if there is work that can be done by the robot (e.g. cases that can be painted)
     * @param paintedNotification callback notification for painting robot
     * @throws ConnectionException if there are issues with the connection
     */
    void registerPaintingRobot(IPaintedNotification paintedNotification) throws ConnectionException;

    /**
     * Checks if the given <code>part</code> is being painted, otherwise it will be set to be painted.
     * Calls {@link IConnection#supply(Part)}
     * @param part to be painted
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException if there are issues with the connection
     */
    void partPainted(Part part, Job job) throws ConnectionException;


    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.AssemblyRobot} and immediately
     * checks if there is work that can be done by the robot (i.e. parts and modules waiting to be assembled)
     * @param assemblyRobotNotification callback notification for {@link at.ac.tuwien.common.robot.AssemblyRobot}
     * @throws ConnectionException if there are issues with the connection
     */
    void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException;

    /**
     * Checks if  the given <code>module</code> is being assembled, otherwise it will proceed with assembling.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onModuleAdded(Module)} to notify GUI about the changes
     * @param module which has been assembled will be added to the modules depending on its type
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException if there are issues with the connection
     */
    void moduleAssembled(Module module, Job job) throws ConnectionException;

    /**
     * Checks if  the given <code>drone</code> is fully assembled and proceeds with calibration, otherwise it will proceed with assembling.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onDroneAdded(Drone)} to notify GUI about the changes
     * @param drone to be checked if uts fully assembled
     * @param job  from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException
     */
    void droneAssembled(Drone drone, Job job) throws ConnectionException;

    /**
     * Adds (i.e. registers) a new  {@link at.ac.tuwien.common.robot.CalibrationRobot} and immediately
     * checks if there is work that can be done by the robot (i.e. drones and modules waiting to be calibrated)
     * @param calibrationRobotNotification callback notification for {@link at.ac.tuwien.common.robot.CalibrationRobot}
     * @throws ConnectionException if there are issues with the connection
     */
    void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException;

    /**
     * Checks if  the given <code>module</code>  has been calibrated, otherwise it will proceed with calibration.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onModuleAdded(Module)}
     * @param module to be calibrated
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException if there are issues with the connection
     */
    void motorRotorPairCalibrated(Module module, Job job) throws ConnectionException;

    /**
     * Checks if  the given <code>drone</code>  has been fully calibrated (i.e. all its containing modules), otherwise it will proceed with calibration.
     * @param drone to be calibrated
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException if there are issues with the connection
     */
    void droneCalibrated(Drone drone, Job job) throws ConnectionException;

    /**
     * Adds (i.e. registers) a new {@link at.ac.tuwien.common.robot.CalibrationRobot} and immediately checks if there is work that
     * can be done by the robot (i.e. already fully calibrated drones waiting to be tested)
     * @param logisticRobotNotification callback notification for {@link at.ac.tuwien.common.robot.LogisticRobot}
     * @throws ConnectionException if there are issues with the connection
     */
    void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException;

    /**
     * Checks if  the given <code>drone</code> has been tested (i.e. sum of calibration values of all modules must not exceed the given range by user),
     * otherwise it will proceed with testing.
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onDroneRemoved(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onGoodDroneTested(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onBadDroneTested(Drone)}
     * Calls {@link at.ac.tuwien.common.view.INotificationCallback#onOrderModified(Order)}
     * @param drone to be tested
     * @param job from which the {@link  at.ac.tuwien.rmi.Transaction} will be notified  about the status of the work and proceed respectively
     * @throws ConnectionException if there are issues with the connection
     */
    void droneTested(Drone drone, Job job) throws ConnectionException;

    /**
     * End the connection
     * @throws ConnectionException issues while closing the connection
     */
    void end() throws ConnectionException;
}
