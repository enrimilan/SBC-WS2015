package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.notification.CalibratedNotification;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class CalibrationRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(CalibrationRobot.class);
    private String host;
    private int port;

    public CalibrationRobot(IConnection connection, String host, int port) throws RemoteException, ConnectionException {
        super(connection);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connection.establish(host, port);
            connection.registerCalibrationRobot(new CalibratedNotification(id));
            startRobot();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }

    public static void main(String[] args){
        if(args.length != 3){
            throw new IllegalArgumentException("Usage: CalibrationRobot rmi|xvsm SERVER_HOST SERVER_PORT");
        }

        IConnection connection = Utils.getConnectionInstance(args[0]);
        try {
            CalibrationRobot calibrationRobot = new CalibrationRobot(connection, args[1], Integer.valueOf(args[2]));
            calibrationRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }
}
