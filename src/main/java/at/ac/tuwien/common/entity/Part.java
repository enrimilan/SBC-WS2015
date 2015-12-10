package at.ac.tuwien.common.entity;

import java.io.Serializable;
import java.util.UUID;

public class Part implements Serializable {

    private UUID partId;
    private UUID supplierId;
    private PartType partType;

    public Part(UUID supplierId, PartType partType) {
        this.partId = UUID.randomUUID();
        this.supplierId = supplierId;
        this.partType = partType;
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

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", supplierId=" + supplierId +
                ", partType=" + partType +
                '}';
    }
}
