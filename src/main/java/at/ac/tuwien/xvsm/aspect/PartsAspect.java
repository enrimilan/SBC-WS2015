package at.ac.tuwien.xvsm.aspect;

import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.xvsm.XVSMServer;
import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.WriteEntriesRequest;

import java.util.List;

public class PartsAspect extends AbstractContainerAspect {

    private XVSMServer server;

    public PartsAspect(XVSMServer server) throws MzsCoreException {
        this.server = server;
    }

    @Override
    public AspectResult postWrite(WriteEntriesRequest request, Transaction tx, SubTransaction stx, Capi3AspectPort capi3, int executionCount) {
        List<Entry> entries = request.getEntries();
        try {
//            server.onNewPart(entries.get(0));
            server.supply(entries.get(0));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return AspectResult.OK;
    }
}
