package at.ac.tuwien.common.connection;

import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.*;

public interface IConnection {

    void setHost(String host);
    void setPort(int port);

    void establish(String host, int port) throws ConnectionException;

    int getAmount(Part part) throws ConnectionException;
    void supply(Part part) throws ConnectionException;

    void registerPaintingRobot(IPaintedNotification paintedNotification) throws ConnectionException;
    void partPainted(Part part, Job job) throws ConnectionException;

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
