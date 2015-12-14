package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.notification.TestedNotification;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.RemoteException;

public class LogisticRobot extends AbstractRobot implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(LogisticRobot.class);
    private int minCalibrationValue;
    private int maxCalibrationValue;

    public LogisticRobot(IConnection connection, int minCalibrationValue, int maxCalibrationValue) throws
            ConnectionException, RemoteException {
        super(connection);
        this.minCalibrationValue = minCalibrationValue;
        this.maxCalibrationValue = maxCalibrationValue;
    }

    @Override
    public void run() {
        try {
            connection.registerLogisticRobot(new TestedNotification(id, minCalibrationValue, maxCalibrationValue));
            startRobot();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: LogisticRobot MIN_CALIBRATION_VALUE MAX_CALIBRATION_VALUE rmi|xvsm");
        }
        int minCalibrationValue = Integer.valueOf(args[0]);
        int maxCalibrationValue = Integer.valueOf(args[1]);
        IConnection connection = Utils.getConnectionInstance(args[2]);
        try {
            LogisticRobot logisticRobot = new LogisticRobot(connection ,minCalibrationValue, maxCalibrationValue);
            logisticRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
