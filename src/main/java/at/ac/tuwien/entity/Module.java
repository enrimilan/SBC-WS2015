package at.ac.tuwien.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Module implements Serializable {

    private UUID assemblerId;
    private UUID calibratorId;
    private ModuleType moduleType;
    private ArrayList<Part> parts;

    public Module(ModuleType moduleType, UUID assemblerId) {
        this.moduleType = moduleType;
        this.assemblerId = assemblerId;
        this.parts = new ArrayList<Part>();
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

    @Override
    public String toString() {
        return "Module{" +
                "assemblerId=" + assemblerId +
                ", calibratorId=" + calibratorId +
                ", moduleType=" + moduleType +
                ", parts=" + parts +
                '}';
    }
}
