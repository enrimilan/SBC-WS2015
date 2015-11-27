package at.ac.tuwien.connection;

import at.ac.tuwien.entity.Part;
import at.ac.tuwien.server.IServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Connection implements IConnection {

    private final static int PORT = 4444;
    private final static String NAME = "admin";
    private final static String HOST = "127.0.0.1";
    private IServer server;
    private Registry registry;


    @Override
    public void establish() throws ConnectionException {
        try {
            registry = LocateRegistry.getRegistry(HOST,PORT);
            server = (IServer) registry.lookup(NAME);
        } catch (RemoteException e) {
           throw new ConnectionException(e.getMessage());
        } catch (NotBoundException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void supply(Part part) throws ConnectionException {
        try {
            server.supply(part);
        } catch (RemoteException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
