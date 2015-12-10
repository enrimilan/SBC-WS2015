package at.ac.tuwien.xvsm;

import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.connection.IConnection;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.robot.IAssemblyRobotNotification;
import at.ac.tuwien.robot.ICalibrationRobotNotification;
import at.ac.tuwien.robot.ILogisticRobotNotification;

public class XVSMConnection implements IConnection {

    @Override
    public void establish() throws ConnectionException {

    }

    @Override
    public void supply(Part part) throws ConnectionException {

    }

    @Override
    public void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws ConnectionException {

    }

    @Override
    public void moduleAssembled(Module module) throws ConnectionException {

    }

    @Override
    public void droneAssembled(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws ConnectionException {

    }

    @Override
    public void motorRotorPairCalibrated(Module module) throws ConnectionException {

    }

    @Override
    public void droneCalibrated(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws ConnectionException {

    }

    @Override
    public void droneTested(Drone drone) throws ConnectionException {

    }
}
