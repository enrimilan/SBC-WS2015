package at.ac.tuwien.common.robot.notification;

import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IAssembledNotification extends Remote {

    void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors) throws RemoteException;
    void assembleCaseControlUnitPair(Part casePart, Part controlUnit) throws RemoteException;
    void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs) throws RemoteException;
}
