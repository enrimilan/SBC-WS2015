package at.ac.tuwien.common.entity;

import java.io.Serializable;
import java.util.UUID;

public class Order implements Serializable {

    private UUID orderId;
    private int orderSize;
    private CaseType caseType;
    private Color droneColor;

    public Order(Integer orderSize, CaseType caseType, Color droneColor){
        this.orderId = UUID.randomUUID();
        this.orderSize = orderSize;
        this.caseType = caseType;
        this.droneColor = droneColor;
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

    @Override
    public String toString() {
        return "Order [orderId=" + orderId.toString() + ", orderSize=" + orderSize
                + ", caseType=" + caseType + ", droneColor=" + droneColor + "]";
    }

}
