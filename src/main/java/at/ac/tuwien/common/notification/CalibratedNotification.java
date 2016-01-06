package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CalibratedNotification extends UnicastRemoteObject implements ICalibratedNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(CalibratedNotification.class);
    private final static int INTERVAL = 1000;
    private final static int MIN_CALIBRATION_VALUE = -10;
    private final static int MAX_CALIBRATION_VALUE = 10;
    private UUID calibratorRobotId;
    private String host;
    private int port;

    public CalibratedNotification(UUID calibratorRobotId, String host, int port) throws RemoteException {
        super();
        this.calibratorRobotId = calibratorRobotId;
        this.host = host;
        this.port = port;
    }

    @Override
    public void calibrateMotorRotorPair(Module module, Job job) {
        logger.debug("calibrating motor-rotor pair");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    int value = ThreadLocalRandom.current().nextInt(MIN_CALIBRATION_VALUE, MAX_CALIBRATION_VALUE + 1);
                    module.setCalibrationValue(value);
                    module.setCalibratorId(calibratorRobotId);
                    module.setStatus(Status.CALIBRATED);
                    Thread.sleep(INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.setHost(host);
                    connection.setPort(port);
                    job.setStatus(JobStatus.DONE);
                    connection.motorRotorPairCalibrated(module, job);
                    connection.registerCalibrationRobot(CalibratedNotification.this);
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
    public void calibrateModuleInDrone(Drone drone, Job job) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    IConnection connection = Utils.getConnectionInstance();
                    connection.setHost(host);
                    connection.setPort(port);
                    ArrayList<Module> motorRotorPairs = drone.getMotorRotorPairs();
                    int value = 0;
                    for(Module motorRotorPair: motorRotorPairs){
                        if(motorRotorPair.getStatus() == Status.CALIBRATED){
                            value = value + motorRotorPair.getCalibrationValue();
                        }
                        else{
                            logger.debug("calibrating motor-rotor pair in drone");
                            drone.setStatus(Status.CALIBRATING);
                            value = ThreadLocalRandom.current().nextInt(MIN_CALIBRATION_VALUE, MAX_CALIBRATION_VALUE + 1);
                            motorRotorPair.setCalibrationValue(value);
                            motorRotorPair.setCalibratorId(calibratorRobotId);
                            motorRotorPair.setStatus(Status.CALIBRATED);
                            Thread.sleep(INTERVAL);
                            job.setStatus(JobStatus.DONE);
                            connection.droneCalibrated(drone, job);
                            connection.registerCalibrationRobot(CalibratedNotification.this);
                            return;
                        }
                    }
                    // if we got here, all of the 3 motor rotor pairs were already calibrated by some robots
                    logger.debug("calibrating drone");
                    Module caseControlUnitPair = drone.getCaseControlUnitPair();
                    caseControlUnitPair.setCalibrationValue(value);
                    caseControlUnitPair.setCalibratorId(calibratorRobotId);
                    caseControlUnitPair.setStatus(Status.CALIBRATED);
                    drone.setStatus(Status.CALIBRATED);
                    Thread.sleep(INTERVAL);
                    job.setStatus(JobStatus.DONE);
                    connection.droneCalibrated(drone, job);
                    connection.registerCalibrationRobot(CalibratedNotification.this);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
