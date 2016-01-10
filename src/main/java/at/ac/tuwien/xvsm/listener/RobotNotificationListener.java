package at.ac.tuwien.xvsm.listener;

import at.ac.tuwien.common.notification.IAssembledNotification;
import at.ac.tuwien.common.notification.ICalibratedNotification;
import at.ac.tuwien.common.notification.IPaintedNotification;
import at.ac.tuwien.common.notification.ITestedNotification;
import at.ac.tuwien.xvsm.XVSMServer;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class RobotNotificationListener implements NotificationListener {

    private final static Logger logger = LoggerFactory.getLogger(RobotNotificationListener.class);
    private XVSMServer server;

    public RobotNotificationListener(XVSMServer server) {
        this.server = server;
    }

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> list) {
        Entry entry = (Entry) list.get(0);

        if(entry.getValue() instanceof IPaintedNotification){
            logger.info("painting robot just joined and is ready to do some work");
            server.checkForWorkWithPartForPaintingRobot();
        }
        if(entry.getValue() instanceof IAssembledNotification){
            logger.info("assembler robot just joined and is ready to do some work");
            server.checkForWorkWithModulesForAssemblyRobot();
            server.checkForWorkWithPartsForAssemblyRobot();
        }
        if(entry.getValue() instanceof ICalibratedNotification){
            logger.info("calibrator robot just joined and is ready to do some work");
            server.checkForWorkWithDroneForCalibrationRobot();
            server.checkForWorkWithMotorRotorPairForCalibrationRobot();
        }
        if(entry.getValue() instanceof ITestedNotification){
            logger.info("tester robot just joined and is ready to do some work");
            server.checkForWorkWithDroneForLogisticRobot();
        }
    }
}
