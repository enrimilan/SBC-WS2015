package at.ac.tuwien.entity;

import java.util.UUID;

public class MotorRotorPair {

    private Part motor;
    private Part rotor;
    private UUID assemblerId;

    public MotorRotorPair(Part motor, Part rotor, UUID assemblerId) {
        this.motor = motor;
        this.rotor = rotor;
        this.assemblerId = assemblerId;
    }

    public Part getMotor() {
        return motor;
    }

    public Part getRotor() {
        return rotor;
    }

    public UUID getAssemblerId(){
        return assemblerId;
    }

}
