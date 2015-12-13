package at.ac.tuwien.xvsm.aspect;

import at.ac.tuwien.common.notification.*;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.xvsm.XVSMServer;
import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.WriteEntriesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationsAspect extends AbstractContainerAspect {

    private static Logger logger = LoggerFactory.getLogger(NotificationsAspect.class);

    private XVSMServer server;
    private INotificationCallback notificationCallback;

    public NotificationsAspect(XVSMServer server, INotificationCallback notificationCallback) throws MzsCoreException {
        this.server = server;
        this.notificationCallback = notificationCallback;
    }

    @Override
    public AspectResult postWrite(WriteEntriesRequest request, Transaction tx, SubTransaction stx, Capi3AspectPort capi3, int executionCount) {
        Object entry = request.getEntries().get(0).getValue();

        if(entry instanceof IAssembledNotification){
            logger.debug("assembler joined and is ready to do some work");
        }
        if(entry instanceof ICalibratedNotification){
            logger.debug("calibrator joined and is ready to do some work");
        }
        if(entry instanceof ITestedNotification){
            logger.debug("tester joined and is ready to do some work");
        }

        return AspectResult.OK;
    }
}
