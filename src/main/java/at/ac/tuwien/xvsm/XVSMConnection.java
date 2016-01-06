package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.notification.*;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.capi3.*;
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
    private String host;
    private int port;
    private ContainerReference partsContainer, modulesContainer, dronesContainer, testedDronesContainer;
    private ContainerReference assembledNotifications, calibratedNotifications, testedNotifications;

    public XVSMConnection(){
        this.core = DefaultMzsCore.newInstance(Constants.RANDOM_FREE_PORT);
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void establish(String host, int port) throws ConnectionException {
        logger.debug("Establishing connection with host {} and port {}", host, port);
        this.capi = new Capi(core);
        List<Coordinator> coordinators = new ArrayList<Coordinator>();
        coordinators.add(new QueryCoordinator());
        coordinators.add(new FifoCoordinator());

        this.partsContainer = Utils.getOrCreateContainer(Constants.PARTS_CONTAINER, capi, coordinators, host, port);
        this.modulesContainer = Utils.getOrCreateContainer(Constants.MODULES_CONTAINER, capi, coordinators, host, port);
        this.dronesContainer = Utils.getOrCreateContainer(Constants.DRONES_CONTAINER, capi, coordinators, host, port);
        this.testedDronesContainer = Utils.getOrCreateContainer(Constants.TESTED_DRONES_CONTAINER, capi, coordinators, host, port);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi, coordinators, host, port);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi, coordinators, host, port);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi, coordinators, host, port);
        logger.debug("Connection established.");
    }

    @Override
    public int getAmount(Part part) throws ConnectionException {
        Property partTypeProp = Property.forName("*", "partType");
        Property caseTypeProp = Property.forName("*", "caseType");
        Property colorProp = Property.forName("*", "color");
        Query query = new Query().filter(Matchmakers.and(
                    partTypeProp.equalTo(part.getPartType()),
                    caseTypeProp.equalTo(part.getCaseType()),
                    colorProp.equalTo(Color.GRAY)));
        try {
            return capi.read(partsContainer, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL),
                    MzsConstants.RequestTimeout.DEFAULT, null).size();
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
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
    public void registerPaintingRobot(PaintedNotification paintedNotification) throws ConnectionException {
        //TODO
    }

    @Override
    public void partPainted(Part part) throws ConnectionException {
        //TODO
    }

    @Override
    public void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException {
        try {
            capi.write(assembledNotifications, new Entry((Serializable) assemblyRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void moduleAssembled(Module module, Job job) throws ConnectionException {
        try{
            if(capi == null || modulesContainer == null){
                establish(host, port);
            }
            capi.write(modulesContainer, new Entry(module));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneAssembled(Drone drone, Job job) throws ConnectionException {
        try{
            if(capi == null || dronesContainer == null){
                establish(host, port);
            }
            capi.write(dronesContainer, new Entry(drone));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException {
        try {
            capi.write(calibratedNotifications, new Entry((Serializable) calibrationRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void motorRotorPairCalibrated(Module module, Job job) throws ConnectionException {
        try{
            capi.write(modulesContainer, new Entry(module));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneCalibrated(Drone drone, Job job) throws ConnectionException {
        try{
            capi.write(dronesContainer, new Entry(drone));
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException {
        try {
            capi.write(testedNotifications, new Entry((Serializable) logisticRobotNotification));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void droneTested(Drone drone, Job job) throws ConnectionException {
        try{
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
