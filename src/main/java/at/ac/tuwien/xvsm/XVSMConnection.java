package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.AssembledNotification;
import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XVSMConnection implements IConnection {

    private final static Logger logger = LoggerFactory.getLogger(XVSMConnection.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference partsContainer, modulesContainer, dronesContainer, testedDronesContainer;
    private ContainerReference assembledNotifications, calibratedNotifications, testedNotifications;

    @Override
    public void establish() throws ConnectionException {
        this.core = DefaultMzsCore.newInstance(Constants.RANDOM_FREE_PORT);
        this.capi = new Capi(core);

        List<Coordinator> coordinators = new ArrayList<Coordinator>();
        coordinators.add(new QueryCoordinator());
        coordinators.add(new FifoCoordinator());

        this.partsContainer = Utils.getOrCreateContainer(Constants.PARTS_CONTAINER, capi, coordinators);
        this.modulesContainer = Utils.getOrCreateContainer(Constants.MODULES_CONTAINER, capi, coordinators);
        this.dronesContainer = Utils.getOrCreateContainer(Constants.DRONES_CONTAINER, capi, coordinators);
        this.testedDronesContainer = Utils.getOrCreateContainer(Constants.TESTED_DRONES_CONTAINER, capi, coordinators);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi, coordinators);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi, coordinators);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi, coordinators);
        logger.debug("Connection established.");
    }

    @Override
    public void supply(Part part) throws ConnectionException {
        try{
            capi.write(partsContainer, new Entry(part));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException {
        try {
            establish();
            capi.write(assembledNotifications, new Entry((Serializable) assemblyRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void moduleAssembled(Module module, Job job) throws ConnectionException {
        try{
            establish();
            capi.write(modulesContainer, new Entry(module));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneAssembled(Drone drone, Job job) throws ConnectionException {
        try{
            establish();
            capi.write(dronesContainer, new Entry(drone));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException {
        try {
            establish();
            capi.write(calibratedNotifications, new Entry((Serializable) calibrationRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void motorRotorPairCalibrated(Module module, Job job) throws ConnectionException {
        try{
            establish();
            capi.write(modulesContainer, new Entry(module));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneCalibrated(Drone drone, Job job) throws ConnectionException {
        try{
            establish();
            capi.write(dronesContainer, new Entry(drone));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException {
        try {
            establish();
            capi.write(testedNotifications, new Entry((Serializable) logisticRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneTested(Drone drone, Job job) throws ConnectionException {
        try{
            establish();
            capi.write(testedDronesContainer, new Entry(drone));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void end() throws ConnectionException {
        if(core!=null)
            core.shutdown(true);
    }
}
