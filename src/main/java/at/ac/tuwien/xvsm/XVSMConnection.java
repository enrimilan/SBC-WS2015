package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.common.robot.IAssemblyRobotNotification;
import at.ac.tuwien.common.robot.ICalibrationRobotNotification;
import at.ac.tuwien.common.robot.ILogisticRobotNotification;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XVSMConnection implements IConnection {

    private final static Logger logger = LoggerFactory.getLogger(XVSMConnection.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference motorsContainer, rotorsContainer, casesContainer, controlUnitsContainer;

    @Override
    public void establish() throws ConnectionException {
        this.core = DefaultMzsCore.newInstance();
        this.capi = new Capi(core);
        this.motorsContainer = Utils.getOrCreateContainer(Constants.MOTORS_CONTAINER_NAME, capi);
        this.rotorsContainer = Utils.getOrCreateContainer(Constants.ROTORS_CONTAINER_NAME, capi);
        this.casesContainer = Utils.getOrCreateContainer(Constants.CASES_CONTAINER_NAME, capi);
        this.controlUnitsContainer = Utils.getOrCreateContainer(Constants.CONTROL_UNITS_CONTAINER_NAME, capi);
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
    public void registerAssemblyRobot(IAssemblyRobotNotification assemblyRobotNotification) throws ConnectionException {

    }

    @Override
    public void moduleAssembled(Module module) throws ConnectionException {

    }

    @Override
    public void droneAssembled(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerCalibrationRobot(ICalibrationRobotNotification calibrationRobotNotification) throws ConnectionException {

    }

    @Override
    public void motorRotorPairCalibrated(Module module) throws ConnectionException {

    }

    @Override
    public void droneCalibrated(Drone drone) throws ConnectionException {

    }

    @Override
    public void registerLogisticRobot(ILogisticRobotNotification logisticRobotNotification) throws ConnectionException {

    }

    @Override
    public void droneTested(Drone drone) throws ConnectionException {

    }

    @Override
    public void end() throws ConnectionException {
        core.shutdown(true);
    }
}
