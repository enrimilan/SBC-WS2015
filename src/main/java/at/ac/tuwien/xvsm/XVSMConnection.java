package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.robot.IAssemblyRobotNotification;
import at.ac.tuwien.common.robot.ICalibrationRobotNotification;
import at.ac.tuwien.common.robot.ILogisticRobotNotification;

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
