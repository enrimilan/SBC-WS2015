package at.ac.tuwien.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Module implements Serializable {

    private UUID assemblerId;
    private UUID calibratorId;
    private ModuleType moduleType;
    private ArrayList<Part> parts;
    private Status status;
    private int calibrationValue;
    private UUID id;

    public Module(ModuleType moduleType, UUID assemblerId) {
        this.id = UUID.randomUUID();
        this.moduleType = moduleType;
        this.assemblerId = assemblerId;
        this.parts = new ArrayList<Part>();
        this.status = Status.ASSEMBLED;
    }

    public void addPart(Part part){
        parts.add(part);
    }

    public UUID getAssemblerId() {
        return assemblerId;
    }

    public UUID getCalibratorId() {
        return calibratorId;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public void setCalibratorId(UUID calibratorId) {
        this.calibratorId = calibratorId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getCalibrationValue() {
        return calibrationValue;
    }

    public void setCalibrationValue(int calibrationValue) {
        this.calibrationValue = calibrationValue;
    }

    public UUID getId(){
        return id;
    }

    @Override
    public String toString() {
        return "Module{" +
                "assemblerId=" + assemblerId +
                ", calibratorId=" + calibratorId +
                ", moduleType=" + moduleType +
                ", parts=" + parts +
                ", status=" + status +
                ", calibrationValue=" + calibrationValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        return id.equals(module.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
