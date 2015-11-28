package at.ac.tuwien.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Drone {

    private CaseControlUnitPair caseControlUnitPair;
    private ArrayList<MotorRotorPair> motorRotorPairs;
    private UUID assemblerId;

    public Drone(CaseControlUnitPair caseControlUnitPair, UUID assemblerId) {
        this.caseControlUnitPair = caseControlUnitPair;
        this.assemblerId = assemblerId;
    }

    public CaseControlUnitPair getCaseControlUnitPairs(){
        return caseControlUnitPair;
    }

    public ArrayList<MotorRotorPair> getMotorRotorPairs() {
        return motorRotorPairs;
    }

    public void setMotorRotorPairs(ArrayList<MotorRotorPair> motorRotorPairs) {
        this.motorRotorPairs = motorRotorPairs;
    }
}
