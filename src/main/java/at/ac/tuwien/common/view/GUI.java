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
        server.start();
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
                    public void supplyNotifier(CopyOnWriteArrayList<Part> cases, CopyOnWriteArrayList<Part> controlUnits, CopyOnWriteArrayList<Part> motors, CopyOnWriteArrayList<Part> rotors) {
                        CopyOnWriteArrayList<Part> allParts = new CopyOnWriteArrayList<Part>();
                        allParts.addAll(cases);
                        allParts.addAll(controlUnits);
                        allParts.addAll(motors);
                        allParts.addAll(rotors);
                        partsData.setAll(allParts);
                    }

                    @Override
                    public void droneNotifier(CopyOnWriteArrayList<Drone> drones) {
                        dronesData.setAll(drones);
                    }

                    @Override
                    public void testGoodDroneNotifier(CopyOnWriteArrayList<Drone> drones) {
                        goodDronesData.setAll(drones);
                    }

                    @Override
                    public void testBadDroneNotifier(CopyOnWriteArrayList<Drone> drones) {
                        badDronesData.setAll(drones);
                    }

                    @Override
                    public void modulesNotifier(CopyOnWriteArrayList<Module> cu, CopyOnWriteArrayList<Module> mr) {
                        CopyOnWriteArrayList<Module> allModules = new CopyOnWriteArrayList<Module>();
                        allModules.addAll(mr);
                        allModules.addAll(cu);
                        modulesData.setAll(allModules);
                    }

                });

        }
    }
}
