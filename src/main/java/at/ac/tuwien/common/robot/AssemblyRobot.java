package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.notification.AssembledNotification;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.UUID;

public class AssemblyRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(AssemblyRobot.class);
    private String host;
    private int port;

    public AssemblyRobot(IConnection connection, String host, int port) throws ConnectionException {
        super(connection);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connection.establish(host, port);
            connection.registerAssemblyRobot(new AssembledNotification(UUID.randomUUID(), host, port));
            startRobot();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length != 3){
            throw new IllegalArgumentException("Usage: AssemblyRobot rmi|xvsm SERVER_HOST SERVER_PORT");
        }
        try {
            IConnection connection = Utils.getConnectionInstance(args[0]);
            connection.setHost(args[1]);
            connection.setPort(Integer.valueOf(args[2]));
            AssemblyRobot assemblyRobot = new AssemblyRobot(connection, args[1], Integer.valueOf(args[2]));
            assemblyRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        }
    }
}
