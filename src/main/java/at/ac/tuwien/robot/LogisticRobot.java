package at.ac.tuwien.robot;

import at.ac.tuwien.connection.Connection;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class LogisticRobot extends UnicastRemoteObject implements Runnable, ILogisticRobotNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(LogisticRobot.class);
    private final static int INTERVAL = 1000;
    private Connection connection;
    private UUID id;
    private LogisticRobot logisticRobot;
    private int minCalibrationValue;
    private int maxCalibrationValue;

    public LogisticRobot(int minCalibrationValue, int maxCalibrationValue) throws ConnectionException, RemoteException {
        super();
        this.minCalibrationValue = minCalibrationValue;
        this.maxCalibrationValue = maxCalibrationValue;
        this.connection = new Connection();
        connection.establish();
        this.id = UUID.randomUUID();
        this.logisticRobot = this;
    }

    @Override
    public void testDrone(final Drone drone) throws RemoteException {
        logger.debug("testing drone.");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    int value = drone.getCaseControlUnitPair().getCalibrationValue();
                    if(value<=maxCalibrationValue && value>=minCalibrationValue){
                        drone.setStatus(Status.TESTED_GOOD);
                    }
                    else{
                        drone.setStatus(Status.TESTED_BAD);
                    }
                    Thread.sleep(INTERVAL);
                    connection.droneTested(drone);
                    connection.registerLogisticRobot(logisticRobot);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void run() {
        try {
            connection.registerLogisticRobot(this);
            while (System.in.read() != -1);
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: LogisticRobot MIN_CALIBRATION_VALUE MAX_CALIBRATION_VALUE");
        }
        int minCalibrationValue = Integer.valueOf(args[0]);
        int maxCalibrationValue = Integer.valueOf(args[1]);
        try {
            LogisticRobot logisticRobot = new LogisticRobot(minCalibrationValue, maxCalibrationValue);
            logisticRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
