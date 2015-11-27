package at.ac.tuwien.server;

import at.ac.tuwien.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

    void supply(Part part) throws RemoteException;

}
