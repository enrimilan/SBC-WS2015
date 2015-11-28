package at.ac.tuwien.connection;

import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;

import java.util.ArrayList;

public interface IAssemblyRobotNotification {

    void mountMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors);
    void mountCaseControlUnitPair(Part casePart, Part controlUnit);
    void mountDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs);
}
