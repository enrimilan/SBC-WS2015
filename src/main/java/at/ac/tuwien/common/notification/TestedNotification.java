package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Status;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

public class TestedNotification implements ITestedNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(TestedNotification.class);
    private final static int INTERVAL = 1000;
    private UUID calibratorRobotId;
    private int minCalibrationValue;
    private int maxCalibrationValue;

    public TestedNotification(UUID calibratorRobotId, int minCalibrationValue, int maxCalibrationValue) {
        this.calibratorRobotId = calibratorRobotId;
        this.minCalibrationValue = minCalibrationValue;
        this.maxCalibrationValue = maxCalibrationValue;
    }

    @Override
    public void testDrone(Drone drone) throws RemoteException {
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
                    IConnection connection = Utils.getConnectionInstance();
                    connection.droneTested(drone);
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