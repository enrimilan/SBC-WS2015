package at.ac.tuwien.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Arber on 08.12.2015.
 */
public class PartG {


        private  ObjectProperty<UUID> partId;
        private  ObjectProperty<UUID> supplierId;
        private  ObjectProperty<PartType> partType;

        public PartG(Part part){
            this.partId = new SimpleObjectProperty<UUID>(part.getPartId());
            this.supplierId = new SimpleObjectProperty<UUID>(part.getSupplierId());
            this.partType = new SimpleObjectProperty<PartType>(part.getPartType());
        }

        public PartG(UUID supplierId, PartType partType) {
            this.partId = new SimpleObjectProperty<UUID>(UUID.randomUUID());
            this.supplierId = new SimpleObjectProperty<UUID>(supplierId);
            this.partType = new SimpleObjectProperty<PartType>(partType);
        }

        public UUID getPartId() {
            return partId.get();
        }

        public ObjectProperty<UUID> partIdProperty() {
            return partId;
        }

        public void setPartId(UUID partId) {
            this.partId.set(partId);
        }

        public UUID getSupplierId() {
            return supplierId.get();
        }

        public ObjectProperty<UUID> supplierIdProperty() {
            return supplierId;
        }

        public void setSupplierId(UUID supplierId) {
            this.supplierId.set(supplierId);
        }

        public PartType getPartType() {
            return partType.get();
        }

        public ObjectProperty<PartType> partTypeProperty() {
            return partType;
        }

        public void setPartType(PartType partType) {
            this.partType.set(partType);
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
