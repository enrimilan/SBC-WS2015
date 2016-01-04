package at.ac.tuwien.utils;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.CaseType;
import at.ac.tuwien.common.entity.Color;
import at.ac.tuwien.rmi.RmiConnection;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.xvsm.XVSMConnection;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);
    private static IConnection connection = null;
    private static final List<CaseType> CASE_TYPES = Collections.unmodifiableList(Arrays.asList(CaseType.NORMAL,
            CaseType.PACKAGE_HOLDER, CaseType.CAMERA_HOLDER));
    private static final List<Color> COLORS = Collections.unmodifiableList(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE));
    private static final Random RANDOM = new Random();

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

    public static CaseType generateRandomCaseType(){
        return CASE_TYPES.get(ThreadLocalRandom.current().nextInt(3));
    }

    public static Color generateRandomColor(){
        return COLORS.get(ThreadLocalRandom.current().nextInt(3));
    }

    public static IConnection getConnectionInstance(String type){

        if(type.equals("rmi") && connection == null){
            connection = new RmiConnection();
            logger.debug("new rmi connection");
        }
        else if(type.equals("xvsm") && connection == null){
            connection = new XVSMConnection();
            logger.debug("new xvsm connection");
        }

        return connection;
    }

    public static IConnection getConnectionInstance(){
        return connection;
    }


    public static ContainerReference getOrCreateContainer(String containerName, Capi capi,
                                                          List<Coordinator> obligatoryCoords, String host, int port) {
        ContainerReference cref = null;
        URI spaceUri = getSpaceUri(host, port);
        try {
            cref = capi.lookupContainer(containerName, spaceUri, MzsConstants.RequestTimeout.DEFAULT, null);
            logger.debug("created container: "+ containerName);
        } catch (MzsCoreException e) {
            // Container doesn't exist, so create a new one
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
