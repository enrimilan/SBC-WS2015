package at.ac.tuwien.xvsm;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final static int PORT = 4444;
    private final static String PART_CONTAINER_NAME = "parts";
    private MzsCore core;
    private Capi capi;
    private ContainerReference partsContainer;

    public Server(){

    }

    public void start() throws MzsCoreException {

        //setup the space
        this.core = DefaultMzsCore.newInstance(PORT);
        this.capi = new Capi(core);
        logger.debug("Server started");

        //create the containers
        this.partsContainer = capi.createContainer(
                PART_CONTAINER_NAME,
                null,
                MzsConstants.Container.UNBOUNDED,
                Arrays.asList(new FifoCoordinator()),
                null,
                null);
    }

    public void stop() throws MzsCoreException {
        capi.destroyContainer(partsContainer, null);
        core.shutdown(true);
    }
}
