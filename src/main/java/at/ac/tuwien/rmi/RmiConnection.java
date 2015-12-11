package at.ac.tuwien.rmi;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.robot.IAssemblyRobotNotification;
import at.ac.tuwien.common.robot.ICalibrationRobotNotification;
import at.ac.tuwien.common.robot.ILogisticRobotNotification;
import at.ac.tuwien.utils.Constants;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiConnection implements IConnection {

    private IServer server;
    private Registry registry;

    @Override
    public void establish() throws ConnectionException {
        try {
            registry = LocateRegistry.getRegistry(Constants.SERVER_HOST, Constants.SERVER_PORT);
            server = (IServer) registry.lookup(Constants.SERVER_HOST);
        } catch (RemoteException e) {
           throw new ConnectionException(e.getMessage());
        } catch (NotBoundException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void supply(Part part) throws ConnectionException {
        try {
            server.supply(part);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws ConnectionException {
        try {
            server.registerAssemblyRobot(assemblyRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void moduleAssembled(Module module) throws ConnectionException {
        try {
            server.moduleAssembled(module);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneAssembled(Drone drone) throws ConnectionException {
        try {
            server.droneAssembled(drone);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws ConnectionException {
        try {
            server.registerCalibrationRobot(calibrationRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void motorRotorPairCalibrated(Module module) throws ConnectionException {
        try {
            server.motorRotorPairCalibrated(module);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneCalibrated(Drone drone) throws ConnectionException {
        try {
            server.droneCalibrated(drone);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws ConnectionException {
        try {
            server.registerLogisticRobot(logisticRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneTested(Drone drone) throws ConnectionException {
        try {
            server.droneTested(drone);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void end() throws ConnectionException {
        try {
            registry.unbind(Constants.SERVER_HOST);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        } catch (NotBoundException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}