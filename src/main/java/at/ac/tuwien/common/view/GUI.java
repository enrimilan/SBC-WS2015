package at.ac.tuwien.common.view;

import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;;
import java.util.concurrent.CopyOnWriteArrayList;

public class GUI{

    private static Logger logger = LoggerFactory.getLogger(GUI.class);

    private Stage primaryStage;
    private IServer server;

    private ObservableList<Part> partsData = FXCollections.observableArrayList();
    private ObservableList<Drone> dronesData = FXCollections.observableArrayList();
    private ObservableList<Module> modulesData = FXCollections.observableArrayList();
    private ObservableList<Drone> goodDronesData = FXCollections.observableArrayList();
    private ObservableList<Drone> badDronesData = FXCollections.observableArrayList();

    public ObservableList<Part> getPartsData() {
        return partsData;
    }
    public ObservableList<Drone> getDronesData() {
        return dronesData;
    }

    public ObservableList<Module> getModulesData() {
        return modulesData;
    }

    public ObservableList<Drone> getGoodDronesData() {
        return goodDronesData;
    }

    public ObservableList<Drone> getBadDronesData() {
        return badDronesData;
    }

    public GUI(Stage primaryStage, IServer server) throws IOException {
        this.primaryStage = primaryStage;
        this.server = server;
        Updater u = new Updater();
        u.start();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showDroneDetailsDialog(Drone drone) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("DroneDetailsDialog.fxml"));
            Parent root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Drone Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Set the drone into the controller.
            DroneDetailsDialogController droneDetailsDialogController = loader.getController();
            droneDetailsDialogController.setDialogStage(dialogStage);
            droneDetailsDialogController.setDrone(drone);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Updater extends Thread {
        @Override
        public void run() {

                server.registerGUINotificationCallback(new INotificationCallback() {

                    @Override
                    public synchronized void onPartAdded(Part part) {
                        partsData.add(part);
                    }

                    @Override
                    public synchronized void onPartRemoved(Part part) {
                        partsData.remove(part);
                    }

                    @Override
                    public synchronized void onModuleAdded(Module module) {
                        modulesData.add(module);
                    }

                    @Override
                    public synchronized void onModuleRemoved(Module module) {
                        modulesData.remove(module);
                    }

                    @Override
                    public synchronized void onDroneAdded(Drone drone) {
                        dronesData.add(drone);
                    }

                    @Override
                    public synchronized void onDroneUpdated(Drone drone) {
                        int i = dronesData.indexOf(drone);
                        dronesData.set(i, drone);
                    }

                    @Override
                    public synchronized void onDroneRemoved(Drone drone) {
                        dronesData.remove(drone);
                    }

                    @Override
                    public synchronized void onGoodDroneTested(Drone drone) {
                        goodDronesData.add(drone);
                    }

                    @Override
                    public synchronized void onBadDroneTested(Drone drone) {
                        badDronesData.add(drone);
                    }

                });
            server.start();

        }
    }
}
