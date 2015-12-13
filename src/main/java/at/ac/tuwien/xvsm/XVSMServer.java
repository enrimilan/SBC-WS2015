package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.AssembledNotification;
import at.ac.tuwien.common.notification.CalibratedNotification;
import at.ac.tuwien.common.notification.TestedNotification;
import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.rmi.Transaction;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import at.ac.tuwien.xvsm.listener.*;
import org.apache.commons.collections.ArrayStack;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.mozartspaces.util.parser.sql.javacc.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class XVSMServer implements IServer {

    private final static Logger logger = LoggerFactory.getLogger(XVSMServer.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference partsContainer, modulesContainer, dronesContainer, testedDronesContainer;
    private ContainerReference assembledNotifications, calibratedNotifications, testedNotifications;
    private AtomicReference<Integer> jobId;
    private ConcurrentHashMap<Job,Transaction> jobs;
    private INotificationCallback notificationCallback;

    public XVSMServer(){
        this.jobId = new AtomicReference<>();
        jobId.set(0);
        this.jobs = new ConcurrentHashMap<>();
    }

    public void start() {

        //setup the space
        this.core = DefaultMzsCore.newInstance(Constants.SERVER_PORT);
        this.capi = new Capi(core);
        logger.debug("XVSMServer started");

        List<Coordinator> coordinators = new ArrayList<Coordinator>();
        coordinators.add(new QueryCoordinator());
        coordinators.add(new FifoCoordinator());

        //create the containers
        this.partsContainer = Utils.getOrCreateContainer(Constants.PARTS_CONTAINER, capi, coordinators);
        this.modulesContainer = Utils.getOrCreateContainer(Constants.MODULES_CONTAINER, capi, coordinators);
        this.dronesContainer = Utils.getOrCreateContainer(Constants.DRONES_CONTAINER, capi, coordinators);
        this.testedDronesContainer = Utils.getOrCreateContainer(Constants.TESTED_DRONES_CONTAINER, capi, coordinators);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi, coordinators);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi, coordinators);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi, coordinators);
        try {
            NotificationManager notificationManager = new NotificationManager(core);
            RobotNotificationListener rbn = new RobotNotificationListener(this);
            notificationManager.createNotification(assembledNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(calibratedNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(testedNotifications, rbn, Operation.WRITE);
            notificationManager.createNotification(partsContainer, new PartsListener(this, notificationCallback)
            ,Operation.WRITE);
            notificationManager.createNotification(modulesContainer, new ModulesListener(this, notificationCallback)
                    ,Operation.WRITE);
            notificationManager.createNotification(dronesContainer, new DronesListener(this, notificationCallback)
                    ,Operation.WRITE);
            notificationManager.createNotification(testedDronesContainer,
                    new TestedDronesListener(this, notificationCallback)
                    ,Operation.WRITE);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("created containers");
    }

    @Override
    public void registerGUINotificationCallback(INotificationCallback notificationCallback) {
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void stop(){

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

        // step A: check for cases and control units to assemble
        try {
            Property partTypeProp = Property.forName("*", "partType");
            Query query = new Query().filter(
                    Matchmakers.or(
                            partTypeProp.equalTo(PartType.CASE),
                            partTypeProp.equalTo(PartType.CONTROL_UNIT)
                    )
            ).sortdown(partTypeProp).distinct(partTypeProp);
            List<Part> entries = capi.take(partsContainer, QueryCoordinator.newSelector(query,2),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            Part casePart = entries.get(0);
            Part controlUnitPart = entries.get(1);
            notificationCallback.onPartRemoved(casePart);
            notificationCallback.onPartRemoved(controlUnitPart);
            notification.assembleCaseControlUnitPair(casePart, controlUnitPart, new Job(1));
            return true;

        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }

        logger.info("Step A: there are not at least 1 case AND 1 control unit, but we continue and try step B");
        // step B: assembly maximally 3 motor/rotor modules
        ArrayList<Part> motors = new ArrayList<>();
        ArrayList<Part> rotors = new ArrayList<>();
        try {
            Property partTypeProp = Property.forName("*", "partType");
            Query query = new Query().filter(
                    Matchmakers.or(
                            partTypeProp.equalTo(PartType.MOTOR),
                            partTypeProp.equalTo(PartType.ROTOR)
                    )
            ).sortdown(partTypeProp).distinct(partTypeProp);
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
                notificationCallback.onPartRemoved(motor);
                notificationCallback.onPartRemoved(rotor);
            }
        } catch (CountNotMetException e){
            logger.error(e.getMessage());

        } catch (MzsCoreException e) {
            logger.error(e.getMessage());
        }
        try {
            if(motors.size()>0){
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

        // step C: check for 3x motor/rotor pairs and 1x case/control unit pair
        TransactionReference tx = null;
        try {
            Property moduleTypeProp = Property.forName("*", "moduleType");
            Query query1 = new Query().filter(moduleTypeProp.equalTo(ModuleType.CASE_CONTROL_UNIT_PAIR));
            Query query2 = new Query().filter(moduleTypeProp.equalTo(ModuleType.MOTOR_ROTOR_PAIR));
            tx =  capi.createTransaction(3000, null);
            List<Module> caseControlUnitPairs = capi.take(modulesContainer, QueryCoordinator.newSelector(query1, 1),
                    MzsConstants.RequestTimeout.DEFAULT, tx);
            ArrayList<Module> motorRotorPairs =  capi.take(modulesContainer, QueryCoordinator.newSelector(query2, 3),
                    MzsConstants.RequestTimeout.DEFAULT, tx);
            capi.commitTransaction(tx);
            notificationCallback.onModuleRemoved(caseControlUnitPairs.get(0));
            notificationCallback.onModuleRemoved(motorRotorPairs.get(0));
            notificationCallback.onModuleRemoved(motorRotorPairs.get(1));
            notificationCallback.onModuleRemoved(motorRotorPairs.get(2));
            notification.assembleDrone(caseControlUnitPairs.get(0), motorRotorPairs, new Job(1));
            capi.commitTransaction(notificationTx);
            return true;

        } catch (MzsCoreException e) {
            logger.info(e.getMessage());
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                logger.info(e.getMessage());
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
            notificationCallback.onModuleRemoved(entries.get(0));
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
            Query query = new Query().filter(droneStatusProp.notEqualTo(Status.CALIBRATED));
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

        try {
            Property droneStatusProp = Property.forName("*", "status");
            Query query = new Query().filter(
                    Matchmakers.and(
                            droneStatusProp.notEqualTo(Status.TESTED_GOOD),
                    droneStatusProp.notEqualTo(Status.TESTED_BAD)));
            List<Drone> entries = capi.take(dronesContainer, QueryCoordinator.newSelector(query,1),
                    MzsConstants.RequestTimeout.DEFAULT, null);
            capi.commitTransaction(notificationTx);
            notification.testDrone(entries.get(0), new Job(1));
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
}
