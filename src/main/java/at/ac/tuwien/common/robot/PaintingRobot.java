package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.notification.PaintedNotification;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class PaintingRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(CalibrationRobot.class);
    private String host;
    private int port;

    public PaintingRobot(IConnection connection, String host, int port) throws RemoteException, ConnectionException {
        super(connection);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connection.establish(host, port);
            connection.registerPaintingRobot(new PaintedNotification(id, host, port));
            startRobot();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }

    public static void main(String[] args){
        if(args.length != 3){
            throw new IllegalArgumentException("Usage: PaintingRobot rmi|xvsm SERVER_HOST SERVER_PORT");
        }

        IConnection connection = Utils.getConnectionInstance(args[0]);
        try {
            PaintingRobot paintingRobot = new PaintingRobot(connection, args[1], Integer.valueOf(args[2]));
            paintingRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }
    }
}
