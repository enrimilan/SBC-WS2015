package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.robot.notification.AssembledNotification;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class AssemblyRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(AssemblyRobot.class);

    public AssemblyRobot(IConnection connection) throws ConnectionException {
        super(connection);
    }

    @Override
    public void run() {
        try {
            connection.registerAssemblyRobot(new AssembledNotification(UUID.randomUUID()));
            while (System.in.read() != -1);
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length != 1){
            throw new IllegalArgumentException("Usage: AssemblyRobot rmi|xvsm");
        }
        try {
            IConnection connection = Utils.getConnectionInstance(args[0]);
            AssemblyRobot assemblyRobot = new AssemblyRobot(connection);
            assemblyRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        }
    }
}
