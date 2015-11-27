package at.ac.tuwien.server;

import at.ac.tuwien.entity.Part;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements IServer {

    private final static int PORT = 4444;
    private final static String NAME = "admin";
    private Registry registry;

    public Server() throws RemoteException, AlreadyBoundException {
        super();
        registry = LocateRegistry.createRegistry(PORT);
        registry.bind(NAME, this);
    }

    @Override
    public void supply(Part part) throws RemoteException {

    }
}

