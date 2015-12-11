package at.ac.tuwien.common.connection;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;

public interface IConnection {

    void establish() throws ConnectionException;

    void supply(Part part) throws ConnectionException;

    void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException;
    void moduleAssembled(Module module, Job job) throws ConnectionException;
    void droneAssembled(Drone drone, Job job) throws ConnectionException;

    void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException;
    void motorRotorPairCalibrated(Module module, Job job) throws ConnectionException;
    void droneCalibrated(Drone drone, Job job) throws ConnectionException;

    void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException;
    void droneTested(Drone drone, Job job) throws ConnectionException;

    void end() throws ConnectionException;
}
