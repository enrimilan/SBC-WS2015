package at.ac.tuwien.server;

import at.ac.tuwien.robot.IAssemblyRobotNotification;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

    void supply(Part part) throws RemoteException;
    void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws RemoteException;
    void moduleAssembled(Module module) throws RemoteException;
    void droneAssembled(Drone drone) throws RemoteException;
}
