package at.ac.tuwien.server;

import at.ac.tuwien.robot.IAssemblyRobotNotification;
import at.ac.tuwien.entity.*;
import at.ac.tuwien.robot.ICalibrationRobotNotification;
import at.ac.tuwien.robot.ILogisticRobotNotification;
import at.ac.tuwien.robot.LogisticRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends UnicastRemoteObject implements IServer {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final static int PORT = 4444;
    private final static String NAME = "admin";
    private Registry registry;
    private CopyOnWriteArrayList<Part> cases, controlUnits, motors, rotors;
    private CopyOnWriteArrayList<Module> caseControlUnitPairs, motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones, goodDrones, badDrones;
    private Queue<IAssemblyRobotNotification> assemblyRobots;
    private Queue<ICalibrationRobotNotification> calibrationRobots;
    private Queue<ILogisticRobotNotification> logisticRobots;

    public Server() throws RemoteException, AlreadyBoundException {
        super();
        this.cases = new CopyOnWriteArrayList<Part>();
        this.controlUnits = new CopyOnWriteArrayList<Part>();
        this.motors = new CopyOnWriteArrayList<Part>();
        this.rotors = new CopyOnWriteArrayList<Part>();
        this.caseControlUnitPairs = new CopyOnWriteArrayList<Module>();
        this.motorRotorPairs = new CopyOnWriteArrayList<Module>();
        this.drones = new CopyOnWriteArrayList<Drone>();
        this.goodDrones = new CopyOnWriteArrayList<Drone>();
        this.badDrones =  new CopyOnWriteArrayList<Drone>();
        this.assemblyRobots = new ConcurrentLinkedQueue<IAssemblyRobotNotification>();
        this.calibrationRobots = new ConcurrentLinkedQueue<ICalibrationRobotNotification>();
        this.logisticRobots = new ConcurrentLinkedQueue<ILogisticRobotNotification>();
        registry = LocateRegistry.createRegistry(PORT);
        registry.bind(NAME, this);
        new Thread(new RequestHandler()).start();
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
        logger.debug("Notify GUI: " + part + " has been supplied.");
    }

    @Override
    public synchronized void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws RemoteException {
        assemblyRobots.add(assemblyRobotNotification);
        logger.debug("assembly robot is ready to do some work.");
    }

    @Override
    public void moduleAssembled(Module module) throws RemoteException {
        if(module.getModuleType() == ModuleType.CASE_CONTROL_UNIT_PAIR){
            caseControlUnitPairs.add(module);
        }
        else if(module.getModuleType() == ModuleType.MOTOR_ROTOR_PAIR){
            motorRotorPairs.add(module);
        }
        logger.debug("Notify GUI: " + module + " has been assembled.");
    }

    @Override
    public void droneAssembled(Drone drone) throws RemoteException {
        drones.add(drone);
        logger.debug("Notify GUI: " + drone + " has been assembled.");
    }

    @Override
    public void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws RemoteException {
        calibrationRobots.add(calibrationRobotNotification);
        logger.debug("calibration robot is ready to do some work.");
    }

    @Override
    public void motorRotorPairCalibrated(Module module) throws RemoteException {
        motorRotorPairs.add(0, module);
        logger.debug("Notify GUI: " + module + " has been calibrated.");
    }

    @Override
    public void droneCalibrated(Drone drone) throws RemoteException {
        if(drone.getStatus()==Status.CALIBRATED){
            drones.add(0, drone);
        }
        else{
            drones.add(drone);
        }
    }

    @Override
    public void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws RemoteException {
        logisticRobots.add(logisticRobotNotification);
        logger.debug("logistic robot is ready to do some work.");
    }

    @Override
    public void droneTested(Drone drone) throws RemoteException {
        if(drone.getStatus() == Status.TESTED_GOOD){
            goodDrones.add(drone);
        }
        else{
            badDrones.add(drone);
        }

        logger.debug("Notify GUI: " + drone + " has been tested.");
    }

    private class RequestHandler implements Runnable{

        private boolean running = true;

        @Override
        public void run() {
            logger.debug("Request handler started.");
            while(running){

                if(assemblyRobots.size()>0){
                    //all necessary parts for a drone are available and were already assembled by the robots
                    if(caseControlUnitPairs.size()>=1 && motorRotorPairs.size()>=3){
                        IAssemblyRobotNotification assemblyRobotNotification = assemblyRobots.poll();
                        Module caseControlUnitPair = caseControlUnitPairs.remove(0);
                        ArrayList<Module> motorRotorPairModules = new ArrayList<Module>();
                        int i = 0;
                        while(i<3){
                            motorRotorPairModules.add(motorRotorPairs.remove(0));
                            i++;
                        }
                        try {
                            assemblyRobotNotification.assembleDrone(caseControlUnitPair, motorRotorPairModules);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    //we still need some motor-rotor modules
                    else if(motorRotorPairs.size()<3 && motors.size()>0 && rotors.size()>0){
                        IAssemblyRobotNotification assemblyRobotNotification = assemblyRobots.poll();
                        ArrayList<Part> motorParts = new ArrayList<Part>();
                        ArrayList<Part> rotorParts = new ArrayList<Part>();
                        int i = 0;
                        while(i<3-motorRotorPairs.size() && motors.size()>0 && rotors.size()>0){
                            motorParts.add(motors.remove(0));
                            rotorParts.add(rotors.remove(0));
                            i++;
                        }
                        try {
                            assemblyRobotNotification.assembleMotorRotorPairs(motorParts, rotorParts);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    //assemble a case-control unit module
                    else if(cases.size()>0 && controlUnits.size()>0){
                        IAssemblyRobotNotification assemblyRobotNotification = assemblyRobots.poll();
                        Part casePart = cases.remove(0);
                        Part controlUnit = controlUnits.remove(0);
                        try {
                            assemblyRobotNotification.assembleCaseControlUnitPair(casePart, controlUnit);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    //assemble a motor-rotor module
                    else if(motors.size()>0 && rotors.size()>0){
                        IAssemblyRobotNotification assemblyRobotNotification = assemblyRobots.poll();
                        ArrayList<Part> motorParts = new ArrayList<Part>();
                        ArrayList<Part> rotorParts = new ArrayList<Part>();
                        motorParts.add(motors.remove(0));
                        rotorParts.add(rotors.remove(0));
                        try {
                            assemblyRobotNotification.assembleMotorRotorPairs(motorParts, rotorParts);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(assemblyRobots.size()>0 && drones.size()>0 && drones.get(drones.size()-1).getStatus() == Status.ASSEMBLED){
                        ICalibrationRobotNotification calibrationRobotNotification = calibrationRobots.poll();
                        Drone drone = drones.remove(drones.size()-1);
                        try {
                            calibrationRobotNotification.calibrateModuleInDrone(drone);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                }

                if(assemblyRobots.size()>0 && motorRotorPairs.size()>0 && motorRotorPairs.get(motorRotorPairs.size()-1).getStatus() == Status.ASSEMBLED){
                    ICalibrationRobotNotification calibrationRobotNotification = calibrationRobots.poll();
                    Module motorRotorPair = motorRotorPairs.remove(motorRotorPairs.size()-1);
                    try {
                        calibrationRobotNotification.calibrateMotorRotorPair(motorRotorPair);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                if(logisticRobots.size()>0 && drones.size()>0 && drones.get(0).getStatus() == Status.CALIBRATED){
                    ILogisticRobotNotification logisticRobotNotification = logisticRobots.poll();
                    try {
                        logisticRobotNotification.testDrone(drones.remove(0));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        public void stopRunning(){
            running = false;
        }
    }
}




