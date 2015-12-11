package at.ac.tuwien.xvsm;

import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.robot.notification.AssembledNotification;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import at.ac.tuwien.xvsm.aspect.PartsAspect;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.ContainerIPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XVSMServer implements IServer {

    private final static Logger logger = LoggerFactory.getLogger(XVSMServer.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference motorsContainer, rotorsContainer, casesContainer, controlUnitsContainer;
    private ContainerReference assembledNotifications, calibratedNotifications, testedNotifications;
    private INotificationCallback notificationCallback;

    public XVSMServer(){

    }

    public void start() {

        //setup the space
        this.core = DefaultMzsCore.newInstance(Constants.SERVER_PORT);
        this.capi = new Capi(core);
        logger.debug("XVSMServer started");

        //create the containers
        this.motorsContainer = Utils.getOrCreateContainer(Constants.MOTORS_CONTAINER_NAME, capi);
        this.rotorsContainer = Utils.getOrCreateContainer(Constants.ROTORS_CONTAINER_NAME, capi);
        this.casesContainer = Utils.getOrCreateContainer(Constants.CASES_CONTAINER_NAME, capi);
        this.controlUnitsContainer = Utils.getOrCreateContainer(Constants.CONTROL_UNITS_CONTAINER_NAME, capi);
        this.assembledNotifications = Utils.getOrCreateContainer(Constants.ASSEMBLED_NOTIFICATIONS, capi);
        this.calibratedNotifications = Utils.getOrCreateContainer(Constants.CALIBRATED_NOTIFICATIONS, capi);
        this.testedNotifications = Utils.getOrCreateContainer(Constants.TESTED_NOTIFICATIONS, capi);
        try {
            PartsAspect partsAspect = new PartsAspect(this);
            capi.addContainerAspect(partsAspect, motorsContainer, ContainerIPoint.POST_WRITE);
            capi.addContainerAspect(partsAspect, rotorsContainer, ContainerIPoint.POST_WRITE);
            capi.addContainerAspect(partsAspect, casesContainer, ContainerIPoint.POST_WRITE);
            capi.addContainerAspect(partsAspect, controlUnitsContainer, ContainerIPoint.POST_WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        logger.debug("created containers");
    }

    public void onNewPart(Entry part) throws MzsCoreException {
        TransactionReference tx = capi.createTransaction(3000, null);
        logger.debug("GUI new part");
        List<Selector> notificationSelector = Arrays.asList(FifoCoordinator.newSelector(1));
        ArrayList<AssembledNotification> notifications = capi.take(assembledNotifications, notificationSelector,
                MzsConstants.RequestTimeout.DEFAULT, tx);
        if(!notifications.isEmpty()){
            List<Selector> selector = Arrays.asList(FifoCoordinator.newSelector(3), FifoCoordinator.newSelector(2),
                    FifoCoordinator.newSelector(1), FifoCoordinator.newSelector(0));
            ArrayList<Part> motors = capi.read(motorsContainer, selector, MzsConstants.RequestTimeout.DEFAULT, tx);
            ArrayList<Part> rotors = capi.read(rotorsContainer, selector, MzsConstants.RequestTimeout.DEFAULT, tx);
            if(!motors.isEmpty() && !rotors.isEmpty()){
                capi.commitTransaction(tx);
                notifications.get(0).assembleMotorRotorPairs(motors, rotors);
            }
            else{
                capi.rollbackTransaction(tx);
            }
        }
    }


    @Override
    public void registerGUINotificationCallback(INotificationCallback notificationCallback) {
        this.notificationCallback = notificationCallback;
    }

    @Override
    public void stop(){

    }
}
