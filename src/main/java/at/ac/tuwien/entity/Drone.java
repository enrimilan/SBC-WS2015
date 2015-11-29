package at.ac.tuwien.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Drone implements Serializable {

    private Module caseControlUnitPair;
    private ArrayList<Module> motorRotorPairs;
    private UUID assemblerId;
    private Status status;

    public Drone(Module caseControlUnitPair, UUID assemblerId) {
        this.caseControlUnitPair = caseControlUnitPair;
        this.assemblerId = assemblerId;
        this.status = Status.ASSEMBLED;
    }

    public Module getCaseControlUnitPairs(){
        return caseControlUnitPair;
    }

    public ArrayList<Module> getMotorRotorPairs() {
        return motorRotorPairs;
    }

    public void setMotorRotorPairs(ArrayList<Module> motorRotorPairs) {
        this.motorRotorPairs = motorRotorPairs;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "caseControlUnitPair=" + caseControlUnitPair +
                ", motorRotorPairs=" + motorRotorPairs +
                ", assemblerId=" + assemblerId +
                ", status=" + status +
                '}';
    }
}
