package at.ac.tuwien.server;

import at.ac.tuwien.entity.*;
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
    private CopyOnWriteArrayList<CaseControlUnitPair> caseControlUnitPairs;
    private CopyOnWriteArrayList<MotorRotorPair> motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones;

    public Server() throws RemoteException, AlreadyBoundException {
        super();
        this.cases = new CopyOnWriteArrayList<Part>();
        this.controlUnits = new CopyOnWriteArrayList<Part>();
        this.motors = new CopyOnWriteArrayList<Part>();
        this.rotors = new CopyOnWriteArrayList<Part>();
        this.caseControlUnitPairs = new CopyOnWriteArrayList<CaseControlUnitPair>();
        this.motorRotorPairs = new CopyOnWriteArrayList<MotorRotorPair>();
        this.drones = new CopyOnWriteArrayList<Drone>();
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
}

