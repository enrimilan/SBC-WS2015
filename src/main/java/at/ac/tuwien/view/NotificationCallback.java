package at.ac.tuwien.view;

import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Part;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Enri on 12/8/2015.
 */
public interface NotificationCallback {

    void supplyNotifier(Part part);
    void droneNotifier(CopyOnWriteArrayList<Drone> drones);
    void testGoodDroneNotifier(CopyOnWriteArrayList<Drone> drones);
    void testBadDroneNotifier(CopyOnWriteArrayList<Drone> drones);
}
