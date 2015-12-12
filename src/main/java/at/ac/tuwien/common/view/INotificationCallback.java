package at.ac.tuwien.common.view;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.util.concurrent.CopyOnWriteArrayList;

public interface INotificationCallback {

    void onPartAdded(Part part);
    void onPartRemoved(Part part);

    void onModuleAdded(Module module);
    void onModuleRemoved(Module module);

    void onDroneAdded(Drone drone);
    void onDroneRemoved(Drone drone);

    void onGoodDroneTested(Drone drone);
    void onBadDroneTested(Drone drone);
}
