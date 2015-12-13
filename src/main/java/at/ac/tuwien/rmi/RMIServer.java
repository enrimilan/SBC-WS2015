package at.ac.tuwien.rmi;

import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RMIServer extends UnicastRemoteObject implements IRMIServer, IServer {

    private final static Logger logger = LoggerFactory.getLogger(RMIServer.class);
    private Registry registry;
    private CopyOnWriteArrayList<Part> cases, controlUnits, motors, rotors;
    private CopyOnWriteArrayList<Module> caseControlUnitPairs, motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones, goodDrones, badDrones;
    private Queue<IAssembledNotification> assemblyRobots;
    private Queue<ICalibratedNotification> calibrationRobots;
    private Queue<ITestedNotification> logisticRobots;
    private AtomicReference<Integer> jobId;
    private ConcurrentHashMap<Job,Transaction> jobs;
    private INotificationCallback notificationCallback;

    public RMIServer() throws RemoteException {
        super();
        this.cases = new CopyOnWriteArrayList<>();
        this.controlUnits = new CopyOnWriteArrayList<>();
        this.motors = new CopyOnWriteArrayList<>();
        this.rotors = new CopyOnWriteArrayList<>();
        this.caseControlUnitPairs = new CopyOnWriteArrayList<>();
        this.motorRotorPairs = new CopyOnWriteArrayList<>();
        this.drones = new CopyOnWriteArrayList<>();
        this.goodDrones = new CopyOnWriteArrayList<>();
        this.badDrones =  new CopyOnWriteArrayList<>();
        this.assemblyRobots = new ConcurrentLinkedQueue<>();
        this.calibrationRobots = new ConcurrentLinkedQueue<>();
        this.logisticRobots = new ConcurrentLinkedQueue<>();
        this.jobId = new AtomicReference<>();
        jobId.set(0);
        this.jobs = new ConcurrentHashMap<>();
    }

    @Override
    public void registerGUINotificationCallback(INotificationCallback notificationCallback){
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void start() {
        try {
            registry = LocateRegistry.createRegistry(Constants.SERVER_PORT);
            registry.bind(Constants.SERVER_NAME, this);
        } catch (RemoteException e) {
            logger.error(e.getMessage());
        } catch (AlreadyBoundException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            UnicastRemoteObject.unexportObject(this,true);
            registry.unbind(Constants.SERVER_NAME);
        } catch (RemoteException e) {
            logger.error(e.getMessage());
        } catch (NotBoundException e) {
            logger.error(e.getMessage());
        }
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
        notificationCallback.onPartAdded(part);
        checkForWorkWithPartsForAssemblyRobot();
    }

    @Override
    public synchronized void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws RemoteException {
        assemblyRobots.add(assemblyRobotNotification);
        logger.debug("assembly robot is ready to do some work.");
        checkForWorkWithModulesForAssemblyRobot();
        checkForWorkWithPartsForAssemblyRobot();
    }

    @Override
    public synchronized void moduleAssembled(Module module, Job job) throws RemoteException {
        Transaction t = jobs.get(job);
        if(job == null || job.getStatus() == JobStatus.WORKING){

        }
        else if(t!=null &&  t.getStatus() == TransactionStatus.RUNNING){
            t.commit();
        }
        else{
            return;
        }

        if(module.getModuleType() == ModuleType.CASE_CONTROL_UNIT_PAIR){
            caseControlUnitPairs.add(module);
        }
        else if(module.getModuleType() == ModuleType.MOTOR_ROTOR_PAIR){
            motorRotorPairs.add(module);
        }
        notificationCallback.onModuleAdded(module);
        checkForWorkWithModulesForAssemblyRobot();
        checkForWorkWithMotorRotorPairForCalibrationRobot();
    }

    @Override
    public synchronized void droneAssembled(Drone drone, Job job) throws RemoteException {
        Transaction t = jobs.get(job);
        if(job == null || job.getStatus() == JobStatus.WORKING){
            //null or still not finished
        }
        else if(t!=null &&  t.getStatus() == TransactionStatus.RUNNING){
            t.commit();
        }
        else{
            //the robot was too slow and the changes were already aborted
            return;
        }

        drones.add(drone);
        notificationCallback.onDroneAdded(drone);
        checkForWorkWithDroneForCalibrationRobot();
    }

    @Override
    public synchronized void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws RemoteException {
        calibrationRobots.add(calibrationRobotNotification);
        logger.debug("calibration robot is ready to do some work.");
        checkForWorkWithDroneForCalibrationRobot();
        checkForWorkWithMotorRotorPairForCalibrationRobot();
    }

    @Override
    public synchronized void motorRotorPairCalibrated(Module module, Job job) throws RemoteException {
        Transaction t = jobs.get(job);
        if(job == null || job.getStatus() == JobStatus.WORKING){
            //null or still not finished
        }
        else if(t!=null &&  t.getStatus() == TransactionStatus.RUNNING){
            t.commit();
        }
        else{
            //the robot was too slow and the changes were already aborted
            return;
        }

        motorRotorPairs.add(0, module);
        notificationCallback.onModuleAdded(module);
        checkForWorkWithModulesForAssemblyRobot();
    }

    @Override
    public synchronized void droneCalibrated(Drone drone, Job job) throws RemoteException {
        Transaction t = jobs.get(job);
        if(job == null || job.getStatus() == JobStatus.WORKING){
            //null or still not finished
        }
        else if(t!=null &&  t.getStatus() == TransactionStatus.RUNNING){
            t.commit();
        }
        else{
            //the robot was too slow and the changes were already aborted
            return;
        }

        if(drone.getStatus()==Status.CALIBRATED){
            drones.add(0, drone);
            notificationCallback.onDroneUpdated(drone);
            checkForWorkWithDroneForLogisticRobot();
        }
        else{
            drones.add(drone);
            notificationCallback.onDroneUpdated(drone);
            checkForWorkWithDroneForCalibrationRobot();
        }
    }

    @Override
    public synchronized void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws RemoteException {
        logisticRobots.add(logisticRobotNotification);
        logger.debug("logistic robot is ready to do some work.");
        checkForWorkWithDroneForLogisticRobot();
    }

    @Override
    public synchronized void droneTested(Drone drone, Job job) throws RemoteException {
        Transaction t = jobs.get(job);
        if(job == null || job.getStatus() == JobStatus.WORKING){
            //null or still not finished
        }
        else if(t!=null &&  t.getStatus() == TransactionStatus.RUNNING){
            t.commit();
        }
        else{
            //the robot was too slow and the changes were already aborted
            return;
        }

        if(drone.getStatus() == Status.TESTED_GOOD){
            notificationCallback.onDroneRemoved(drone);
            goodDrones.add(drone);
            notificationCallback.onGoodDroneTested(drone);
        }
        else{
            notificationCallback.onDroneRemoved(drone);
            badDrones.add(drone);
            notificationCallback.onBadDroneTested(drone);
        }
    }

    private synchronized boolean checkForWorkWithPartsForAssemblyRobot(){
        //we still need some motor-rotor modules
        int motorRotorPairsSize = motorRotorPairs.size();
        if(motorRotorPairsSize<3 && motors.size()>0 && rotors.size()>0){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            ArrayList<Part> motorParts = new ArrayList<Part>();
            ArrayList<Part> rotorParts = new ArrayList<Part>();
            int i = 0;
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            while(i<3-motorRotorPairsSize && motors.size()>0 && rotors.size()>0){
                Part m = motors.remove(0);
                Part r = rotors.remove(0);
                notificationCallback.onPartRemoved(m);
                notificationCallback.onPartRemoved(r);
                t.addPart(m);
                t.addPart(r);
                motorParts.add(m);
                rotorParts.add(r);
                i++;
            }
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                assemblyRobotNotification.assembleMotorRotorPairs(motorParts, rotorParts, job);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }

        //assemble a case-control unit module
        if(cases.size()>0 && controlUnits.size()>0){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            Part casePart = cases.remove(0);
            Part controlUnit = controlUnits.remove(0);
            notificationCallback.onPartRemoved(casePart);
            notificationCallback.onPartRemoved(controlUnit);
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addPart(casePart);
            t.addPart(controlUnit);
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                assemblyRobotNotification.assembleCaseControlUnitPair(casePart, controlUnit, job);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }

        //assemble a motor-rotor module
        if(motors.size()>0 && rotors.size()>0){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            ArrayList<Part> motorParts = new ArrayList<Part>();
            ArrayList<Part> rotorParts = new ArrayList<Part>();
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            Part m = motors.remove(0);
            Part r = rotors.remove(0);
            notificationCallback.onPartRemoved(m);
            notificationCallback.onPartRemoved(r);
            t.addPart(m);
            t.addPart(r);
            motorParts.add(m);
            rotorParts.add(r);
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                assemblyRobotNotification.assembleMotorRotorPairs(motorParts, rotorParts, job);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private synchronized boolean checkForWorkWithModulesForAssemblyRobot(){
        //all necessary parts for a drone are available and were already assembled by the robots
        if(caseControlUnitPairs.size()>=1 && motorRotorPairs.size()>=3){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            Module caseControlUnitPair = caseControlUnitPairs.remove(0);
            notificationCallback.onModuleRemoved(caseControlUnitPair);
            ArrayList<Module> motorRotorPairModules = new ArrayList<Module>();
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addModule(caseControlUnitPair);
            int i = 0;
            while(i<3){
                Module mr = motorRotorPairs.remove(0);
                notificationCallback.onModuleRemoved(mr);
                t.addModule(mr);
                motorRotorPairModules.add(mr);
                i++;
            }
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                assemblyRobotNotification.assembleDrone(caseControlUnitPair, motorRotorPairModules, job);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private synchronized boolean checkForWorkWithDroneForCalibrationRobot(){
        if(drones.size()>0 && drones.get(drones.size()-1).getStatus() == Status.ASSEMBLED ){
            ICalibratedNotification calibrationRobotNotification = calibrationRobots.poll();
            if (calibrationRobotNotification == null) {
                return false;
            }
            Drone drone = drones.remove(drones.size()-1);
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addDrone(drone);
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                calibrationRobotNotification.calibrateModuleInDrone(drone, job);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }

        }
        return false;
    }

    private synchronized boolean checkForWorkWithMotorRotorPairForCalibrationRobot(){
        if(motorRotorPairs.size()>0 && motorRotorPairs.get(motorRotorPairs.size()-1).getStatus() == Status.ASSEMBLED ){
            ICalibratedNotification calibrationRobotNotification = calibrationRobots.poll();
            if (calibrationRobotNotification == null) {
                return false;
            }
            Module motorRotorPair = motorRotorPairs.remove(motorRotorPairs.size()-1);
            notificationCallback.onModuleRemoved(motorRotorPair);
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addModule(motorRotorPair);
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                calibrationRobotNotification.calibrateMotorRotorPair(motorRotorPair, job);
                return true;
            } catch (RemoteException e) {
                logger.debug(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private synchronized boolean checkForWorkWithDroneForLogisticRobot(){
        if(drones.size()>0 && drones.get(0).getStatus() == Status.CALIBRATED){
            ITestedNotification logisticRobotNotification = logisticRobots.poll();
            if(logisticRobotNotification == null){
                return false;
            }
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            Drone drone = drones.remove(0);
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addDrone(drone);
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                logisticRobotNotification.testDrone(drone, job);
                return true;
            } catch (RemoteException e) {
                logger.debug(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }
}