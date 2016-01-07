package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.JobStatus;
import at.ac.tuwien.common.entity.Status;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class TestedNotification extends UnicastRemoteObject implements ITestedNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(TestedNotification.class);
    private final static int INTERVAL = 1000;
    private UUID calibratorRobotId;
    private int minCalibrationValue;
    private int maxCalibrationValue;
    private String host;
    private int port;

    public TestedNotification(UUID calibratorRobotId, int minCalibrationValue, int maxCalibrationValue,
                              String host, int port) throws RemoteException {
        super();
        this.calibratorRobotId = calibratorRobotId;
        this.minCalibrationValue = minCalibrationValue;
        this.maxCalibrationValue = maxCalibrationValue;
        this.host = host;
        this.port = port;
    }

    @Override
    public void testDrone(Drone drone, Job job, UUID orderId) {
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
                    if(orderId != null){
                        drone.setOrderId(orderId);
                    }
                    Thread.sleep(INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.setHost(host);
                    connection.setPort(port);
                    job.setStatus(JobStatus.DONE);
                    connection.droneTested(drone, job);
                    connection.registerLogisticRobot(TestedNotification.this);
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
