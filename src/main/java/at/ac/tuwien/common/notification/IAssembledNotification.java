package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * An interface to notify 'Assembler' robot for the various available work it can conduct
 */
public interface IAssembledNotification extends Remote {

    /**
     * Assembles given parts (i.e. list of motors and rotors), into  motor-rotor modules
     * @param motors list of parts of type motor, each to be assembled to the other module-part (i.e. rotor)
     * @param rotors list of parts of type rotor, each to be assembled to the other module-part (i.e. motor)
     * @param job keeps the transaction informed about the progress of assembling conducted by this method
     * @throws RemoteException
     */
    void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors, Job job) throws RemoteException;

    /**
     * Assembles two given parts (i.e case and control unit) into a case-controlUnit module
     * @param casePart part of type 'Case' to be assembled to the other part (i.e. control unit)
     * @param controlUnit part of type 'Control Unit' to be assembled to the other part (i.e. case)
     * @param job keeps the transaction informed about the progress of assembling conducted by this method
     * @param orderId the ID of the order for which the module will be further used
     * @throws RemoteException
     */
    void assembleCaseControlUnitPair(Part casePart, Part controlUnit, Job job, UUID orderId) throws RemoteException;

    /**
     * Assemble given modules (i.e. one case-controlUnit pair and three motor-rotor pairs) into a drone
     * @param caseControlUnitPair a module of type case-controlUnit pair
     * @param motorRotorPairs list containing three modules of type motor-rotor pair
     * @param job keeps the transaction informed about the progress of assembling conducted by this method
     * @param orderId  the ID of the order for which the drone will be assembled
     * @throws RemoteException
     */
    void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs, Job job, UUID orderId) throws RemoteException;
}
