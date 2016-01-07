package at.ac.tuwien.common.entity;

import java.io.Serializable;
import java.util.UUID;

public class Order implements Serializable {

    private UUID orderId;
    private int orderSize;
    private CaseType caseType;
    private Color droneColor;
    private int nrOfPaintPartRequests;
    private int nrOfAssembleCaseControlUnitPairRequests;
    private int nrOfAssembleMotorRotorPairRequests;
    private int nrOfAssembleDronesRequests;
    private int nrOfTestRequests;

    public Order(Integer orderSize, CaseType caseType, Color droneColor){
        this.orderId = UUID.randomUUID();
        this.orderSize = orderSize;
        this.caseType = caseType;
        this.droneColor = droneColor;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(int orderSize) {
        this.orderSize = orderSize;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(CaseType caseType) {
        this.caseType = caseType;
    }

    public Color getDroneColor() {
        return droneColor;
    }

    public void setDroneColor(Color droneColor) {
        this.droneColor = droneColor;
    }

    public int getNrOfPaintPartRequests() {
        return nrOfPaintPartRequests;
    }

    public void setNrOfPaintPartRequests(int nrOfPaintPartRequests) {
        this.nrOfPaintPartRequests = nrOfPaintPartRequests;
    }

    public int getNrOfAssembleCaseControlUnitPairRequests() {
        return nrOfAssembleCaseControlUnitPairRequests;
    }

    public void setNrOfAssembleCaseControlUnitPairRequests(int nrOfAssembleCaseControlUnitPairRequests) {
        this.nrOfAssembleCaseControlUnitPairRequests = nrOfAssembleCaseControlUnitPairRequests;
    }

    public int getNrOfAssembleMotorRotorPairRequests() {
        return nrOfAssembleMotorRotorPairRequests;
    }

    public void setNrOfAssembleMotorRotorPairRequests(int nrOfAssembleMotorRotorPairRequests) {
        this.nrOfAssembleMotorRotorPairRequests = nrOfAssembleMotorRotorPairRequests;
    }

    public int getNrOfAssembleDronesRequests() {
        return nrOfAssembleDronesRequests;
    }

    public void setNrOfAssembleDronesRequests(int nrOfAssembleDronesRequests) {
        this.nrOfAssembleDronesRequests = nrOfAssembleDronesRequests;
    }

    public int getNrOfTestRequests() {
        return nrOfTestRequests;
    }

    public void setNrOfTestRequests(int nrOfTestRequests) {
        this.nrOfTestRequests = nrOfTestRequests;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderSize=" + orderSize +
                ", caseType=" + caseType +
                ", droneColor=" + droneColor +
                ", nrOfPaintPartRequests=" + nrOfPaintPartRequests +
                ", nrOfAssembleCaseControlUnitPairRequests=" + nrOfAssembleCaseControlUnitPairRequests +
                ", nrOfAssembleDronesRequests=" + nrOfAssembleDronesRequests +
                ", nrOfTestRequests=" + nrOfTestRequests +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return orderId.equals(order.orderId);

    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}
