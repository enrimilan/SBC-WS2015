package at.ac.tuwien.server;

import at.ac.tuwien.entity.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends UnicastRemoteObject implements IServer {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final static int PORT = 4444;
    private final static String NAME = "admin";
    private Registry registry;
    private CopyOnWriteArrayList<Part> parts;

    public Server() throws RemoteException, AlreadyBoundException {
        super();
        this.parts = new CopyOnWriteArrayList<Part>();
        registry = LocateRegistry.createRegistry(PORT);
        registry.bind(NAME, this);
    }

    @Override
    public void supply(Part part) throws RemoteException {
        parts.add(part);
        logger.info("Notify GUI: " + part + " has been supplied.");
    }
}

