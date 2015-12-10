package at.ac.tuwien.xvsm;

import at.ac.tuwien.utils.Constants;
import at.ac.tuwien.utils.Utils;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private MzsCore core;
    private Capi capi;
    private ContainerReference motorsContainer, rotorsContainer, casesContainer, controlUnitsContainer;

    public Server(){

    }

    public void start() throws MzsCoreException {

        //setup the space
        this.core = DefaultMzsCore.newInstance(Constants.SERVER_PORT);
        this.capi = new Capi(core);
        logger.debug("Server started");

        //create the containers
        this.motorsContainer = Utils.getOrCreateContainer(Constants.MOTORS_CONTAINER_NAME, capi);
        this.rotorsContainer = Utils.getOrCreateContainer(Constants.ROTORS_CONTAINER_NAME, capi);
        this.casesContainer = Utils.getOrCreateContainer(Constants.CASES_CONTAINER_NAME, capi);
        this.controlUnitsContainer = Utils.getOrCreateContainer(Constants.CONTROL_UNITS_CONTAINER_NAME, capi);
        logger.debug("created containers");
    }

    public void stop() throws MzsCoreException {
        capi.destroyContainer(motorsContainer, null);
        capi.destroyContainer(rotorsContainer, null);
        capi.destroyContainer(casesContainer, null);
        capi.destroyContainer(controlUnitsContainer, null);
        core.shutdown(true);
    }
}
