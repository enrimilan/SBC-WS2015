package at.ac.tuwien.rmi;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.*;
import at.ac.tuwien.utils.Constants;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiConnection implements IConnection {

    private IRMIServer server;
    private Registry registry;
    private String host;
    private int port;

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void establish(String host, int port) throws ConnectionException {
        try {
            registry = LocateRegistry.getRegistry(host, port);
            server = (IRMIServer) registry.lookup(Constants.SERVER_NAME);
        } catch (RemoteException e) {
           throw new ConnectionException(e.getMessage());
        } catch (NotBoundException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public int getAmount(Part part) throws ConnectionException {
        try {
            return server.getAmount(part);
        } catch (RemoteException e) {
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
    public void registerPaintingRobot(IPaintedNotification paintedNotification) throws ConnectionException {
        try {
            server.registerPaintingRobot(paintedNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void partPainted(Part part, Job job) throws ConnectionException {
        try {
            server.partPainted(part, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException {
        try {
            server.registerAssemblyRobot(assemblyRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void moduleAssembled(Module module, Job job) throws ConnectionException {
        try {
            server.moduleAssembled(module, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneAssembled(Drone drone, Job job) throws ConnectionException {
        try {
            server.droneAssembled(drone, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException {
        try {
            server.registerCalibrationRobot(calibrationRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void motorRotorPairCalibrated(Module module, Job job) throws ConnectionException {
        try {
            server.motorRotorPairCalibrated(module, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneCalibrated(Drone drone, Job job) throws ConnectionException {
        try {
            server.droneCalibrated(drone, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException {
        try {
            server.registerLogisticRobot(logisticRobotNotification);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneTested(Drone drone, Job job) throws ConnectionException {
        try {
            server.droneTested(drone, job);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void end() throws ConnectionException {

    }
}
