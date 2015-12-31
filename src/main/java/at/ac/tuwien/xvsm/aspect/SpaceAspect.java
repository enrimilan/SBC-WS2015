package at.ac.tuwien.xvsm.aspect;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.view.INotificationCallback;
import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.aspects.AbstractSpaceAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.RollbackTransactionRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpaceAspect extends AbstractSpaceAspect {

    private Capi capi;
    private INotificationCallback notificationCallback;
    private ContainerReference partsContainer, modulesContainer, dronesContainer;

    public SpaceAspect(Capi capi, INotificationCallback notificationCallback){
        this.capi = capi;
        this.notificationCallback = notificationCallback;
        String host = capi.getCore().getConfig().getSpaceUri().getHost();
        int port = capi.getCore().getConfig().getSpaceUri().getPort();
        List<Coordinator> coordinators = new ArrayList<>();
        coordinators.add(new QueryCoordinator());
        coordinators.add(new FifoCoordinator());
        this.partsContainer = Utils.getOrCreateContainer(Constants.PARTS_CONTAINER, capi, coordinators, host, port);
        this.modulesContainer = Utils.getOrCreateContainer(Constants.MODULES_CONTAINER, capi, coordinators, host, port);
        this.dronesContainer = Utils.getOrCreateContainer(Constants.DRONES_CONTAINER, capi, coordinators, host, port);
    }

    @Override
    public AspectResult postRollbackTransaction(RollbackTransactionRequest request, Transaction tx) {

        Collection<LocalContainerReference> containers = tx.getAccessedContainers();

        for(LocalContainerReference l : containers){
            if(partsContainer.getId().equals(l.getId())){
                try {
                    ArrayList<Part> parts = capi.read(
                            partsContainer,
                            FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
                            MzsConstants.RequestTimeout.DEFAULT,
                            null);
                    notificationCallback.setAllParts(parts);
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
            }
            else if(modulesContainer.getId().equals(l.getId())){
                try {
                    ArrayList<Module> modules = capi.read(
                            modulesContainer,
                            FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
                            MzsConstants.RequestTimeout.DEFAULT,
                            null);
                    notificationCallback.setAllModules(modules);
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
            }
            else if(partsContainer.getId().equals(l.getId())){
                try {
                    ArrayList<Drone> drones = capi.read(
                            dronesContainer,
                            FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),
                            MzsConstants.RequestTimeout.DEFAULT,
                            null);
                    notificationCallback.setAllDrones(drones);
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
            }
        }

        return AspectResult.OK;
    }
}
