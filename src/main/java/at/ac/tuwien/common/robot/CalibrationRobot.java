package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.robot.notification.CalibratedNotification;
import at.ac.tuwien.common.robot.notification.ICalibratedNotification;
import at.ac.tuwien.rmi.RmiConnection;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Status;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CalibrationRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(CalibrationRobot.class);

    public CalibrationRobot(IConnection connection) throws RemoteException, ConnectionException {
        super(connection);
    }

    @Override
    public void run() {
        try {
            connection.registerCalibrationRobot(new CalibratedNotification(id));
            while (System.in.read() != -1);
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length != 1){
            throw new IllegalArgumentException("Usage: CalibrationRobot rmi|xvsm");
        }

        IConnection connection = Utils.getConnectionInstance(args[0]);
        try {
            CalibrationRobot calibrationRobot = new CalibrationRobot(connection);
            calibrationRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
