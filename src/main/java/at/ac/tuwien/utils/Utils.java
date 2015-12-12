package at.ac.tuwien.utils;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.rmi.RmiConnection;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.xvsm.XVSMConnection;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;

public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);
    private static IConnection connection = null;

    public static PartType getPartType(String id){
        if(id.equals("A")){
            return PartType.CASE;
        }
        else if(id.equals("B")){
            return PartType.CONTROL_UNIT;
        }
        else if(id.equals("C")){
            return PartType.MOTOR;
        }
        else if(id.equals("D")){
            return PartType.ROTOR;
        }
        else{
            return null;
        }
    }

    public static IConnection getConnectionInstance(String type){

        if(type.equals("rmi") && connection == null){
            connection = new RmiConnection();
        }
        else if(type.equals("xvsm") && connection == null){
            connection = new XVSMConnection();
        }

        return connection;
    }

    public static IConnection getConnectionInstance(){
        return connection;
    }


    public static ContainerReference getOrCreateContainer(String containerName, Capi capi, ArrayList<Coordinator> obligatoryCoords) {
        ContainerReference cref = null;
        URI spaceUri = getSpaceUri(Constants.SERVER_HOST, Constants.SERVER_PORT);
        try {
            cref = capi.lookupContainer(containerName, spaceUri, MzsConstants.RequestTimeout.DEFAULT, null);
            logger.debug("created container: "+ containerName);
        } catch (MzsCoreException e) {
            // Container doesn't exist, so create a new one
//            ArrayList<Coordinator> obligatoryCoords = new ArrayList<>();
//            obligatoryCoords.add(new QueryCoordinator());
//            obligatoryCoords.add(new FifoCoordinator());
            try {
                logger.debug("Container " + containerName + " not found. Creating a new one");
                cref = capi.createContainer(containerName, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
            } catch (MzsCoreException e1) {
                logger.debug(e.getMessage());
            }
        }
        return cref;
    }

    private static URI getSpaceUri(String host, int port) {
        return URI.create("xvsm://" + host + ":" + port);
    }

}
