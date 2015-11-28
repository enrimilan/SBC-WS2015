package at.ac.tuwien.entity;

import java.util.ArrayList;
import java.util.UUID;

public class CaseControlUnitPair {

    private Part casePart;
    private Part controlUnit;
    private UUID assemblerId;

    public CaseControlUnitPair(Part casePart, Part controlUnit, UUID assemblerId) {
        this.casePart = casePart;
        this.controlUnit = controlUnit;
        this.assemblerId = assemblerId;
    }

    public Part getCasePart() {
        return casePart;
    }

    public Part getControlUnit() {
        return controlUnit;
    }

    public UUID getAssemblerId() {
        return assemblerId;
    }

}
