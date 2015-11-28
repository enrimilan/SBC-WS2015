package at.ac.tuwien.server;

import at.ac.tuwien.connection.IAssemblyRobotNotification;
import at.ac.tuwien.entity.*;
import at.ac.tuwien.robot.AssemblyRobot;
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
    private CopyOnWriteArrayList<Part> cases, controlUnits, motors, rotors;
    private CopyOnWriteArrayList<Module> caseControlUnitPairs, motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones;
    private CopyOnWriteArrayList<IAssemblyRobotNotification> assemblyRobots;

    public Server() throws RemoteException, AlreadyBoundException {
        super();
        this.cases = new CopyOnWriteArrayList<Part>();
        this.controlUnits = new CopyOnWriteArrayList<Part>();
        this.motors = new CopyOnWriteArrayList<Part>();
        this.rotors = new CopyOnWriteArrayList<Part>();
        this.caseControlUnitPairs = new CopyOnWriteArrayList<Module>();
        this.motorRotorPairs = new CopyOnWriteArrayList<Module>();
        this.drones = new CopyOnWriteArrayList<Drone>();
        this.assemblyRobots = new CopyOnWriteArrayList<IAssemblyRobotNotification>();
        registry = LocateRegistry.createRegistry(PORT);
        registry.bind(NAME, this);
    }

    @Override
    public void supply(Part part) throws RemoteException {
        if(part.getPartType() == PartType.CASE){
            cases.add(part);
        }
        else if(part.getPartType() == PartType.CONTROL_UNIT){
            controlUnits.add(part);
        }
        else if(part.getPartType() == PartType.MOTOR){
            motors.add(part);
        }
        else if(part.getPartType() == PartType.ROTOR){
            rotors.add(part);
        }
        logger.info("Notify GUI: " + part + " has been supplied.");
    }

    @Override
    public void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws RemoteException {
        assemblyRobots.add(assemblyRobotNotification);
    }

    @Override
    public void moduleAssembled(Module module) throws RemoteException {
        if(module.getModuleType() == ModuleType.CASE_CONTROL_UNIT_PAIR){
            caseControlUnitPairs.add(module);
        }
        else if(module.getModuleType() == ModuleType.MOTOR_ROTOR_PAIR){
            motorRotorPairs.add(module);
        }
        logger.info("Notify GUI: " + module + " has been supplied.");
    }

    @Override
    public void droneAssembled(Drone drone) throws RemoteException {
        drones.add(drone);
        logger.info("Notify GUI: " + drone + " has been supplied.");
    }
}

