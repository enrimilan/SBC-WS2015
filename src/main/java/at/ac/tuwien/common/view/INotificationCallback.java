package at.ac.tuwien.common.view;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Order;
import at.ac.tuwien.common.entity.Part;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An interface of a callback used to notify GUI for the changes happening in the back-logic
 */
public interface INotificationCallback {

    /**
     * Depending on the connection type (e.g. {@link at.ac.tuwien.rmi.RmiConnection} or {@link at.ac.tuwien.xvsm.XVSMConnection})
     * it sets the title of the main window
     * @param title of the windows containing the host and the port to where the connection has been established
     */
    void setTitle(String title);

    /**
     * Notifies GUI to add the <code>part</code> to the table containing supplied parts
     * @param part which has to be shown in GUI
     */
    void onPartAdded(Part part);

    /**
     * Notifies GUI to remove the <code>part</code> from the table containing supplied parts
     * @param part which has to be removed from GUI
     */
    void onPartRemoved(Part part);

    /**
     * Notifies GUI to refresh the table containing supplied parts with the given list of <code>parts</code>
     * @param parts to be presented on the table containing supplied parts
     */
    void setAllParts(ArrayList<Part> parts);

    /**
     * Notifies GUI to add the <code>module</code> to the table containing modules
     * @param module which has to be shown in GUI
     */
    void onModuleAdded(Module module);

    /**
     * Notifies GUI to remove the <code>module</code> from the table containing modules
     * @param module which has to be removed from GUI
     */
    void onModuleRemoved(Module module);

    /**
     * Notifies GUI to refresh the table containing modules with the given list of <code>modules</code>
     * @param modules to be presented on the table containing modules
     */
    void setAllModules(ArrayList<Module> modules);

    /**
     * Notifies GUI to add the <code>drone</code> to the table containing drones not yet tested
     * @param drone which has to be shown in GUI
     */
    void onDroneAdded(Drone drone);

    /**
     * Notifies GUI to update the information about the <code>drone</code> in the table containing drones which are not yet tested
     * @param drone information of which has to be updated in GUI
     */
    void onDroneUpdated(Drone drone);

    /**
     * Notifies GUI to remove the <code>drone</code> rom the table containing drones which are not yet tested
     * @param drone which has to be removed from GUI
     */
    void onDroneRemoved(Drone drone);

    /**
     * Notifies GUI to refresh the table containing drones with the given list of <code>drones</code> (not yet tested)
     * @param drones to be presented on the table containing drones not tested yet
     */
    void setAllDrones(ArrayList<Drone> drones);

    /**
     * Notifies GUI about the <code>drone</code> being tested positive (i.e. calibration sum values are within permissible range)
     * @param drone tested good
     */
    void onGoodDroneTested(Drone drone);
    /**
     * Notifies GUI about the <code>drone</code> being tested negative (i.e. calibration sum values are NOT within permissible range)
     * @param drone tested bad
     */
    void onBadDroneTested(Drone drone);


    /**
     * Notifies GUI when the status of the <code>order</code> has been changed (e.g. all ordered drones are ready manufactured)
     * @param order information of which has to be updated in GUI
     */
    void onOrderModified(Order order);
}
