package at.ac.tuwien.rmi;

import at.ac.tuwien.common.notification.IPaintedNotification;
import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RMIServer extends UnicastRemoteObject implements IRMIServer, IServer {

    private final static Logger logger = LoggerFactory.getLogger(RMIServer.class);
    private Registry registry;
    private CopyOnWriteArrayList<Part> cases, controlUnits, motors, rotors;
    private CopyOnWriteArrayList<Module> caseControlUnitPairs, motorRotorPairs;
    private CopyOnWriteArrayList<Drone> drones, goodDrones, badDrones;
    private CopyOnWriteArrayList<Order> orders;
    private Queue<IPaintedNotification> paintingRobots;
    private Queue<IAssembledNotification> assemblyRobots;
    private Queue<ICalibratedNotification> calibrationRobots;
    private Queue<ITestedNotification> logisticRobots;
    private AtomicReference<Integer> jobId;
    private ConcurrentHashMap<Job,Transaction> jobs;
    private INotificationCallback notificationCallback;
    private String host;
    private int port;

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
        this.orders = new CopyOnWriteArrayList<>();
        this.paintingRobots = new ConcurrentLinkedQueue<>();
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
            this.port = getRandomFreePort();
            this.host = Constants.SERVER_HOST;
            registry = LocateRegistry.createRegistry(port);
            registry.bind(Constants.SERVER_NAME, this);
            notificationCallback.setTitle("Drone Factory - " + Constants.SERVER_NAME + "@" + port);
            logger.debug("Server listening using port {} and name {}", port, Constants.SERVER_NAME);
        } catch (RemoteException e) {
            logger.error(e.getMessage());
        } catch (AlreadyBoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
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
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public synchronized int getAmount(Part part) throws RemoteException {
        if(part.getPartType() == PartType.CASE){
            return (int)cases.stream().filter(new Predicate<Part>() {
                @Override
                public boolean test(Part partToBeCompared) {
                    if(partToBeCompared.getCaseType() == part.getCaseType() && partToBeCompared.getColor() == Color.GRAY){
                        return true;
                    }
                    return false;
                }
            }).count();
        }
        else if(part.getPartType() == PartType.CONTROL_UNIT){
            return controlUnits.size();
        }
        else if(part.getPartType() == PartType.MOTOR){
            return motors.size();
        }
        else if(part.getPartType() == PartType.ROTOR){
            return rotors.size();
        }
        return 0;
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
        if(part.getColor() == Color.GRAY){
            checkForWorkWithPartForPaintingRobot();
        }
        checkForWorkWithPartsForAssemblyRobot();
    }

    @Override
    public void registerPaintingRobot(IPaintedNotification paintedNotification) throws RemoteException {
        paintingRobots.add(paintedNotification);
        logger.debug("painting robot is ready to do some work.");
        checkForWorkWithPartForPaintingRobot();
    }

    @Override
    public synchronized void partPainted(Part part, Job job) throws RemoteException {
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
        supply(part);
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

        Order o = findOrderById(drone.getOrderId());
        if(drone.getStatus() == Status.TESTED_GOOD){
            notificationCallback.onDroneRemoved(drone);
            goodDrones.add(drone);
            notificationCallback.onGoodDroneTested(drone);
            if(o != null){
                notificationCallback.onOrderModified(o);
                if(o.getOrderSize() == o.getNrOfProducedDrones()){
                    orders.remove(o);
                }
            }

        }
        else{
            if(o != null){
                o.setNrOfProducedDrones(o.getNrOfProducedDrones()-1);
                o.setNrOfAssembleCaseControlUnitPairRequests(o.getNrOfAssembleCaseControlUnitPairRequests()-1);
                o.setNrOfAssembleMotorRotorPairRequests(o.getNrOfAssembleMotorRotorPairRequests()-3);
                o.setNrOfPaintPartRequests(o.getNrOfPaintPartRequests()-1);
            }
            notificationCallback.onDroneRemoved(drone);
            badDrones.add(drone);
            notificationCallback.onBadDroneTested(drone);
        }
    }

    private synchronized boolean checkForWorkWithPartForPaintingRobot(){
        UUID orderId = null;
        Color color = Color.GRAY;
        Part casePart;
        Order order = null;
        List<Part> casesNeeded = new ArrayList<>();
        for(Order o : orders){
            //look for cases that were not assigned to any order(gray) and have got the order's case type.
            if(o.getNrOfPaintPartRequests()<o.getOrderSize()){
                casesNeeded = cases.stream().filter(new Predicate<Part>() {
                    @Override
                    public boolean test(Part part) {
                        if(part.getColor() == Color.GRAY && part.getCaseType() == o.getCaseType()){
                            return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());

                //found cases for order.
                if(casesNeeded.size()>0){
                    logger.debug("found cases for order.");
                    orderId = o.getOrderId();
                    color = o.getDroneColor();
                    casePart = casesNeeded.get(0);
                    order = o;
                    break;
                }
            }
        }

        //no cases were found. so paint some random case which was not already painted of course.
        if(casesNeeded.isEmpty()){
            logger.debug("no cases matching any order were found. continuing in random mode.");
            casesNeeded = cases.stream().filter(new Predicate<Part>() {
                @Override
                public boolean test(Part part) {
                    if(part.getPartType() == PartType.CASE && part.getColor() == Color.GRAY){
                        return true;
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }

        if(casesNeeded.size()>0){
            IPaintedNotification paintedNotification = paintingRobots.poll();
            if(paintedNotification == null){
                return false;
            }
            if(order != null){
                order.setNrOfPaintPartRequests(order.getNrOfPaintPartRequests()+1);
            }
            casePart = casesNeeded.get(0);
            cases.remove(casePart);
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            notificationCallback.onPartRemoved(casePart);
            t.addPart(casePart);
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                paintedNotification.paintPart(casePart, color, job, orderId);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private synchronized boolean checkForWorkWithPartsForAssemblyRobot(){

        for(Order o : orders){
            Order order = null;
            if(o.getNrOfAssembleCaseControlUnitPairRequests()<o.getOrderSize()){
                List<Part> caseParts = cases.stream().filter(new Predicate<Part>() {
                    @Override
                    public boolean test(Part part) {
                        if(part.getCaseType() == o.getCaseType() && part.getColor() == o.getDroneColor()
                                && (part.getOrderId() == null || part.getOrderId().equals(o.getOrderId()))){
                            return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());
                if(caseParts.size()>0 && controlUnits.size()>0){
                    IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
                    if(assemblyRobotNotification == null){
                        return false;
                    }
                    logger.debug("found case and control unit for order to assemble");
                    o.setNrOfAssembleCaseControlUnitPairRequests(o.getNrOfAssembleCaseControlUnitPairRequests()+1);
                    Part casePart = caseParts.remove(0);
                    cases.remove(casePart);
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
                        assemblyRobotNotification.assembleCaseControlUnitPair(casePart, controlUnit, job, o.getOrderId());
                        return true;
                    } catch (RemoteException e) {
                        logger.error(e.getMessage());
                        t.rollback();
                    }
                }
                else if(o.getNrOfAssembleMotorRotorPairRequests()<o.getOrderSize()*3){
                    if(motorRotorPairs.size()<3 && motors.size()>0 && rotors.size()>0){
                        IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
                        if(assemblyRobotNotification == null){
                            return false;
                        }
                        ArrayList<Part> motorParts = new ArrayList<Part>();
                        ArrayList<Part> rotorParts = new ArrayList<Part>();
                        int i = 0;
                        Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
                        while(i<3 && motors.size()>0 && rotors.size()>0){
                            if(order != null){
                                order.setNrOfAssembleMotorRotorPairRequests(order.getNrOfAssembleMotorRotorPairRequests());
                            }
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
                }
            }
        }

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
        //assure we get only parts not related to any order.
        List<Part> caseParts = cases.stream().filter(new Predicate<Part>() {
            @Override
            public boolean test(Part part) {
                if(part.getOrderId()==null){
                    return true;
                }
                return false;
            }
        }).collect(Collectors.toList());
        if(caseParts.size()>0 && controlUnits.size()>0){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            Part casePart = caseParts.get(0);
            cases.remove(casePart);
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
                assemblyRobotNotification.assembleCaseControlUnitPair(casePart, controlUnit, job, null);
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
        Order order = null;
        UUID orderId = null;
        List<Module> caseControlUnitModules = new ArrayList<>();
        for(Order o : orders){
            if(o.getNrOfAssembleDronesRequests()<o.getOrderSize()){
                caseControlUnitModules = caseControlUnitPairs.stream().filter(new Predicate<Module>() {
                    @Override
                    public boolean test(Module module) {
                        if(module.getOrderId() !=null && module.getOrderId().equals(o.getOrderId())){
                            return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());
                if(caseControlUnitModules.size()>0){
                    order = o;
                    orderId = o.getOrderId();
                    break;
                }
            }
        }

        if(caseControlUnitModules.isEmpty()){
            caseControlUnitModules = caseControlUnitPairs.stream().filter(new Predicate<Module>() {
                @Override
                public boolean test(Module module) {
                    if(module.getOrderId() == null){
                        return true;
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }

        //all necessary parts for a drone are available and were already assembled by the robots
        if(caseControlUnitModules.size()>=1 && motorRotorPairs.size()>=3){
            IAssembledNotification assemblyRobotNotification = assemblyRobots.poll();
            if(assemblyRobotNotification == null){
                return false;
            }
            if(order != null){
                order.setNrOfAssembleDronesRequests(order.getNrOfAssembleDronesRequests()+1);
            }
            Module caseControlUnitPair = caseControlUnitModules.remove(0);
            caseControlUnitPairs.remove(caseControlUnitPair);
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
                assemblyRobotNotification.assembleDrone(caseControlUnitPair, motorRotorPairModules, job, orderId);
                return true;
            } catch (RemoteException e) {
                logger.error(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private synchronized boolean checkForWorkWithDroneForCalibrationRobot(){
        if(drones.size()>0 && drones.get(drones.size()-1).getStatus() != Status.CALIBRATED ){
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
        Order order = null;
        UUID orderId = null;
        List<Drone> droneList = new ArrayList<>();
        for(Order o : orders){
            if(o.getNrOfProducedDrones()<o.getOrderSize()){
                droneList = drones.stream().filter(new Predicate<Drone>() {
                    @Override
                    public boolean test(Drone drone) {
                        if(drone.getStatus() == Status.CALIBRATED && drone.getOrderId() != null && drone.getOrderId().equals(o.getOrderId())){
                            return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());
                if(droneList.size()>0){
                    order = o;
                    orderId = o.getOrderId();
                    break;
                }
            }
        }
        if(droneList.isEmpty()){
            droneList = drones.stream().filter(new Predicate<Drone>() {
                @Override
                public boolean test(Drone drone) {
                    if(drone.getStatus() == Status.CALIBRATED && drone.getOrderId() == null){
                        return true;
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }

        if(droneList.size()>0){
            ITestedNotification logisticRobotNotification = logisticRobots.poll();
            if(logisticRobotNotification == null){
                return false;
            }
            if(order != null){
                order.setNrOfProducedDrones(order.getNrOfProducedDrones()+1);
            }
            jobId.set(jobId.get()+1);
            Job job = new Job(jobId.get());
            Drone drone = droneList.remove(0);
            drones.remove(drone);
            Transaction t = new Transaction(this, Constants.TRANSACTION_TIME_TO_LIVE);
            t.addDrone(drone);
            jobs.put(job, t);
            (new Thread(t)).start();
            try {
                logisticRobotNotification.testDrone(drone, job, orderId);
                return true;
            } catch (RemoteException e) {
                logger.debug(e.getMessage());
                t.rollback();
            }
        }
        return false;
    }

    private int getRandomFreePort() throws IOException {
        ServerSocket s = new ServerSocket(0);
        s.close();
        return s.getLocalPort();
    }

    private Order findOrderById(UUID orderId){
        if(orderId == null){
            return null;
        }
        for(Order o : orders){
            if(o.getOrderId().equals(orderId)){
                return o;
            }
        }
        return null;
    }

}