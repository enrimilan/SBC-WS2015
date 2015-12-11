package at.ac.tuwien.rmi;

import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.robot.notification.IAssembledNotification;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.robot.notification.ICalibratedNotification;
import at.ac.tuwien.common.robot.notification.ITestedNotification;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.utils.Constants;
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

public class RMIServer extends UnicastRemoteObject implements IRMIServer, IServer {

    private final static Logger logger = LoggerFactory.getLogger(RMIServer.class);
    private Registry registry;
    private CopyOnWriteArrayList<Part> cases, controlUnits, motors, rotors;
    private CopyOnWriteArrayList<Module> caseControlUnitPairs, motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones, goodDrones, badDrones;
    private Queue<IAssembledNotification> assemblyRobots;
    private Queue<ICalibratedNotification> calibrationRobots;
    private Queue<ITestedNotification> logisticRobots;

    private INotificationCallback notificationCallback;

    @Override
    public void registerGUINotificationCallback(INotificationCallback notificationCallback){
        this.notificationCallback = notificationCallback;
    }

    public RMIServer() throws RemoteException, AlreadyBoundException {
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
        this.assemblyRobots = new ConcurrentLinkedQueue<IAssembledNotification>();
        this.calibrationRobots = new ConcurrentLinkedQueue<ICalibratedNotification>();
        this.logisticRobots = new ConcurrentLinkedQueue<ITestedNotification>();
        registry = LocateRegistry.createRegistry(Constants.SERVER_PORT);
        registry.bind(Constants.SERVER_NAME, this);
        new Thread(new RequestHandler()).start();
    }

    @Override
    public void start(){

    }

    @Override
    public void stop() {

    }

    @Override
    public synchronized void supply(Part part) throws RemoteException {
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
        notificationCallback.supplyNotifier(cases, controlUnits, motors, rotors);
//        logger.debug("Notify GUI: " + part + " has been supplied.");
    }

    @Override
    public synchronized void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws RemoteException {
        assemblyRobots.add(assemblyRobotNotification);
        logger.debug("assembly robot is ready to do some work.");
    }

    @Override
    public synchronized void moduleAssembled(Module module) throws RemoteException {
        if(module.getModuleType() == ModuleType.CASE_CONTROL_UNIT_PAIR){
            caseControlUnitPairs.add(module);
        }
        else if(module.getModuleType() == ModuleType.MOTOR_ROTOR_PAIR){
            motorRotorPairs.add(module);
        }
        notificationCallback.modulesNotifier(motorRotorPairs, caseControlUnitPairs);
//        logger.debug("Notify GUI: " + module + " has been assembled.");
    }

    @Override
    public synchronized void droneAssembled(Drone drone) throws RemoteException {
        drones.add(drone);
        notificationCallback.droneNotifier(drones);
//        logger.debug("Notify GUI: " + drone + " has been assembled.");
    }

    @Override
    public synchronized void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws RemoteException {
        calibrationRobots.add(calibrationRobotNotification);
        logger.debug("calibration robot is ready to do some work.");
    }

    @Override
    public synchronized void motorRotorPairCalibrated(Module module) throws RemoteException {
        motorRotorPairs.add(0, module);
        notificationCallback.modulesNotifier(motorRotorPairs, caseControlUnitPairs);
//        logger.debug("Notify GUI: " + module + " has been calibrated.");
    }

    @Override
    public synchronized void droneCalibrated(Drone drone) throws RemoteException {
        if(drone.getStatus()==Status.CALIBRATED){
            drones.add(0, drone);
        }
        else{
            drones.add(drone);
        }
//        logger.debug("Notify GUI: " + drone + " has been calibrated.");
        notificationCallback.droneNotifier(drones);
    }

    @Override
    public synchronized void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws RemoteException {
        logisticRobots.add(logisticRobotNotification);
        logger.debug("logistic robot is ready to do some work.");
    }

    @Override
    public synchronized void droneTested(Drone drone) throws RemoteException {
        if(drone.getStatus() == Status.TESTED_GOOD){
            goodDrones.add(drone);
            notificationCallback.testGoodDroneNotifier(goodDrones);
        }
        else{
            badDrones.add(drone);
            notificationCallback.testBadDroneNotifier(badDrones);
        }

//        logger.debug("Notify GUI: " + drone + " has been tested.");
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
                        IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
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
                        IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
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
                        IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
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
                        IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
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

                if(calibrationRobots.size()>0 && drones.size()>0 && drones.get(drones.size()-1).getStatus() == Status.ASSEMBLED){
                        ICalibratedNotification calibrationRobotNotification = calibrationRobots.poll();
                        Drone drone = drones.remove(drones.size()-1);
                        try {
                            calibrationRobotNotification.calibrateModuleInDrone(drone);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                }

                if(calibrationRobots.size()>0 && motorRotorPairs.size()>0 && motorRotorPairs.get(motorRotorPairs.size()-1).getStatus() == Status.ASSEMBLED){
                    ICalibratedNotification calibrationRobotNotification = calibrationRobots.poll();
                    Module motorRotorPair = motorRotorPairs.remove(motorRotorPairs.size()-1);
                    try {
                        calibrationRobotNotification.calibrateMotorRotorPair(motorRotorPair);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                if(logisticRobots.size()>0 && drones.size()>0 && drones.get(0).getStatus() == Status.CALIBRATED){
                    ITestedNotification logisticRobotNotification = logisticRobots.poll();
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




