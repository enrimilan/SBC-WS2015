package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.common.robot.notification.AssembledNotification;
import at.ac.tuwien.common.robot.notification.IAssembledNotification;
import at.ac.tuwien.common.robot.notification.ICalibratedNotification;
import at.ac.tuwien.common.robot.notification.ITestedNotification;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class XVSMConnection implements IConnection {

    private final static Logger logger = LoggerFactory.getLogger(XVSMConnection.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference motorsContainer, rotorsContainer, casesContainer, controlUnitsContainer;
    private ContainerReference assembledNotifications, calibratedNotifications, testedNotifications;

    @Override
    public void establish() throws ConnectionException {
        this.core = DefaultMzsCore.newInstance(Constants.RANDOM_FREE_PORT);
        this.capi = new Capi(core);
        this.motorsContainer = Utils.getOrCreateContainer(Constants.MOTORS_CONTAINER_NAME, capi);
        this.rotorsContainer = Utils.getOrCreateContainer(Constants.ROTORS_CONTAINER_NAME, capi);
        this.casesContainer = Utils.getOrCreateContainer(Constants.CASES_CONTAINER_NAME, capi);
        this.controlUnitsContainer = Utils.getOrCreateContainer(Constants.CONTROL_UNITS_CONTAINER_NAME, capi);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi);
        logger.debug("Connection established.");
    }

    @Override
    public void supply(Part part) throws ConnectionException {
        try{
            if(part.getPartType() == PartType.MOTOR){
                capi.write(motorsContainer, new Entry(part));
            }
            if(part.getPartType() == PartType.ROTOR){
                capi.write(rotorsContainer, new Entry(part));
            }
            if(part.getPartType() == PartType.CASE){
                capi.write(casesContainer, new Entry(part));
            }
            if(part.getPartType() == PartType.CONTROL_UNIT){
                capi.write(controlUnitsContainer, new Entry(part));
            }
        }
        catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void registerAssemblyRobot(IAssembledNotification assemblyRobotNotification) throws ConnectionException {
        try {
            capi.write(assembledNotifications, new Entry(new AssembledNotification(UUID.randomUUID())));
        } catch (MzsCoreException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    @Override
    public void moduleAssembled(Module module) throws ConnectionException {

    }

    @Override
    public void droneAssembled(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerCalibrationRobot(ICalibratedNotification calibrationRobotNotification) throws ConnectionException {

    }

    @Override
    public void motorRotorPairCalibrated(Module module) throws ConnectionException {

    }

    @Override
    public void droneCalibrated(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerLogisticRobot(ITestedNotification logisticRobotNotification) throws ConnectionException {

    }

    @Override
    public void droneTested(Drone drone) throws ConnectionException {

    }

    @Override
    public void end() throws ConnectionException {
        core.shutdown(true);
    }
}
