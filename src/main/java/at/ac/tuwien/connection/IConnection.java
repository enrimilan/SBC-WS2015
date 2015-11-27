package at.ac.tuwien.connection;

import at.ac.tuwien.entity.Part;

public interface IConnection {

    void connect() throws ConnectionException;
    void supply(Part part) throws ConnectionException;
}
