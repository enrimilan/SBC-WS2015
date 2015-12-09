package at.ac.tuwien;

import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.server.Server;
import at.ac.tuwien.view.DroneDetailsDialogController;
import at.ac.tuwien.view.NotificationCallback;
import at.ac.tuwien.view.OverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private Stage primaryStage;
    private ObservableList<Part> partsData = FXCollections.observableArrayList();
    private ObservableList<Drone> dronesData = FXCollections.observableArrayList();
    private ObservableList<Drone> assembledData = FXCollections.observableArrayList();

    private ObservableList<Drone> goodDronesData = FXCollections.observableArrayList();
    private ObservableList<Drone> badDronesData = FXCollections.observableArrayList();

    private Server s ;
    public ObservableList<Part> getPartsData() {
        return partsData;
    }
    public ObservableList<Drone> getDronesData() {
        return dronesData;
    }

    public ObservableList<Drone> getGoodDronesData() {
        return goodDronesData;
    }

    public ObservableList<Drone> getBadDronesData() {
        return badDronesData;
    }

    public ObservableList<Drone> getAssembledData() {
        return assembledData;
    }


    public static void main(String[] args) {
        logger.info("Application started.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("OutputOverview.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Drone Factory");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> System.exit(0));

        OverviewController controller = loader.getController();
        controller.setMain(this);

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
            try {
                s =   new Server();
                s.registerNotificatioCallback(new NotificationCallback() {
                    @Override
                    public void supplyNotifier(Part part) {
                        partsData.add(part);
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

                });
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }
        }
    }
}
