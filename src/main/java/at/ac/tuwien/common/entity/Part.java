package at.ac.tuwien.common.entity;

import java.io.Serializable;
import java.util.UUID;

public class Part implements Serializable {

    private UUID partId;
    private UUID supplierId;
    private UUID painterId;
    private UUID orderId;
    private PartType partType;
    private CaseType caseType;


    private Color color;

    public Part(UUID supplierId, PartType partType) {
        this.partId = UUID.randomUUID();
        this.supplierId = supplierId;
        this.partType = partType;
        this.color = Color.GRAY;
    }

    public UUID getPartId() {
        return partId;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public PartType getPartType() {
        return partType;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setCaseType(CaseType caseType) {
        this.caseType = caseType;
    }

    public Color getColor() {
        return color;
    }

    public Color getCaseColor() {
        if (partType == PartType.CASE){
            return color;
        } else return null;
    }

    public String getPartWithType(){
        if (partType == PartType.CASE)
            return partType.name() + " - " + caseType.name() + " :" + getCaseColor();
       return partType.name();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPainterId(UUID painterId) {
        this.painterId = painterId;
    }

    public UUID getPainterId() {
        return painterId;
    }

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", supplierId=" + supplierId +
                ", painterId=" + painterId +
                ", orderId=" + orderId +
                ", partType=" + partType +
                ", caseType=" + caseType +
                ", color=" + color +
                '}';
    }
}
