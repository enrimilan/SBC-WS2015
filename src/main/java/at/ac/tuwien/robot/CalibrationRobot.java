package at.ac.tuwien.robot;

import at.ac.tuwien.connection.Connection;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class CalibrationRobot extends UnicastRemoteObject implements Runnable, ICalibrationRobotNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(CalibrationRobot.class);
    private final static int INTERVAL = 1000;
    private Connection connection;
    private UUID id;
    private CalibrationRobot calibrationRobot;

    public CalibrationRobot() throws RemoteException, ConnectionException {
        super();
        this.connection = new Connection();
        connection.establish();
        this.id = UUID.randomUUID();
        this.calibrationRobot = this;
    }

    @Override
    public void calibrateMotorRotorPair(Module module) throws RemoteException {

    }

    @Override
    public void calibrateModuleInDrone(Drone drone) throws RemoteException {

    }

    @Override
    public void run() {
        try {
            connection.registerCalibrationRobot(this);
            while (System.in.read() != -1);
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length != 0){
            throw new IllegalArgumentException("Usage: CalibrationRobot");
        }

        try {
            CalibrationRobot calibrationRobot = new CalibrationRobot();
            calibrationRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
