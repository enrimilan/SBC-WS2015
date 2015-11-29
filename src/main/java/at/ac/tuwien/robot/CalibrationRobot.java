package at.ac.tuwien.robot;

import at.ac.tuwien.connection.Connection;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CalibrationRobot extends UnicastRemoteObject implements Runnable, ICalibrationRobotNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(CalibrationRobot.class);
    private final static int INTERVAL = 1000;
    private final static int MIN_CALIBRATION_VALUE = -10;
    private final static int MAX_CALIBRATION_VALUE = 10;
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
    public void calibrateMotorRotorPair(final Module module) throws RemoteException {
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    int value = ThreadLocalRandom.current().nextInt(MIN_CALIBRATION_VALUE, MAX_CALIBRATION_VALUE + 1);
                    module.setCalibrationValue(value);
                    module.setCalibratorId(id);
                    module.setStatus(Status.CALIBRATED);
                    Thread.sleep(INTERVAL);
                    connection.motorRotorPairCalibrated(module);
                    connection.registerCalibrationRobot(calibrationRobot);
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
    public void calibrateModuleInDrone(final Drone drone) throws RemoteException {
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    ArrayList<Module> motorRotorPairs = drone.getMotorRotorPairs();
                    int value = 0;
                    for(Module motorRotorPair: motorRotorPairs){
                        if(motorRotorPair.getStatus() == Status.CALIBRATED){
                            value = value + motorRotorPair.getCalibrationValue();
                        }
                        else{
                            value = ThreadLocalRandom.current().nextInt(MIN_CALIBRATION_VALUE, MAX_CALIBRATION_VALUE + 1);
                            motorRotorPair.setCalibrationValue(value);
                            motorRotorPair.setCalibratorId(id);
                            motorRotorPair.setStatus(Status.CALIBRATED);
                            Thread.sleep(INTERVAL);
                            connection.droneCalibrated(drone);
                            connection.registerCalibrationRobot(calibrationRobot);
                            return;
                        }
                    }
                    // if we got here, all of the 3 motor rotor pairs were already calibrated by some robots
                    Module caseControlUnitPair = drone.getCaseControlUnitPair();
                    caseControlUnitPair.setCalibrationValue(value);
                    caseControlUnitPair.setCalibratorId(id);
                    caseControlUnitPair.setStatus(Status.CALIBRATED);
                    drone.setStatus(Status.CALIBRATED);
                    Thread.sleep(INTERVAL);
                    connection.droneCalibrated(drone);
                    connection.registerCalibrationRobot(calibrationRobot);
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
