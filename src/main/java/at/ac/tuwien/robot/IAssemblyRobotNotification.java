package at.ac.tuwien.robot;

import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;

import java.util.ArrayList;

public interface IAssemblyRobotNotification {

    void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors);
    void assembleCaseControlUnitPair(Part casePart, Part controlUnit);
    void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs);
}
