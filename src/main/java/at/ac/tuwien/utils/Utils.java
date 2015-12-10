package at.ac.tuwien.utils;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.rmi.RmiConnection;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.xvsm.XVSMConnection;

public class Utils {

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

    public static IConnection getConnection(String type){
        if(type.equals("rmi")){
            return new RmiConnection();
        }
        else{
            return new XVSMConnection();
        }
    }
}
