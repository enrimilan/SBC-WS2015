package at.ac.tuwien;

import at.ac.tuwien.xvsm.Server;
import org.mozartspaces.core.MzsCoreException;

public class MainXVSM {

    public static void main(String[] args){
        Server server = new Server();
        try {
            server.start();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }
}
