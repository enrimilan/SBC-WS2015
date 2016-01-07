package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface IAssembledNotification extends Remote {

    void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors, Job job) throws RemoteException;
    void assembleCaseControlUnitPair(Part casePart, Part controlUnit, Job job, UUID orderId) throws RemoteException;
    void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs, Job job) throws RemoteException;
}
