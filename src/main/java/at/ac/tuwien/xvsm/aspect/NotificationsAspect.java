package at.ac.tuwien.xvsm.aspect;

import at.ac.tuwien.common.notification.*;
import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.WriteEntriesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationsAspect extends AbstractContainerAspect {

    private static Logger logger = LoggerFactory.getLogger(NotificationsAspect.class);

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
