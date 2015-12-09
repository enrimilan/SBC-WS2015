package at.ac.tuwien.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Drone implements Serializable {

    private UUID droneId;
    private Module caseControlUnitPair;
    private ArrayList<Module> motorRotorPairs;
    private UUID assemblerId;
    private Status status;

    public Drone(Module caseControlUnitPair, UUID assemblerId) {
        this.droneId = UUID.randomUUID();
        this.caseControlUnitPair = caseControlUnitPair;

        this.assemblerId = assemblerId;
        this.status = Status.ASSEMBLED;
    }

    public UUID getDroneId() {
        return droneId;
    }

    public void setDroneId(UUID droneId) {
        this.droneId = droneId;
    }

    public Module getCaseControlUnitPair(){
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

    public UUID getAssemblerId() {
        return assemblerId;
    }

    public void setAssemblerId(UUID assemblerId) {
        this.assemblerId = assemblerId;
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
