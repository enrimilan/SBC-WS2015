package at.ac.tuwien.connection;

import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.robot.IAssemblyRobotNotification;
import at.ac.tuwien.robot.ICalibrationRobotNotification;
import at.ac.tuwien.robot.ILogisticRobotNotification;
import at.ac.tuwien.robot.LogisticRobot;

public interface IConnection {

    void establish() throws ConnectionException;
    void supply(Part part) throws ConnectionException;
    void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws ConnectionException;
    void moduleAssembled(Module module) throws ConnectionException;
    void droneAssembled(Drone drone) throws ConnectionException;
    void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws ConnectionException;
    void motorRotorPairCalibrated(Module module) throws ConnectionException;
    void droneCalibrated(Drone drone) throws ConnectionException;
    void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws ConnectionException;
    void droneTested(Drone drone) throws ConnectionException;
}
