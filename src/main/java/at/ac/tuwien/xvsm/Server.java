package at.ac.tuwien.xvsm;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private final static int PORT = 4444;
    private MzsCore core;
    private Capi capi;

    public Server(){

    }

    public void start(){
        this.core = DefaultMzsCore.newInstance(PORT);
        this.capi = new Capi(core);
        logger.debug("Server started");
    }
}
