package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.*;
import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.rmi.*;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import at.ac.tuwien.xvsm.aspect.SpaceAspect;
import at.ac.tuwien.xvsm.listener.*;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.SpaceIPoint;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class XVSMServer implements IServer {

    private final static Logger logger = LoggerFactory.getLogger(XVSMServer.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference partsContainer, modulesContainer, dronesContainer, testedDronesContainer;
    private CopyOnWriteArrayList<Order> orders;
    private ContainerReference paintedNotifications, assembledNotifications, calibratedNotifications, testedNotifications;
    private INotificationCallback notificationCallback;
    private String host;
    private int port;

    @Override
    public void registerGUINotificationCallback(INotificationCallback notificationCallback) {
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void start() {

        //setup the space
        this.core = DefaultMzsCore.newInstance(Constants.RANDOM_FREE_PORT);
        this.capi = new Capi(core);
        this.host = Constants.SERVER_HOST;
        this.port = core.getConfig().getSpaceUri().getPort();
        notificationCallback.setTitle("Drone Factory - @" + port);
        logger.debug("XVSMServer started");
        logger.debug("Server listening using port {}", port);

        List<Coordinator> coordinators = new ArrayList<Coordinator>();
        coordinators.add(new QueryCoordinator());
        coordinators.add(new FifoCoordinator());

        this.orders = new CopyOnWriteArrayList<>();

        //create the containers
        this.partsContainer = Utils.getOrCreateContainer(Constants.PARTS_CONTAINER, capi, coordinators, host, port);
        this.modulesContainer = Utils.getOrCreateContainer(Constants.MODULES_CONTAINER, capi, coordinators, host, port);
        this.dronesContainer = Utils.getOrCreateContainer(Constants.DRONES_CONTAINER, capi, coordinators, host, port);
        this.testedDronesContainer = Utils.getOrCreateContainer(Constants.TESTED_DRONES_CONTAINER, capi, coordinators, host, port);
        this.paintedNotifications = Utils.getOrCreateContainer(Constants.PAINTED_NOTIFICATIONS, capi, coordinators, host, port);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi, coordinators, host, port);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi, coordinators, host, port);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi, coordinators, host, port);
        try {
            //add a space aspect for aborting transactions
            capi.addSpaceAspect(new SpaceAspect(capi,notificationCallback),null, SpaceIPoint.POST_ROLLBACK_TRANSACTION);

            //add listeners
            NotificationManager notificationManager = new NotificationManager(core);
            RobotNotificationListener rbn = new RobotNotificationListener(this);
            notificationManager.createNotification(paintedNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(assembledNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(calibratedNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(testedNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(partsContainer, new PartsListener(this, notificationCallback)
            ,Operation.WRITE, Operation.TAKE);
            notificationManager.createNotification(modulesContainer, new ModulesListener(this, notificationCallback)
                    ,Operation.WRITE, Operation.TAKE);
            notificationManager.createNotification(dronesContainer, new DronesListener(this, notificationCallback)
                    ,Operation.WRITE);
            notificationManager.createNotification(testedDronesContainer,
                    new TestedDronesListener(this, notificationCallback)
                    ,Operation.WRITE);

        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        logger.debug("created containers");
    }

    @Override
    public void stop(){
        if(core!=null)
            core.shutdown(true);
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

    public synchronized boolean checkForWorkWithPartForPaintingRobot(){
        // take the painting robot notification (if available)
        PaintedNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<PaintedNotification> notifications = capi.take(paintedNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        for(Order o : orders){
            //look for cases that were not assigned to any order(gray) and have got the order's case type.
            if(o.getNrOfPaintPartRequests()<o.getOrderSize()) {
                Property partTypeProp = Property.forName("*", "partType");
                Property caseTypeProp = Property.forName("*", "caseType");
                Property colorProp = Property.forName("*", "color");
                Query query = new Query().filter(
                        Matchmakers.and(
                                partTypeProp.equalTo(PartType.CASE),
                                caseTypeProp.equalTo(o.getCaseType()),
                                colorProp.equalTo(Color.GRAY)
                        )
                ).cnt(1);
                try {
                    List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query, 1),
                            MzsConstants.RequestTimeout.DEFAULT, null);
                    capi.commitTransaction(notificationTx);
                    Part casePart = entries.get(0);
                    o.setNrOfPaintPartRequests(o.getNrOfPaintPartRequests()+1);
                    notification.paintPart(casePart, o.getDroneColor(), new Job(1), o.getOrderId());
                    return true;
                } catch (MzsCoreException e) {
                    logger.debug(e.getMessage());
                } catch (RemoteException e) {
                    logger.debug(e.getMessage());
                }
            }
        }

        //no cases were found. so paint some random case which was not already painted of course.
        Property partTypeProp = Property.forName("*", "partType");
        Property colorProp = Property.forName("*", "color");
        Query query = new Query().filter(
                Matchmakers.and(
                        partTypeProp.equalTo(PartType.CASE),
                        colorProp.equalTo(Color.GRAY)
                )
        ).cnt(1);
        try {
            List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query, 1),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            Part casePart = entries.get(0);
            notification.paintPart(casePart, Color.GRAY, new Job(1), null);
            return true;
        } catch (MzsCoreException e) {
            logger.debug(e.getMessage());
        } catch (RemoteException e) {
            logger.debug(e.getMessage());
        }

        //if we reached here, no work was found, so give the notification back.
        try {
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.debug(e.getMessage());
        }
        return false;
    }

    public synchronized boolean checkForWorkWithPartsForAssemblyRobot(){

        // take the assembly robot notification (if available)
        AssembledNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<AssembledNotification> notifications = capi.take(assembledNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        for(Order o : orders){
            if(o.getNrOfAssembleCaseControlUnitPairRequests()<o.getOrderSize()){
                Property partTypeProp = Property.forName("*", "partType");
                Property caseTypeProp = Property.forName("*", "caseType");
                Property droneColorProp = Property.forName("*", "color");
                Query query = new Query().filter(
                        Matchmakers.or(
                                Matchmakers.and(partTypeProp.equalTo(PartType.CONTROL_UNIT)),
                                Matchmakers.and(
                                        partTypeProp.equalTo(PartType.CASE),
                                        caseTypeProp.equalTo(o.getCaseType()),
                                        droneColorProp.equalTo(o.getDroneColor())))
                ).distinct(partTypeProp).sortup(partTypeProp);
                List<Part> entries = null;
                try {
                    entries = capi.take(partsContainer, QueryCoordinator.newSelector(query,2),
                            MzsConstants.RequestTimeout.DEFAULT, null);
                    capi.commitTransaction(notificationTx);
                    Part casePart = entries.get(0);
                    Part controlUnitPart = entries.get(1);
                    o.setNrOfAssembleCaseControlUnitPairRequests(o.getNrOfAssembleCaseControlUnitPairRequests()+1);
                    notification.assembleCaseControlUnitPair(casePart, controlUnitPart, new Job(1), o.getOrderId());
                    return true;
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
            }
            else if(o.getNrOfAssembleMotorRotorPairRequests()<o.getOrderSize()*3){
                ArrayList<Part> motors = new ArrayList<>();
                ArrayList<Part> rotors = new ArrayList<>();
                try {
                    Property partTypeProp = Property.forName("*", "partType");
                    Query query = new Query().filter(
                            Matchmakers.or(
                                    partTypeProp.equalTo(PartType.MOTOR),
                                    partTypeProp.equalTo(PartType.ROTOR)
                            )
                    ).distinct(partTypeProp).sortup(partTypeProp);
                    int n = 3;
                    for(int i=0; i<n; i++){
                        List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query,2),
                                MzsConstants.RequestTimeout.DEFAULT, null);
                        if(entries.size()<2){
                            logger.info(entries.size()+" entries");
                            break;
                        }
                        Part motor = entries.get(0);
                        Part rotor = entries.get(1);
                        motors.add(motor);
                        rotors.add(rotor);
                    }
                } catch (CountNotMetException e){
                    logger.error(e.getMessage());

                } catch (MzsCoreException e) {
                    logger.error(e.getMessage());
                }

                if(motors.size()>0 && rotors.size()>0 && motors.size()==rotors.size()){
                    o.setNrOfAssembleMotorRotorPairRequests(o.getNrOfAssembleMotorRotorPairRequests()+motors.size());
                    notification.assembleMotorRotorPairs(motors, rotors, new Job(1));
                    try {
                        capi.commitTransaction(notificationTx);
                    } catch (MzsCoreException e) {
                        logger.debug(e.getMessage());
                    }
                    return true;
                }
            }
        }

        // step A: check for cases and control units to assemble
        try {
            Property partTypeProp = Property.forName("*", "partType");
            Query query = new Query().filter(
                    Matchmakers.or(
                            partTypeProp.equalTo(PartType.CASE),
                            partTypeProp.equalTo(PartType.CONTROL_UNIT)
                    )
            ).distinct(partTypeProp).sortup(partTypeProp);
            List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query,2),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            Part casePart = entries.get(0);
            Part controlUnitPart = entries.get(1);
            notification.assembleCaseControlUnitPair(casePart, controlUnitPart, new Job(1), null);
            return true;

        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        logger.info("Step A: there are not at least 1 case AND 1 control unit, but we continue and try step B");
        // step B: assemble maximally 3 motor/rotor modules
        ArrayList<Part> motors = new ArrayList<>();
        ArrayList<Part> rotors = new ArrayList<>();
        try {
            Property partTypeProp = Property.forName("*", "partType");
            Query query = new Query().filter(
                    Matchmakers.or(
                            partTypeProp.equalTo(PartType.MOTOR),
                            partTypeProp.equalTo(PartType.ROTOR)
                    )
            ).distinct(partTypeProp).sortup(partTypeProp);
            int n = 3;
            for(int i=0; i<n; i++){
                List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query,2),
                        MzsConstants.RequestTimeout.DEFAULT, null);
                if(entries.size()<2){
                    logger.info(entries.size()+" entries");
                    break;
                }
                Part motor = entries.get(0);
                Part rotor = entries.get(1);
                motors.add(motor);
                rotors.add(rotor);
            }
        } catch (CountNotMetException e){
            logger.error(e.getMessage());

        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }
        try {
            if(motors.size()>0 && rotors.size()>0 && motors.size()==rotors.size()){
                notification.assembleMotorRotorPairs(motors, rotors, new Job(1));
                capi.commitTransaction(notificationTx);
                return true;
            }
            logger.info("Step B: no motors and rotors found. Giving the notification back.");
            // return back the notification, as we did not find work for the robot
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public synchronized boolean checkForWorkWithModulesForAssemblyRobot(){

        // take the assembly robot notification (if available)
        AssembledNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<AssembledNotification> notifications = capi.take(assembledNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        for(Order o : orders){
            if(o.getNrOfAssembleDronesRequests()<o.getOrderSize()){
                TransactionReference tx = null;
                ArrayList<Module> caseControlUnitPairs = new ArrayList<>();
                ArrayList<Module> motorRotorPairs = new ArrayList<>();
                try {
                    Property moduleTypeProp = Property.forName("*", "moduleType");
                    Property orderIdProp = Property.forName("*", "orderId");
                    Query query1 = new Query().filter(
                            Matchmakers.and(
                                    moduleTypeProp.equalTo(ModuleType.CASE_CONTROL_UNIT_PAIR),
                                    orderIdProp.equalTo(o.getOrderId())));
                    Query query2 = new Query().filter(moduleTypeProp.equalTo(ModuleType.MOTOR_ROTOR_PAIR));
                    tx =  capi.createTransaction(Constants.TRANSACTION_TIME_TO_LIVE, null);
                    caseControlUnitPairs = capi.take(modulesContainer, QueryCoordinator.newSelector(query1, 1),
                            MzsConstants.RequestTimeout.DEFAULT, tx);
                    motorRotorPairs =  capi.take(modulesContainer, QueryCoordinator.newSelector(query2, 3),
                            MzsConstants.RequestTimeout.DEFAULT, tx);
                    capi.commitTransaction(tx);
                    capi.commitTransaction(notificationTx);
                    notification.assembleDrone(caseControlUnitPairs.get(0), motorRotorPairs, new Job(1), null);
                    return true;

                } catch (MzsCoreException e) {
                    logger.info(e.getMessage());
                    try {
                        capi.rollbackTransaction(tx);
                    } catch (MzsCoreException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        // step C: check for 3x motor/rotor pairs and 1x case/control unit pair
        TransactionReference tx = null;
        ArrayList<Module> caseControlUnitPairs = new ArrayList<>();
        ArrayList<Module> motorRotorPairs = new ArrayList<>();
        try {
            Property moduleTypeProp = Property.forName("*", "moduleType");
            Query query1 = new Query().filter(moduleTypeProp.equalTo(ModuleType.CASE_CONTROL_UNIT_PAIR));
            Query query2 = new Query().filter(moduleTypeProp.equalTo(ModuleType.MOTOR_ROTOR_PAIR));
            tx =  capi.createTransaction(Constants.TRANSACTION_TIME_TO_LIVE, null);
            caseControlUnitPairs = capi.take(modulesContainer, QueryCoordinator.newSelector(query1, 1),
                    MzsConstants.RequestTimeout.DEFAULT, tx);
            motorRotorPairs =  capi.take(modulesContainer, QueryCoordinator.newSelector(query2, 3),
                    MzsConstants.RequestTimeout.DEFAULT, tx);
            capi.commitTransaction(tx);
            capi.commitTransaction(notificationTx);
            notification.assembleDrone(caseControlUnitPairs.get(0), motorRotorPairs, new Job(1), null);
            return true;

        } catch (MzsCoreException e) {
            logger.info(e.getMessage());
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
        }

        try {
            logger.info("Step C: not enough modules to assemble a drone. Giving the notification back.");
            // return back the notification, as we did not find work for the robot
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public synchronized boolean checkForWorkWithMotorRotorPairForCalibrationRobot(){
        // take the calibrator robot notification (if available)
        CalibratedNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<CalibratedNotification> notifications = capi.take(calibratedNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        try {
            Property moduleTypeProp = Property.forName("*", "moduleType");
            Property moduleStatusProp = Property.forName("*", "status");
            Query query = new Query().filter(
                    Matchmakers.and(
                            moduleTypeProp.equalTo(ModuleType.MOTOR_ROTOR_PAIR),
                            moduleStatusProp.equalTo(Status.ASSEMBLED)));
            List<Module> entries = capi.take(modulesContainer, QueryCoordinator.newSelector(query,1),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            notification.calibrateMotorRotorPair(entries.get(0), new Job(1));
            return true;
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        try {
            logger.info("no motor/rotor pair found to calibrate");
            // return back the notification, as we did not find work for the robot
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public synchronized boolean checkForWorkWithDroneForCalibrationRobot(){
        // take the calibrator robot notification (if available)
        CalibratedNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<CalibratedNotification> notifications = capi.take(calibratedNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        try {
            Property droneStatusProp = Property.forName("*", "status");
            Property orderIdProp = Property.forName("*", "orderId");
            Query query = new Query().filter(droneStatusProp.notEqualTo(Status.CALIBRATED)).sortup(orderIdProp);
            List<Drone> entries = capi.take(dronesContainer, QueryCoordinator.newSelector(query,1),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            notification.calibrateModuleInDrone(entries.get(0),new Job(1));
            return true;
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        try {
            logger.info("no drone found to calibrate");
            // return back the notification, as we did not find work for the robot
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public synchronized boolean checkForWorkWithDroneForLogisticRobot(){
        // take the tester robot notification (if available)
        TestedNotification notification;
        TransactionReference notificationTx;
        try {
            notificationTx = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, null);
            ArrayList<TestedNotification> notifications = capi.take(testedNotifications, FifoCoordinator.newSelector(1),
                    MzsConstants.RequestTimeout.DEFAULT, notificationTx);
            notification = notifications.get(0);
        } catch (MzsCoreException e) {
            //no robot available, so we finish immediately.
            logger.info(e.getMessage());
            return false;
        }

        for(Order o : orders){
            //look for orders
            if(o.getNrOfProducedDrones()<o.getOrderSize()) {
                Property droneStatusProp = Property.forName("*", "status");
                Property orderIdProp = Property.forName("*", "orderId");
                Property caseTypeProp = Property.forName("*", "caseType");
                Property droneColorProp = Property.forName("*", "color");
                Query query1 = new Query().filter(
                        Matchmakers.and(
                                droneStatusProp.equalTo(Status.CALIBRATED),
                                orderIdProp.equalTo(o.getOrderId())
                        )
                ).cnt(1);
                try {
                    List<Drone> entries = capi.take(dronesContainer, QueryCoordinator.newSelector(query1, 1),
                            MzsConstants.RequestTimeout.DEFAULT, null);
                    capi.commitTransaction(notificationTx);
                    Drone drone = entries.get(0);
                    notification.testDrone(drone, new Job(1), o.getOrderId());
                    return true;
                } catch (MzsCoreException e) {
                    logger.debug(e.getMessage());
                }

                //try some drones matching our order
                Query query2 = new Query().filter(
                        Matchmakers.and(
                                droneStatusProp.equalTo(Status.CALIBRATED),
                                caseTypeProp.equalTo(o.getCaseType()),
                                droneColorProp.equalTo(o.getDroneColor())
                        )
                ).cnt(1);
                try {
                    List<Drone> entries = capi.take(dronesContainer, QueryCoordinator.newSelector(query2, 1),
                            MzsConstants.RequestTimeout.DEFAULT, null);
                    capi.commitTransaction(notificationTx);
                    Drone drone = entries.get(0);
                    notification.testDrone(drone, new Job(1), o.getOrderId());
                    return true;
                } catch (MzsCoreException e) {
                    logger.debug(e.getMessage());
                }

            }
        }

        //try some random drones not assigned to any order
        try {
            Property droneStatusProp = Property.forName("*", "status");
            Property orderIdProp = Property.forName("*", "orderId");
            Query query = new Query().filter(
                    Matchmakers.and(
                            droneStatusProp.equalTo(Status.CALIBRATED),
                            orderIdProp.equalTo(null)
                            ));
            List<Drone> entries = capi.take(dronesContainer, QueryCoordinator.newSelector(query,1),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            notification.testDrone(entries.get(0), new Job(1), null);
            return true;
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        try {
            logger.info("no drone found to test");
            // return back the notification, as we did not find work for the robot
            capi.rollbackTransaction(notificationTx);
        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public Order findOrderById(UUID orderId){
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

    public CopyOnWriteArrayList<Order> getOrders() {
        return orders;
    }
}
