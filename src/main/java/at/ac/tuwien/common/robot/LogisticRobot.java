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
    private String host;
    private int port;

    public LogisticRobot(IConnection connection, int minCalibrationValue, int maxCalibrationValue, String host,
                         int port) throws ConnectionException, RemoteException {
        super(connection);
        this.minCalibrationValue = minCalibrationValue;
        this.maxCalibrationValue = maxCalibrationValue;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connection.establish(host, port);
            connection.registerLogisticRobot(new TestedNotification(id, minCalibrationValue, maxCalibrationValue, host, port));
            startRobot();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException("Usage: LogisticRobot MIN_CALIBRATION_VALUE MAX_CALIBRATION_VALUE rmi|xvsm");
        }
        int minCalibrationValue = Integer.valueOf(args[0]);
        int maxCalibrationValue = Integer.valueOf(args[1]);
        IConnection connection = Utils.getConnectionInstance(args[2]);
        try {
            LogisticRobot logisticRobot = new LogisticRobot(connection ,minCalibrationValue, maxCalibrationValue,
                    args[3], Integer.valueOf(args[4]));
            logisticRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }
}
