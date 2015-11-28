package at.ac.tuwien.robot;

import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IAssemblyRobotNotification extends Remote {

    void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors) throws RemoteException;
    void assembleCaseControlUnitPair(Part casePart, Part controlUnit) throws RemoteException;
    void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs) throws RemoteException;
}
