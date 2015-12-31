package at.ac.tuwien.common.view;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public interface INotificationCallback {

    void setTitle(String title);

    void onPartAdded(Part part);
    void onPartRemoved(Part part);
    void setAllParts(ArrayList<Part> parts);

    void onModuleAdded(Module module);
    void onModuleRemoved(Module module);
    void setAllModules(ArrayList<Module> modules);

    void onDroneAdded(Drone drone);
    void onDroneUpdated(Drone drone);
    void onDroneRemoved(Drone drone);
    void setAllDrones(ArrayList<Drone> drones);

    void onGoodDroneTested(Drone drone);
    void onBadDroneTested(Drone drone);
}
