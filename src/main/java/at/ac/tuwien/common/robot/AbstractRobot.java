package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;

public abstract class AbstractRobot {

    protected IConnection connection;

    public AbstractRobot(IConnection connection) throws ConnectionException {
        this.connection = connection;
        connection.establish();
    }
}
