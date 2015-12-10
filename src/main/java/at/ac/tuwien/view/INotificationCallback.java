package at.ac.tuwien.view;

import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Module;
import at.ac.tuwien.entity.Part;

import java.util.concurrent.CopyOnWriteArrayList;

public interface INotificationCallback {

    void supplyNotifier(CopyOnWriteArrayList<Part> cases, CopyOnWriteArrayList<Part> controlUnits, CopyOnWriteArrayList<Part>  motors, CopyOnWriteArrayList<Part>  rotors );
    void droneNotifier(CopyOnWriteArrayList<Drone> drones);
    void testGoodDroneNotifier(CopyOnWriteArrayList<Drone> drones);
    void testBadDroneNotifier(CopyOnWriteArrayList<Drone> drones);

    void modulesNotifier(CopyOnWriteArrayList<Module> mr, CopyOnWriteArrayList<Module> cu);
}
