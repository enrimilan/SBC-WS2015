package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;

import java.util.UUID;

public abstract class AbstractRobot {

    protected IConnection connection;
    protected UUID id;

    public AbstractRobot(IConnection connection) throws ConnectionException {
        this.connection = connection;
        this.id = UUID.randomUUID();
        connection.establish();
    }
}
