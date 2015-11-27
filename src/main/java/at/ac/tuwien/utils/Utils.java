package at.ac.tuwien.utils;

import at.ac.tuwien.entity.PartType;

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
}
